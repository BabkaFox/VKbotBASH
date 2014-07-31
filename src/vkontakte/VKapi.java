package vkontakte;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

public class VKapi {

    private String client_id = "4458250";           //id вашего приложения в вк
    private String scope = "status,messages";       //права, которые хотим получить
    private String redirect_uri = "http://oauth.vk.com/blank.html";
    private String display = "page";
    private String response_type = "token";
    private int userID = 37653522;  //id страницы пользователя
    private String access_token;    //сюда сохраняем токен
    public String email;  //логин получаем из формы
    public String pass ;  //пароль получаем из формы
    public boolean status;


    HttpClient httpClient = new DefaultHttpClient();

    public void setConnection() throws IOException, URISyntaxException {



        // Делаем первый запрос
        HttpPost post = new HttpPost("http://oauth.vk.com/authorize?" +
                "client_id="+client_id+
                "&scope="+scope+
                "&redirect_uri="+redirect_uri+
                "&display="+display+
                "&response_type="+response_type);
        HttpResponse response;
        response = httpClient.execute(post);
        post.abort();


        //Для запроса авторизации необходимо два параметра полученных в первом запросе
        //ip_h и to_h

        String post_string = converHttpEntityToString(response.getEntity());
        String ip_h = findKey(post_string, "name=\"ip_h\" value=\"", "\"");
        String to = findKey(post_string, "name=\"to\" value=\"", "\"");


        // Делаем запрос авторизации
        post = new HttpPost("https://login.vk.com/?act=login&soft=1"+
                "&q=1"+
                "&ip_h="+ip_h+
                "&from_host=oauth.vk.com"+
                "&to="+to+
                "&expire=0"+
                "&email="+email+
                "&pass="+pass);
        response = httpClient.execute(post);
        post.abort();

        // Получили редирект на подтверждение требований приложения
        String HeaderLocation = response.getFirstHeader("location").getValue();
        post = new HttpPost(HeaderLocation);

        // Проходим по нему
        response = httpClient.execute(post);
        post.abort();

        // Теперь последний редирект на получение токена
        try {
            HeaderLocation = response.getFirstHeader("location").getValue();

        }catch (NullPointerException e){
            post_string = converHttpEntityToString(response.getEntity());
            HeaderLocation = findKey(post_string, "method=\"post\" action=\"", "\"");  //нажимаем кнопку подтверждения -_- не смог сделать иначе Т_Т

        }
        // Проходим по нему
        post = new HttpPost(HeaderLocation);
        response = httpClient.execute(post);
        post.abort();

        // Теперь в след редиректе необходимый токен
        HeaderLocation = response.getFirstHeader("location").getValue();
        // Просто спарсим его сплитами
        access_token = HeaderLocation.split("#")[1].split("&")[0].split("=")[1];
        status = true;
    }

    public String getStatus() throws IOException {      //С помощью этого метода можно получить статус со страницы
        String stat = "Пустой статус";
        HttpPost post = new HttpPost("https://api.vk.com/method/status.get?user_id=" + userID + "&v=5.23&access_token=" + access_token);
        HttpResponse response = httpClient.execute(post);
        post.abort();
        stat = converHttpEntityToString(response.getEntity());
        return stat;
    }

    public String setStatus(String text, long day, long hours, long minutes, long seconds) throws IOException { //С помощью этого метода устанавливаем статус на страницу

        HttpPost post = new HttpPost("https://api.vk.com/method/status.set?text=" + text + "%20" + day + "%20" + hours + ":" + minutes + ":" + seconds + "&v=5.23&access_token=" + access_token);
        HttpResponse response = httpClient.execute(post);
        post.abort();
        String answ = converHttpEntityToString(response.getEntity());

        return answ;
    }

    public String sendMesaage(String text) throws IOException{  //Метод для отправки сообщения

        for (int i = 0; i < text.length(); i++) {                                                //убираем из текста пробелы, заменяем на %20
            if (text.charAt(i) == ' '){
                text = text.substring(0,i) + "%20" + text.substring(i+1);
            }
        }
        for (int i = 0; i < text.length(); i++) {                                                //убираем из текста ", заменяем на '
            if (text.charAt(i) == '\"'){
                text = text.substring(0,i) + "'" + text.substring(i+1);
            }
        }

        HttpPost post = new HttpPost("https://api.vk.com/method/messages.send?user_id=37653522&message=" + text + "&v=5.23&access_token=" + access_token);
        HttpResponse response = httpClient.execute(post);
        post.abort();

        String answ = converHttpEntityToString(response.getEntity());

        return answ;  //в ответ получаем id сообщения
    }


    private String converHttpEntityToString(HttpEntity ent) {            //А вот это уже взято из интернетов. Работает и хрен с ним. Для парсинга всей сраницы
        BufferedInputStream bis;
        StringBuilder sb = new StringBuilder();
        try {
            bis = new BufferedInputStream(ent.getContent());
            byte[] buffer = new byte[1024];
            int count;
            while ((count = bis.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, count, "utf-8"));
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private String findKey(String source, String patternbegin, String patternend) { //Для поиска нужных элементов на спарсенной странице. Тоже не моё
        int startkey = source.indexOf(patternbegin);
        if (startkey > -1) {
            int stopkey = source.indexOf(patternend,
                    startkey + patternbegin.length());
            if (stopkey > -1) {
                String key = source.substring(startkey + patternbegin.length(),
                        stopkey);
                return key;
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException, URISyntaxException, AWTException, InterruptedException, NoSuchAlgorithmException {

    }
}
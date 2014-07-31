package vkontakte;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Random;

/**
 * Created by max on 31.07.2014.
 */
public class BashParser {
    public String getQuote() throws IOException {   //Метод для получение рандомной цитаты с первой страницы баша
        String quote = "Цитаты нет :(";
        Document doc = Jsoup.connect("http://bash.im/").get();
        Elements quotes = doc.getElementsByClass("quote");
        Random rand = new Random();
        int n = rand.nextInt(quotes.size());
        Element qout = quotes.get(n);
        if (qout.getElementsByClass("text").size() > 0) {
            quote = qout.getElementsByClass("text").text();
        }
        return quote;
    }


    public static void main(String[] args) throws IOException {

    }
}

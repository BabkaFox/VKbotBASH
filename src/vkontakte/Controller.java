package vkontakte;


import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URISyntaxException;

public class Controller {
    @FXML
    Button btnLogin;
    @FXML
    Button btnNext;
    @FXML
    Button btnSend;

    @FXML
    TextField textLogin;
    @FXML
    TextArea textQuote;
    @FXML
    PasswordField textPass;

    @FXML
    Label labStatus;
    @FXML
    Label labTimer;
    @FXML
    public ProgressIndicator progressLog;

    VKapi vk = new VKapi();   //
    BashParser pars = new BashParser();


    @FXML
    public void inLogin() throws IOException, URISyntaxException {

        progressLog.setProgress(0);
        vk.email = textLogin.getText();
        vk.pass = textPass.getText();

        vk.setConnection();

        if (vk.status == true){
            progressLog.setProgress(1.0);

            labStatus.setText("Статус: онлайн");
            btnLogin.setDisable(true);
            btnSend.setDisable(false);
            textLogin.setDisable(true);
            textPass.setDisable(true);
        } else {
            labStatus.setText("Статус: офлайн");
        }
    }
    @FXML
    public void setd() throws IOException {
        vk.sendMesaage(textQuote.getText());
    }
    @FXML
    public void getQout() throws IOException {
        textQuote.setText(pars.getQuote());

    }
    @FXML
    public void formLoad() throws IOException {
        textQuote.setText(pars.getQuote());

    }


}

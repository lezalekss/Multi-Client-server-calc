package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.*;
import java.net.Socket;



public class LoginController {
    public static Socket getSoketZaKomunikaciju() {
        return soketZaKomunikaciju;
    }

    public static String getUsername() {
        return username;
    }

    private String odgovor=null;
    private static String username;
    private static Socket soketZaKomunikaciju;
    static {
        soketZaKomunikaciju = null;
    }


    // ************** LOGIN CONTROLLER *******************//
    @FXML
    private JFXTextField usernameField;
    @FXML
    private JFXPasswordField passwordField;
    @FXML
    private JFXButton loginButton;
    @FXML
    private JFXButton registerButton;


    @FXML
    private void handleEmptyFields(){
        username = this.usernameField.getText().trim();
        String password = this.passwordField.getText().trim();

        boolean disableButton = username.isEmpty() || password.isEmpty();

        this.loginButton.setDisable(disableButton);
        this.registerButton.setDisable(disableButton);
    }
    // *************************************************************//
    @FXML
    private void handleLoginButton (ActionEvent event) {
        username = this.usernameField.getText();
        String password = this.passwordField.getText();

        try {
             soketZaKomunikaciju = new Socket("localhost", 16114);
            PrintStream tokKaServeru = new PrintStream(soketZaKomunikaciju.getOutputStream());
            BufferedReader tokOdServera = new BufferedReader(
                    new InputStreamReader(soketZaKomunikaciju.getInputStream()));
            tokKaServeru.println("LOGOVANJE");

            tokKaServeru.println(username);
            tokKaServeru.println(password);
            odgovor = tokOdServera.readLine();
             if(odgovor.equals("CONFIRMED")){
                 alert2("ulogovali.");
                   this.changeToNextScene(event);
               }

            else {
                soketZaKomunikaciju.close();
                alert(odgovor);
                this.setIncorrectInfoStyle();
            }
        }

        catch (Exception e){
            alert(odgovor);
            this.setIncorrectInfoStyle();
        }

    }
    @FXML
    private void handleRegisterButton (ActionEvent event) {
        username = this.usernameField.getText();
        String password = this.passwordField.getText();

        try {
            soketZaKomunikaciju = new Socket("localhost", 16114);
            PrintStream tokKaServeru = new PrintStream(soketZaKomunikaciju.getOutputStream());
                BufferedReader tokOdServera = new BufferedReader(
                        new InputStreamReader(soketZaKomunikaciju.getInputStream()));
                tokKaServeru.println("REGISTRACIJA");

                tokKaServeru.println(username);
                tokKaServeru.println(password);
                String odgovor = tokOdServera.readLine();
                if(odgovor.equals("CONFIRMED")){
                    alert2("registrovali. Bićete prosleđeni dalje.");
                    this.changeToNextScene(event);
                }

                else {
                    soketZaKomunikaciju.close();
                    alert3(odgovor);
                this.setIncorrectInfoStyle();
            }
        }

        catch (Exception e){
            alert(odgovor);
            this.setIncorrectInfoStyle();
        }

    }
    @FXML
    private void handleGuestButton (ActionEvent event) {
        try {
            soketZaKomunikaciju = new Socket("localhost", 16114);
            PrintStream tokKaServeru = new PrintStream(soketZaKomunikaciju.getOutputStream());
            BufferedReader tokOdServera = new BufferedReader(
                    new InputStreamReader(soketZaKomunikaciju.getInputStream()));
            tokKaServeru.println("GOST");
            username = "GOST";
            String odgovor = tokOdServera.readLine();
            if(odgovor.equals("CONFIRMED")){
                alert2(" ulogovali kao GOST (limited - 3 operacije).");
                this.changeToNextScene(event);
            }

            else {
                soketZaKomunikaciju.close();
                alert3(odgovor);
                this.setIncorrectInfoStyle();
            }
        }

        catch (Exception e){
            alert(odgovor);
            this.setIncorrectInfoStyle();
        }

    }
    private void changeToNextScene(ActionEvent event){
        try{

            Parent root = FXMLLoader.load(getClass().getResource("/resources/view/MenuScreen.fxml"));

            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            primaryStage.getScene().setRoot(root);
            primaryStage.hide();


            primaryStage.centerOnScreen();
            primaryStage.show();
        }catch(IOException e){
            e.printStackTrace();;
            alert("Problem sa serverom.");
        }
    }

    private void setIncorrectInfoStyle(){
        this.usernameField.setText("GRESKA");
        this.passwordField.setText("GRESKA");
    }


    private void alert(String tekst){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("GREŠKA PRI LOGOVANJU");
        alert.setHeaderText(tekst);
        alert.setContentText("Molimo vas, probajte ponovo.");

        alert.showAndWait();
    }
    private void alert2(String tekst){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("DOBRODOSLI");
        alert.setHeaderText("Poštovani korisniče, uspešno ste se "+tekst);
        alert.setContentText("Možete krenuti sa računanjem.");

        alert.showAndWait();
    }
    private void alert3(String tekst){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("GREŠKA PRI REGISTRACIJI");
        alert.setHeaderText(tekst);
        alert.setContentText("Molimo vas, probajte ponovo.");

        alert.showAndWait();
    }


}

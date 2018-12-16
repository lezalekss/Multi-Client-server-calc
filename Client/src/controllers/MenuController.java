package controllers;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static controllers.LoginController.*;


public class MenuController {
   private static PrintStream clientOutput ;
    private static BufferedReader clientInput;
    private static String username;
    // konstruktor za nit (da se prosledi soket i da li je gost
    public MenuController() {
        Socket soketZaKomunikaciju = getSoketZaKomunikaciju();
        username = getUsername();

        try{
            clientInput = new BufferedReader(new InputStreamReader(soketZaKomunikaciju.getInputStream()));
            clientOutput = new PrintStream(soketZaKomunikaciju.getOutputStream());

            Scanner citaj  = new Scanner(new File("istorijaKalkulacija.txt"));
            while (citaj.hasNext())
                istorijaTekst.setText(citaj.nextLine());
            citaj.close();

        }
        catch(IOException e){
            e.printStackTrace();
        }


    }

    private double prviBroj=0;
    private String drugiBroj;
    private String operacija;
    @FXML
    private TextField result;
    @FXML
    public JFXButton bt1 ; @FXML public JFXButton bt2; @FXML public JFXButton bt3; @FXML public JFXButton bt4;  @FXML public JFXButton bt5;
    @FXML  public JFXButton bt6; @FXML public JFXButton bt7; @FXML public JFXButton bt9; @FXML public JFXButton bt8;@FXML public JFXButton bt0;
    @FXML public JFXButton btBackspace; @FXML public JFXButton btPlus; @FXML public JFXButton btMinus; @FXML public JFXButton btMnozenje;
    @FXML public JFXButton btDeljenje; @FXML public JFXButton btC;
    @FXML
    public JFXButton btPreuzmi;  @FXML public TextArea istorijaTekst;

    @FXML
    private void upisi1() {
        String enterNumber = result.getText() + bt1.getText();
        result.setText(enterNumber);
    }
    @FXML
    private void upisi2() {
        String enterNumber = result.getText() + bt2.getText();
        result.setText(enterNumber);
    }
    @FXML
    private void upisi3() {
        String enterNumber = result.getText() + bt3.getText();
        result.setText(enterNumber);
    }
    @FXML
    private void upisi4() {
        String enterNumber = result.getText() + bt4.getText();
        result.setText(enterNumber);
    }
    @FXML
    private void upisi5() {
        String enterNumber = result.getText() + bt5.getText();
        result.setText(enterNumber);
    }
    @FXML
    private void upisi6() {
        String enterNumber = result.getText() + bt6.getText();
        result.setText(enterNumber);
    }
    @FXML
    private void upisi7() {
        String enterNumber = result.getText() + bt7.getText();
        result.setText(enterNumber);
    }
    @FXML
    private void upisi8() {
        String enterNumber = result.getText() + bt8.getText();
        result.setText(enterNumber);
    }
    @FXML
    private void upisi9() {
        String enterNumber = result.getText() + bt9.getText();
        result.setText(enterNumber);
    }
    @FXML
    private void upisi0() {
        String enterNumber = result.getText() + bt0.getText();
        result.setText(enterNumber);
    }

    @FXML
    private void backspace() {
        if(!result.getText().isEmpty())
             result.setText(result.getText().substring(0, result.getText().length() - 1));
    }
    @FXML
    private void saberi() {
        if(!result.getText().isEmpty())
             prviBroj = Double.parseDouble(result.getText());
        result.setText("");
        operacija="+";
    }
    @FXML
    private void oduzmi() {
        if(!result.getText().isEmpty())
             prviBroj = Double.parseDouble(result.getText());
        result.setText("");
        operacija="-";
    }
    @FXML
    private void pomnozi() {
        if(!result.getText().isEmpty())
             prviBroj = Double.parseDouble(result.getText());
        else {drugiBroj="";}
        result.setText("");
        operacija="*";
    }
    @FXML
    private void podeli() {
        if(!result.getText().isEmpty())
            prviBroj = Double.parseDouble(result.getText());
        result.setText("");
        operacija="/";
    }
    @FXML
    private void obrisi() {
        prviBroj = 0;
        drugiBroj = null;
        result.setText("");
        operacija="";
    }

    @FXML
    private void salji(ActionEvent event){
        drugiBroj = result.getText();
        if(drugiBroj.isEmpty() || prviBroj==0 )
            return;
        clientOutput.println("izracunaj");

        clientOutput.println(""+prviBroj);

        clientOutput.println(drugiBroj);
        clientOutput.println(operacija);
        prviBroj=0; drugiBroj=null;
        try {
            String tekst = clientInput.readLine();
            if(tekst.equals("EROR")){
                alert();
                Platform.exit();
                System.exit(0);

            }
            result.setText(tekst);
        }catch (IOException e) {
            alert2();
            changeToNextScene(event);
            e.printStackTrace();
        }

    }

    @FXML
    private void preuzmi(ActionEvent event){
        String tekst;
        if(username.equals("GOST"))
            alert1();
        else{
            try {
                clientOutput.println("preuzmi");
                //tekst=clientInput.readLine();
                //istorijaTekst.setText(tekst+" ");
                int brKalkulacija = Integer.parseInt(clientInput.readLine());
                File f = new File("istorijaKalkulacija.txt");
                PrintWriter out = new PrintWriter(
                        new FileWriter("istorijaKalkulacija.txt"));
                for(int i=0;i<brKalkulacija;i++){
                    tekst=clientInput.readLine();
                    out.println(tekst);
                    istorijaTekst.setText(istorijaTekst.getText()+tekst+"\n");
                }
                out.close();


            }catch (IOException e){
                alert2();
                changeToNextScene(event);
                e.printStackTrace();
            }
        }
    }


    private void alert(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("GREŠKA");
        alert.setHeaderText("VASA SESIJA JE ISTEKLA");

        alert.showAndWait();
    }
    private void alert1(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("GREŠKA");
        alert.setHeaderText("Da biste preuzeli istoriju kalkulacija morate biti registrovani.");

        alert.showAndWait();
    }
    private void alert2(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("GREŠKA");
        alert.setHeaderText("Izgubljena je veza sa serverom...");

        alert.showAndWait();
    }
    private void changeToNextScene(ActionEvent event){
        try{

            Parent root = FXMLLoader.load(getClass().getResource("/resources/view/LoginScreen.fxml"));

            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();


            primaryStage.getScene().setRoot(root);
            primaryStage.hide();


            primaryStage.centerOnScreen();
            primaryStage.show();
        }catch(IOException e){
            alert2();
        }
    }

}

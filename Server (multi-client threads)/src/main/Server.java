package main;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;


import static java.lang.Character.isDigit;
import static java.lang.Character.isUpperCase;

public class Server {
    private static ArrayList<String> lista = new ArrayList<>();
    private static BCryptHelper bCryptHelper;
    private static Connection connection = null;
    public static void main(String[] args) {

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mariadb://localhost/myserverdatabase", "root", "");


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        bCryptHelper = new BCryptHelper();
        Scanner citaj;
        ServerSocket serverSoket ;
        Socket soketZaKomunikaciju;
        BufferedReader clientInput ;
        PrintStream clientOutput ;
        //punimo listu registrovanih korisnika
        try {
            citaj  = new Scanner(new File("registrovani.txt"));
            while (citaj.hasNext())
                lista.add(citaj.nextLine());
            citaj.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        try{
            serverSoket = new ServerSocket(16114);


            // Ovde prihvatamo nove korisnike/klijente i za svakog pokrecemo novu NIT ClientHandler koja preuzima dalju komunikaciju sa njima.
            // Kao parametar prosledjujemo svakoj niti soketZaKomunikaciju
           while (true){
               System.out.println("Server ceka na novu konekciju . .  .");
               soketZaKomunikaciju = serverSoket.accept();

               clientInput = new BufferedReader(new InputStreamReader(soketZaKomunikaciju.getInputStream()));
               clientOutput = new PrintStream(soketZaKomunikaciju.getOutputStream());

               String input = clientInput.readLine();
               switch (input) {
                   case "LOGOVANJE": {
                       String input1 = clientInput.readLine();
                       String input2 = clientInput.readLine();
                       if (isRegistered(input1,input2)) {
                           clientOutput.println("CONFIRMED");
                           System.out.println("Korisnik " + input1 + " je ulogovan .");
                           // ovde se pokrece nit za llogovanje
                           ClientHandler klijent = new ClientHandler(soketZaKomunikaciju,input1);
                           klijent.start();
                       }else clientOutput.println("Korisnik: "+input1+" ne postoji / ili je šifra pogrešna.");

                       break;
                   }
                   case "REGISTRACIJA": {
                       String input1 = clientInput.readLine();
                       String input2 = clientInput.readLine();
                       if (!isRegistered(input1,input2) && isValid(input2)) {
                           String hashedPass=bCryptHelper.GetHash(input2);

                           try {
                               Statement stmt = connection.createStatement();

                               stmt.execute("INSERT INTO `user`(`Username`, `Password`) VALUES ('"+input1+"','"+hashedPass+"')");
                               stmt.close();

                               //appendStrToFile(input1, hashedPass);
                               lista.add(input1);
                               lista.add(hashedPass);
                               clientOutput.println("CONFIRMED");
                               System.out.println("Registrovao se korisnik " + input1 + " i ulogovan je .");
                               // ovde se pokrece nit za llogovanje
                               ClientHandler klijent = new ClientHandler(soketZaKomunikaciju,input1);
                               klijent.start();
                           } catch (SQLException e) {
                               e.printStackTrace();
                           }



                       }else clientOutput.println("Korisnik: "+input1+" vec postoji / ili je šifra neadekvatna.");

                       break;
                   }
                   case "GOST":{
                       clientOutput.println("CONFIRMED");
                       System.out.println("Klijent je pristupio kao gost.");
                       //ovde se pokrece nit za gosta
                       GuestHandler gost = new GuestHandler(soketZaKomunikaciju);
                       gost.start();
                   } break;
               }



           }

        }catch (IOException e){
            System.out.println("GRESKA PRI POKRETANJU SERVERA");
        }



    }


    private static boolean isRegistered(String user, String pass){
        try {


            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM user WHERE Username='"+user+"'");

            if(rs.next()){
                String hashedPass = rs.getString(3);


                if(bCryptHelper.CheckHash(pass,hashedPass))
                    return true;

            }

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
    private static boolean isValid(String pass){
        boolean broj = false,velikoSlovo=false;
        if(pass.length() >=8 ){
            for(int i=0;i<pass.length();i++){
                if(isDigit(pass.charAt(i)))
                    broj=true;
                if(isUpperCase(pass.charAt(i)))
                    velikoSlovo=true;
            }if(broj && velikoSlovo)return true;

        }return false;
    }
    private static void appendStrToFile(String user,
                                       String pass)
    {
        try {

            // Open given file in append mode.
            PrintWriter out = new PrintWriter(
                    new FileWriter("registrovani.txt", true));
            out.println(user);
            out.println(pass);
            out.close();
        }
        catch (IOException e) {
            System.out.println("exception occoured" + e);
        }
    }

}

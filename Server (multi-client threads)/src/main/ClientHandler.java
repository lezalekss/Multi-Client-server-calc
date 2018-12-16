package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ClientHandler extends Thread{

    private BufferedReader clientInput ;
    private PrintStream clientOutput ;
    private Socket soketZaKomunikaciju ;
    private String username;
    private static Connection connection = null;
    private Statement stmt = null;
    // konstruktor za nit (da se prosledi soket i da li je gost
    public ClientHandler(Socket soket, String user) {
        soketZaKomunikaciju = soket;
        username = user;
    }

    @Override
    public void run() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            connection = DriverManager.getConnection(
                    "jdbc:mariadb://localhost/myserverdatabase", "root", "");


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            clientInput = new BufferedReader(new InputStreamReader(soketZaKomunikaciju.getInputStream()));
            clientOutput = new PrintStream(soketZaKomunikaciju.getOutputStream());
          //  ArrayList<String> istorija = new ArrayList<>();
            stmt = connection.createStatement();
            ResultSet rs;
            String istorijaIzBaze=null;
            rs = stmt.executeQuery("SELECT istorija FROM user WHERE Username= '"+username+"' ");
            if(rs.next()) {
                istorijaIzBaze = rs.getString("istorija");
                if(istorijaIzBaze==null)
                    istorijaIzBaze="";
            }
            while(true){
                String opcija= clientInput.readLine();
                if(opcija.equals("izracunaj")) {
                    double prviBroj = Double.parseDouble(clientInput.readLine());
                    double drugiBroj = Double.parseDouble(clientInput.readLine());
                    double res;
                    String operacija = clientInput.readLine();
                    switch (operacija) {
                        case "+":
                            res = prviBroj + drugiBroj;
                            break;
                        case "-":
                            res = prviBroj - drugiBroj;
                            break;
                        case "/":
                            res = prviBroj / drugiBroj;
                            break;
                        case "*":
                            res = prviBroj * drugiBroj;
                            break;
                        default:
                            return;
                    }
                    DecimalFormat df = new DecimalFormat("###.##");

                    clientOutput.println("" + df.format(res));

                    istorijaIzBaze=istorijaIzBaze +"" + prviBroj+" " +operacija + " " +drugiBroj +" = " +df.format(res) + "\r\n";

                    // OVDE POGLEDATI BACA EXCEPTION
                    stmt.execute("UPDATE user SET istorija=' "+ istorijaIzBaze +"' WHERE Username= '"+username+"'");


                    //istorija.add(prviBroj + " " +operacija + " " +drugiBroj +" = " +df.format(res) + "\n");


                }else if(opcija.equals("preuzmi")){ // Ako korisnik zeli da preuzme istoriju kalkulacija
                    String linije[]=istorijaIzBaze.split("\n");
                    clientOutput.println(linije.length);
                    for(int i=0;i<linije.length;i++) {
                        clientOutput.println(linije[i]);
                    }


                }

            }



        }
        catch (IOException e) {
            System.out.println("Klijent: "+username+ " je napustio aplikaciju.");

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if(stmt!=null)
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}

package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.text.DecimalFormat;

public class GuestHandler extends Thread{

    BufferedReader clientInput ;
    PrintStream clientOutput ;
    Socket soketZaKomunikaciju ;
    // konstruktor za nit (da se prosledi soket i da li je gost
    public GuestHandler(Socket soket) {
        soketZaKomunikaciju = soket;
    }

    @Override
    public void run() {
        try {
            clientInput = new BufferedReader(new InputStreamReader(soketZaKomunikaciju.getInputStream()));
            clientOutput = new PrintStream(soketZaKomunikaciju.getOutputStream());

            for(int i=0;i<3;i++){

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



                }else if(clientInput.readLine().equals(""));

            }{String opcija= clientInput.readLine();
                double prviBroj = Double.parseDouble(clientInput.readLine());
                double drugiBroj = Double.parseDouble(clientInput.readLine());
                double res;
                String operacija = clientInput.readLine();
                clientOutput.println("EROR");
            }



        } catch (IOException e) {
            System.out.println("Client je napustio aplikaciju.");

        }

    }
}

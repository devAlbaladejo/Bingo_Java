import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/*
    Author: Ángel Albaladejo Flores 2ºDAM SEMI
 */

public class ServerThread extends Thread{

    int[] ticketNumbers = new int[5];
    List<Integer> generatedNumbers = new ArrayList<>();
    boolean repeatedNumber = false;
    Socket service;
    String ticketNumbersString = "";
    int countTicketNumbers = 0;
    int currentNumber = 0;
    boolean winner = false;

    public ServerThread(Socket s)
    {
        service = s;
    }

    @Override
    public void run()
    {
        DataInputStream socketIn = null;
        ObjectOutputStream socketOut = null;
        try
        {
            socketIn = new DataInputStream(service.getInputStream());
            socketOut = new ObjectOutputStream(service.getOutputStream());

            generateTicket();

            //We send the ticket to the client
            socketOut.writeUTF(ticketNumbersString);
            socketOut.flush();

            //As long as there is no winning client, it is executed
            do{
                winner = socketIn.readBoolean();
                if(winner)
                    BingoServer.finishGame = true;

                //If you have not won any customers, numbers continue to be generated
                if(!BingoServer.finishGame){
                    generatedNumbers.add(BingoServer.randomNumbers.get(currentNumber));
                    socketOut.writeObject(generatedNumbers);
                    socketOut.flush();
                }
                currentNumber++;
                Thread.sleep(2000);
                socketOut.reset();
            }
            while(!BingoServer.finishGame);

            //If a client wins, -1 is sent to the rest to indicate that they have lost
            if(!winner){
                generatedNumbers.add(-1);
                socketOut.writeObject(generatedNumbers);
                socketOut.flush();
            }

        } catch (IOException | InterruptedException e) {
            System.out.println(e);
        } finally {
            try {
                if (socketOut != null)
                    socketOut.close();
            } catch (IOException ex) {}
            try {
                if (socketIn != null)
                    socketIn.close();
            } catch (IOException ex) {}
            try {
                if (service != null)
                    service.close();
            } catch (IOException ex) {}
        }
    }

    //We create a method to generate the ticket
    public void generateTicket(){
        //We generate the ticket without repetitions
        while(countTicketNumbers < ticketNumbers.length){
            int a = (int)(Math.random()*20 + 1);
            for(int i = 0; i < countTicketNumbers; i++){
                if(ticketNumbers[i] == a)
                    repeatedNumber = true;
            }

            if(!repeatedNumber){
                ticketNumbers[countTicketNumbers] = a;
                countTicketNumbers++;
            }
            repeatedNumber = false;
        }

        //We order them ascending
        for (int i = 0; i < ticketNumbers.length - 1; i++) {
            for (int j = 0; j < ticketNumbers.length - i - 1; j++) {
                if (ticketNumbers[j + 1] < ticketNumbers[j]) {
                    int aux = ticketNumbers[j + 1];
                    ticketNumbers[j + 1] = ticketNumbers[j];
                    ticketNumbers[j] = aux;
                }
            }
        }

        //We generate the ticket
        for(int i = 0; i < ticketNumbers.length; i++){
            ticketNumbersString += ticketNumbers[i] + " ";
        }
    }
}
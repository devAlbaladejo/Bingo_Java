import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
    Author: Ángel Albaladejo Flores 2ºDAM SEMI
 */

public class BingoServer {

    public static final int MAX_CONNECTIONS = 2;
    public static final int MAX_NUMBER = 20;
    public static List<Integer> randomNumbers = new ArrayList<>();
    public static boolean finishGame = false;

    public static void main(String[] args){
        //We generate random numbers
        randomNumbers = IntStream.rangeClosed(1, MAX_NUMBER)
                .boxed()
                .collect(Collectors.toList());
        Collections.shuffle(randomNumbers);

        //We show the generated numbers
        System.out.println("The list of randoms numbers is: ");

        for (int number: randomNumbers) {
            System.out.print(number + " ");
        }
        System.out.println();

        //We create as many Server Thread as connections we allow
        ServerThread[] st = new ServerThread[MAX_CONNECTIONS];
        int connectedPlayers = 0;

        try (ServerSocket server = new ServerSocket(6000))
        {
            System.out.println("Waiting players ...");

            //As long as there are free connections we accept them
            do{
                Socket service = server.accept();
                System.out.println("A player has connected");
                st[connectedPlayers] = new ServerThread(service);
                connectedPlayers++;
                System.out.println((MAX_CONNECTIONS - connectedPlayers) + " players left");
            }
            while(connectedPlayers < MAX_CONNECTIONS);

            //We will start the client threads
            System.out.println("Starting game ... ");
            for(int i = 0; i < MAX_CONNECTIONS; i++){
                st[i].start();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
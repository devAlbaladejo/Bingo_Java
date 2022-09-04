package com.angelalbaladejoflores.bingofxclient;

import com.angelalbaladejoflores.bingofxclient.utils.MessageUtils;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/*
    Author: Ángel Albaladejo Flores 2ºDAM SEMI
 */

public class Controller{

    public Label lblServerAddress;
    public TextField textServerAddress;
    public TextField textPort;
    public Label lblPort;
    public Button btnConnect;
    public Label lblNumber1;
    public Label lblGameOver;
    public Label lblNumber2;
    public Label lblNumber4;
    public Label lblNumber3;
    public Label lblNumber5;

    int hits = 0;
    List<Integer> generatedNumbers = new ArrayList<>();
    String[] numbers = null;
    private ScheduledService<Integer> service;
    ObjectInputStream socketIn;
    DataOutputStream socketOut;

    public void connectClient(ActionEvent actionEvent) throws IOException {
        //We check if there are errors when entering the data
        if ((!textServerAddress.getText().isEmpty()) || (!textPort.getText().isEmpty())) {
            if ((!textServerAddress.getText().equals("localhost")) && (!textServerAddress.getText().equals("127.0.0.1"))) {
                MessageUtils.errorMessage("Error", "Unknown Server", "Unknown server address. Test with localhost or 127.0.0.1");
            } else if (!textPort.getText().equals("6000")) {
                MessageUtils.errorMessage("Error", "Unknown port", "The server only listens to port 6000");
            } else {
                //If all the data has been entered correctly, it executes
                try
                {
                    Socket mySocket = new Socket(textServerAddress.getText(), Integer.parseInt(textPort.getText()));
                    service = new ScheduledService<Integer>() {
                        @Override
                        protected Task<Integer> createTask() {
                            return new Task<Integer>() {
                                @Override
                                protected Integer call() throws Exception {
                                    //The first time the ticket is shown
                                    if(numbers == null){
                                        socketIn = new ObjectInputStream(mySocket.getInputStream());
                                        socketOut = new DataOutputStream(mySocket.getOutputStream());
                                        String line = socketIn.readUTF();
                                        numbers = line.split(" ");
                                        Platform.runLater(() -> {
                                            lblNumber1.setText(numbers[0]);
                                            lblNumber2.setText(numbers[1]);
                                            lblNumber3.setText(numbers[2]);
                                            lblNumber4.setText(numbers[3]);
                                            lblNumber5.setText(numbers[4]);
                                        });
                                        socketOut.writeBoolean(false);
                                        return 0;
                                    }
                                    else{
                                        generatedNumbers = (List<Integer>) socketIn.readObject();
                                        System.out.println(generatedNumbers);
                                        //If it is different from -1, it checks if it has hit the number
                                        if((generatedNumbers.get(generatedNumbers.size()-1)) != -1) {
                                            for (int i = 0; i < numbers.length; i++) {
                                                if ((generatedNumbers.get(generatedNumbers.size()-1) == Integer.parseInt(numbers[i]))) {
                                                    hits++;
                                                    switch(i){
                                                        case 0:
                                                            lblNumber1.setStyle("-fx-background-color:#00ff00");
                                                            break;
                                                        case 1:
                                                            lblNumber2.setStyle("-fx-background-color:#00ff00");
                                                            break;
                                                        case 2:
                                                            lblNumber3.setStyle("-fx-background-color:#00ff00");
                                                            break;
                                                        case 3:
                                                            lblNumber4.setStyle("-fx-background-color:#00ff00");
                                                            break;
                                                        case 4:
                                                            lblNumber5.setStyle("-fx-background-color:#00ff00");
                                                            break;
                                                    }
                                                }
                                            }

                                            if(hits == 5){
                                                socketOut.writeBoolean(true);
                                                socketOut.flush();
                                            }
                                            else {
                                                socketOut.writeBoolean(false);
                                                socketOut.flush();
                                            }

                                            return hits;
                                        }
                                        //If not, it has been lost
                                        else
                                            return -1;
                                    }
                                }
                            };
                        }
                    };

                } catch (Exception e) {}

                service.setOnSucceeded(s -> {
                    try{
                        if(service.getValue() == 5) {
                            lblGameOver.setText("YOU WIN!");
                            service.cancel();
                        }
                        else {
                            if (service.getValue() == -1) {
                                lblGameOver.setText("YOU LOSE!");
                                service.cancel();
                            }
                        }
                    } catch (Exception e){}
                });
                service.start();
            }
        } else {
            MessageUtils.errorMessage("Error", "Empty fields", "You must indicate the address and port of the server");
        }
    }
}
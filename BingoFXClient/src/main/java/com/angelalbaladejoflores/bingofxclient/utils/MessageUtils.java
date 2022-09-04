package com.angelalbaladejoflores.bingofxclient.utils;

import javafx.scene.control.Alert;

/*
 This class shows the different messages generated in the program
 @author: Ángel Albaladejo Flores
 */

public class MessageUtils {
    public static void errorMessage(String title, String header, String content){
        Alert error = new Alert(Alert.AlertType.ERROR);

        error.setTitle(title);
        error.setHeaderText(header);
        error.setContentText(content);
        error.showAndWait();
    }

    public static void informationMessage(String title, String header, String content){
        Alert information = new Alert(Alert.AlertType.INFORMATION);

        information.setTitle(title);
        information.setHeaderText(header);
        information.setContentText(content);
        information.showAndWait();
    }
}

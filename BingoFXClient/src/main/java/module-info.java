module com.angelalbaladejoflores.bingofxclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.angelalbaladejoflores.bingofxclient to javafx.fxml;
    exports com.angelalbaladejoflores.bingofxclient;
}
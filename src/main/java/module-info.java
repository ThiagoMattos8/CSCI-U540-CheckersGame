module com.vague.checkersgame {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.vague.checkersgame to javafx.fxml;
    exports com.vague.checkersgame;
}
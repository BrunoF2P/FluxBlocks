module com.uneb.tetris {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens com.uneb.tetris to javafx.fxml;
    exports com.uneb.tetris;
}
open module com.uneb.tetris {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;

    requires com.almasb.fxgl.all;

    exports com.uneb.tetris;
    exports com.uneb.tetris.core;
    exports com.uneb.tetris.ui;
    exports com.uneb.tetris.ui.components;
    exports com.uneb.tetris.ui.screens;
    exports com.uneb.tetris.event;

}
open module com.uneb.tetris {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;

    requires transitive com.almasb.fxgl.all;
    requires transitive com.almasb.fxgl.core;
    requires transitive com.almasb.fxgl.entity;

    exports com.uneb.tetris.core;
    exports com.uneb.tetris.ui;
    exports com.uneb.tetris.ui.components;
    exports com.uneb.tetris.ui.screens;
    exports com.uneb.tetris.event;
    exports com.uneb.tetris.board;
    exports com.uneb.tetris.piece;
}
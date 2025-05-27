open module com.uneb.tetris {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;

    requires transitive com.fasterxml.jackson.databind;
    requires transitive com.almasb.fxgl.all;

    exports com.uneb.tetris.game.core;
    exports com.uneb.tetris.ui;
    exports com.uneb.tetris.ui.components;
    exports com.uneb.tetris.ui.screens;
    exports com.uneb.tetris.piece.factory;
    exports com.uneb.tetris.game.logic;
    exports com.uneb.tetris.architecture.mediators;
    exports com.uneb.tetris.architecture.events;
    exports com.uneb.tetris.piece.entities;
    exports com.uneb.tetris.piece.movement;
    exports com.uneb.tetris.game.scoring;
    exports com.uneb.tetris.piece;
}
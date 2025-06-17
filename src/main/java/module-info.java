open module com.uneb.fluxblocks {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires javafx.graphics;
    requires java.desktop;

    requires transitive com.fasterxml.jackson.databind;
    requires transitive com.fasterxml.jackson.core;
    requires transitive com.almasb.fxgl.all;


    exports com.uneb.fluxblocks.piece.timing;
    exports com.uneb.fluxblocks.piece.collision;
    exports com.uneb.fluxblocks.game.core;
    exports com.uneb.fluxblocks.ui;
    exports com.uneb.fluxblocks.ui.components;
    exports com.uneb.fluxblocks.ui.screens;
    exports com.uneb.fluxblocks.piece.factory;
    exports com.uneb.fluxblocks.game.logic;
    exports com.uneb.fluxblocks.architecture.mediators;
    exports com.uneb.fluxblocks.architecture.events;
    exports com.uneb.fluxblocks.piece.entities;
    exports com.uneb.fluxblocks.piece.movement;
    exports com.uneb.fluxblocks.game.scoring;
    exports com.uneb.fluxblocks.piece;
}
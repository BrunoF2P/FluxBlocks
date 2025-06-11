package com.uneb.tetris.ui.screens;

import com.uneb.tetris.architecture.mediators.GameMediator;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class OptionScreen  {
    private final VBox content;
    private final StackPane root;
    private final GameMediator mediator;

    public OptionScreen( GameMediator mediator) {
        this.mediator = mediator;
        this.content = new VBox(20);
        this.root = new StackPane();
    }



    public Node getNode() {
        return root;
    }

    public void destroy() {
        content.getChildren().clear();
        root.getStylesheets().clear();
    }

}

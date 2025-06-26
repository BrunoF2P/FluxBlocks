package com.uneb.fluxblocks.ui.components;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * Componente reutilizável para footers das telas.
 * Exibe atalhos de teclado e ações disponíveis.
 */
public class FooterComponent extends HBox {
    
    /**
     * Construtor padrão com alinhamento à direita.
     * @param items Array de pares [label, tecla]
     */
    public FooterComponent(String[][] items) {
        this(items, Pos.CENTER_RIGHT);
    }
    
    /**
     * Construtor com alinhamento customizado.
     * @param items Array de pares [label, tecla]
     * @param alignment Alinhamento do footer
     */
    public FooterComponent(String[][] items, Pos alignment) {
        super(12);
        setAlignment(alignment);
        getStyleClass().add("menu-footer");
        
        for (String[] item : items) {
            if (item.length >= 2) {
                getChildren().add(createFooterItem(item[0], item[1]));
            }
        }
    }
    
    /**
     * Cria um item individual do footer.
     * @param label Texto do label
     * @param key Tecla/atalho
     * @return HBox contendo o item
     */
    private HBox createFooterItem(String label, String key) {
        HBox box = new HBox(12);
        box.getStyleClass().add("footer-item");
        box.setAlignment(Pos.CENTER);

        Text labelText = new Text(label);
        labelText.getStyleClass().add("footer-label");

        StackPane keyContainer = new StackPane();
        keyContainer.getStyleClass().add("footer-key-container");
        Text keyText = new Text(key);
        keyText.getStyleClass().add("footer-key");
        keyContainer.getChildren().add(keyText);

        box.getChildren().addAll(labelText, keyContainer);
        return box;
    }
} 
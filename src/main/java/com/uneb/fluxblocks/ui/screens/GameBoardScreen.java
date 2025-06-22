package com.uneb.fluxblocks.ui.screens;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.ui.components.GameBoardScreenComponent;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

/**
 * Tela do tabuleiro do jogo, responsável por gerenciar a exibição e interações do tabuleiro.
 * Contém o componente principal que renderiza o tabuleiro e os efeitos visuais.
 */
public class GameBoardScreen {
    
    private final Entity screenEntity;
    private final GameBoardScreenComponent screenComponent;
    private final GameMediator mediator;
    private final int playerId;



    public GameBoardScreen(GameMediator mediator, int playerId) {
        this.mediator = mediator;
        this.playerId = playerId;
        
        this.screenComponent = new GameBoardScreenComponent(mediator, playerId);
        
        this.screenEntity = FXGL.entityBuilder()
            .at(0, 0)
            .with(screenComponent)
            .buildAndAttach();
    }

    /**
     * Aplica efeito de limpeza de linha.
     *
     * @param row Linha onde aplicar o efeito
     */
    public void applyLineClearEffect(int row) {
        screenComponent.applyLineClearEffect(row);
    }

    /**
     * Limpa todo o conteúdo do tabuleiro.
     */
    public void clearBoard() {
        screenComponent.clearBoard();
    }

    /**
     * Retorna o nó raiz da tela do tabuleiro.
     *
     * @return O componente Parent que contém toda a interface do tabuleiro
     */
    public Parent getNode() {
        return screenComponent.getEffectsLayer().getParent();
    }

    /**
     * Retorna a camada de efeitos visuais do tabuleiro.
     *
     * @return O painel usado para efeitos visuais
     */
    public Pane getEffectsLayer() {
        return screenComponent.getEffectsLayer();
    }

    /**
     * Retorna a largura total do tabuleiro em pixels.
     * @return Largura do tabuleiro
     */
    public int getWidth() {
        return screenComponent.getWidth();
    }

    /**
     * Retorna a altura total do tabuleiro em pixels.
     * @return Altura do tabuleiro
     */
    public int getHeight() {
        return screenComponent.getHeight();
    }
    
    /**
     * Retorna a entidade FXGL da tela.
     * @return Entidade FXGL
     */
    public Entity getEntity() {
        return screenEntity;
    }
}
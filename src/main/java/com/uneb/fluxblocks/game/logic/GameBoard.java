package com.uneb.fluxblocks.game.logic;

import java.util.ArrayList;
import java.util.List;

import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.configuration.GameConfig;
import com.uneb.fluxblocks.ui.effects.Effects;
import com.uneb.fluxblocks.ui.effects.FloatingTextEffect;

import javafx.application.Platform;
import javafx.scene.layout.Pane;

/**
 * Representa o tabuleiro do jogo.
 * Gerencia o estado das células, detecção de linhas completas e notificação de mudanças.
 * 
 * <p>O tabuleiro possui uma área visível e uma área de buffer para permitir
 * que peças apareçam gradualmente acima do tabuleiro visível.</p>
 * 
 * @author Bruno Bispo
 */
public class GameBoard {
    /** Valor que representa uma célula vazia */
    public static final int EMPTY_CELL = 0;
    
    /** Valor retornado quando a posição está fora dos limites do tabuleiro */
    public static final int INVALID_POSITION = -1;
    
    private final GameMediator mediator;
    private final int width = GameConfig.BOARD_WIDTH;
    private final int bufferHeight = GameConfig.BOARD_VISIBLE_ROW;
    private final int visibleHeight = GameConfig.BOARD_HEIGHT;
    private final int height = visibleHeight + bufferHeight;
    private final int[][] grid;
    private final int playerId;
    
    private boolean hasChanges = false;

    /**
     * Constrói um novo tabuleiro de jogo.
     * 
     * @param mediator O mediador para comunicação entre componentes
     * @param playerId O ID do jogador associado a este tabuleiro
     */
    public GameBoard(GameMediator mediator, int playerId) {
        this.mediator = mediator;
        this.playerId = playerId;
        this.grid = new int[height][width];
        clearGrid();
    }

    /**
     * Retorna a largura do tabuleiro.
     * 
     * @return A largura em células
     */
    public int getWidth() {
        return width;
    }

    /**
     * Retorna a altura total do tabuleiro (incluindo buffer).
     * 
     * @return A altura total em células
     */
    public int getHeight() {
        return height;
    }

    /**
     * Obtém o valor de uma célula na posição especificada.
     * 
     * @param x A coordenada X (coluna)
     * @param y A coordenada Y (linha) - pode ser negativa para área de buffer
     * @return O valor da célula ou {@link #INVALID_POSITION} se a posição for inválida
     */
    public int getCell(int x, int y) {
        int realY = y + bufferHeight;
        if (x >= 0 && x < width && realY >= 0 && realY < height) {
            return grid[realY][x];
        }
        return INVALID_POSITION;
    }

    /**
     * Verifica se uma posição é válida no tabuleiro.
     * 
     * @param x A coordenada X (coluna)
     * @param y A coordenada Y (linha) - pode ser negativa para área de buffer
     * @return true se a posição for válida
     */
    public boolean isValidPosition(int x, int y) {
        return x >= 0 && x < width && y >= -bufferHeight && y < visibleHeight;
    }

    /**
     * Define o valor de uma célula na posição especificada.
     * 
     * @param x A coordenada X (coluna)
     * @param y A coordenada Y (linha) - pode ser negativa para área de buffer
     * @param value O valor a ser definido
     * @return true se a célula foi definida com sucesso
     */
    public boolean setCell(int x, int y, int value) {
        int realY = y + bufferHeight;
        if (x >= 0 && x < width && realY >= 0 && realY < height) {
            grid[realY][x] = value;
            hasChanges = true;
            return true;
        }
        return false;
    }

    /**
     * Remove linhas completas do tabuleiro com efeitos visuais.
     * 
     * @param effectsLayer A camada de efeitos para aplicar animações visuais
     * @return O número de linhas removidas
     */
    public int removeCompletedLines(Pane effectsLayer) {
        int linesRemoved = 0;
        boolean[] isLineComplete = new boolean[height];
        List<Integer> clearedLines = new ArrayList<>();

        for (int y = height - 1; y >= 0; y--) {
            if (isLineComplete(y)) {
                isLineComplete[y] = true;
                linesRemoved++;
                clearedLines.add(y - bufferHeight);
            }
        }

        if (linesRemoved > 0) {
            int finalLinesRemoved = linesRemoved;
            Platform.runLater(() -> {
                // Aplica efeito visual em todas as linhas limpas
                for (int y = 0; y < height; y++) {
                    if (isLineComplete[y]) {
                        Effects.applyLineClearEffect(effectsLayer, y - bufferHeight, GameConfig.CELL_SIZE);
                    }
                }

                // Exibe o texto flutuante apenas na linha central das linhas limpas
                if (!clearedLines.isEmpty()) {
                    int middleIdx = clearedLines.size() / 2;
                    int lineIdx = clearedLines.get(middleIdx);
                    FloatingTextEffect.showLineClearText(effectsLayer, lineIdx, GameConfig.CELL_SIZE, finalLinesRemoved);
                }

                // Screen shake
                double intensity = Effects.SHAKE_INTENSITY_BASE +
                        (finalLinesRemoved - 1) * Effects.SHAKE_INTENSITY_MULTIPLIER;
                mediator.emit(UiEvents.SCREEN_SHAKE, new UiEvents.ScreenShakeEvent(playerId, intensity));
            });

            removeCompleteLines(isLineComplete);
            hasChanges = true;
        }

        return linesRemoved;
    }

    /**
     * Verifica se uma linha está completa.
     * 
     * @param y A coordenada Y da linha (coordenada interna)
     * @return true se a linha estiver completa
     */
    private boolean isLineComplete(int y) {
        for (int x = 0; x < width; x++) {
            if (grid[y][x] == EMPTY_CELL) {
                return false;
            }
        }
        return true;
    }

    /**
     * Remove as linhas completas e move as linhas acima para baixo.
     * 
     * @param isLineComplete Array indicando quais linhas estão completas
     */
    private void removeCompleteLines(boolean[] isLineComplete) {
        int writeY = height - 1;  // Começamos do fundo

        for (int readY = height - 1; readY >= 0; readY--) {
            if (!isLineComplete[readY]) {
                if (writeY != readY) {
                    System.arraycopy(grid[readY], 0, grid[writeY], 0, width);
                }
                writeY--;
            }
        }

        // Preenche as linhas restantes com células vazias
        while (writeY >= 0) {
            for (int x = 0; x < width; x++) {
                grid[writeY][x] = EMPTY_CELL;
            }
            writeY--;
        }
    }

    /**
     * Limpa todo o tabuleiro, preenchendo com células vazias.
     */
    public void clearGrid() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = EMPTY_CELL;
            }
        }
        hasChanges = true;
        notifyBoardUpdated();
    }

    /**
     * Notifica que o tabuleiro foi atualizado, enviando uma cópia do estado atual.
     * Só envia notificação se houve mudanças desde a última notificação.
     */
    public void notifyBoardUpdated() {
        if (hasChanges) {
            int[][] gridCopy = new int[height][width];
            for (int y = 0; y < height; y++) {
                System.arraycopy(grid[y], 0, gridCopy[y], 0, width);
            }

            mediator.emit(UiEvents.BOARD_UPDATE, new UiEvents.BoardUpdateEvent(playerId, gridCopy));
            hasChanges = false;
        }
    }

    /**
     * Força a notificação de atualização do tabuleiro, mesmo sem mudanças.
     */
    public void forceBoardUpdate() {
        hasChanges = true;
        notifyBoardUpdated();
    }
}

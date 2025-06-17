package com.uneb.tetris.ui.components;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.uneb.tetris.configuration.GameConfig;
import com.uneb.tetris.ui.theme.TetrominoColors;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 * {@code BoardCanvas} é um componente visual customizado baseado em {@link Canvas},
 * responsável pela renderização otimizada do tabuleiro de jogo do Tetris.
 *
 * <p>Utiliza cache de imagens para melhorar desempenho de desenho e permite atualizações
 * incrementais apenas das células modificadas.</p>
 *
 * @author Bruno Bispo
 */
public class BoardCanvas extends Canvas {

    /** Cache global de imagens por tipo de célula com tamanho padrão (30px). */
    private static final Map<Integer, Image> IMAGE_CACHE = new HashMap<>();

    static {
        int cellSize = 30;
        for (int type = 0; type <= 9; type++) {
            IMAGE_CACHE.put(type, createCellImage(type, cellSize));
        }
    }

    private final int width;
    private final int height;
    private final int cellSize;

    /** Cache local de imagens baseado no tamanho da célula do canvas atual. */
    private final Map<Integer, Image> instanceImageCache = new HashMap<>();

    /** Grid anterior utilizado para otimizar o redesenho do canvas. */
    private int[][] previousGrid;

    private boolean firstDraw = true;

    /**
     * Construtor que inicializa o canvas do tabuleiro com as dimensões informadas.
     *
     * @param width número de colunas do tabuleiro
     * @param height número de linhas do tabuleiro
     * @param cellSize tamanho de cada célula (em pixels)
     */
    public BoardCanvas(int width, int height, int cellSize) {
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;

        setWidth(width * cellSize);
        setHeight(height * cellSize);

        for (int type = 0; type <= 9; type++) {
            if (cellSize == 30) {
                instanceImageCache.put(type, IMAGE_CACHE.get(type));
            } else {
                instanceImageCache.put(type, createCellImage(type, cellSize));
            }
        }

        previousGrid = new int[height][width];
        for (int[] row : previousGrid) Arrays.fill(row, -1);
    }

    /**
     * Cria uma imagem de célula com o tipo e tamanho especificado.
     *
     * @param type código do tipo do tetromino
     * @param cellSize tamanho da célula
     * @return imagem da célula renderizada
     */
    private static Image createCellImage(int type, int cellSize) {
        Canvas cellCanvas = new Canvas(cellSize, cellSize);
        GraphicsContext gc = cellCanvas.getGraphicsContext2D();

        int spacing = 1;
        int innerSize = cellSize - (spacing * 2);

        gc.setFill(Color.web("#15202b"));
        gc.fillRect(0, 0, cellSize, cellSize);

        if (type != 0) {
            Color tetroColor = TetrominoColors.getColor(type);
            gc.setFill(tetroColor);
            gc.fillRoundRect(spacing, spacing, innerSize, innerSize, 10, 10);
            gc.setLineWidth(0.5);
            gc.strokeRoundRect(spacing, spacing, innerSize, innerSize, 10, 10);
        } else {
            gc.setFill(Color.web("#15202b"));
            gc.fillRoundRect(spacing, spacing, innerSize, innerSize, 8, 8);
            gc.setStroke(Color.rgb(255, 255, 255, 0.05));
            gc.setLineWidth(0.5);
            gc.strokeRoundRect(spacing, spacing, innerSize, innerSize, 8, 8);
        }

        return cellCanvas.snapshot(null, null);
    }

    /**
     * Atualiza o estado visual do canvas com base no grid atual de jogo.
     *
     * @param grid matriz bidimensional representando o estado do tabuleiro
     */
    public void updateBoard(int[][] grid) {
        GraphicsContext gc = getGraphicsContext2D();

        if (firstDraw) {
            drawBackground(gc);
            firstDraw = false;
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (previousGrid[y][x] != grid[y][x]) {
                    gc.drawImage(instanceImageCache.get(grid[y][x]), x * cellSize, y * cellSize);
                }
            }
        }

        previousGrid = Arrays.stream(grid).map(int[]::clone).toArray(int[][]::new);
    }

    /**
     * Renderiza o plano de fundo do canvas com gradiente e bordas estilizadas.
     *
     * @param gc contexto gráfico do canvas
     */
    private void drawBackground(GraphicsContext gc) {
        gc.setFill(Color.web("#15202b"));
        gc.fillRect(0, 0, getWidth(), getHeight());

        Stop[] stops = new Stop[] {
                new Stop(0, Color.web("#1e2b38")),
                new Stop(1, Color.web("#14202c"))
        };
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);

        gc.setFill(gradient);
        gc.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

        gc.setStroke(Color.web("#2c3e50"));
        gc.setLineWidth(10);
        gc.strokeRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
    }

    /**
     * Limpa o conteúdo do canvas, restaurando o fundo e redesenhando todas as células como vazias.
     */
    public void clearBoard() {
        GraphicsContext gc = getGraphicsContext2D();
        drawBackground(gc);
        firstDraw = false;

        Image emptyCell = instanceImageCache.get(0);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                gc.drawImage(emptyCell, x * cellSize, y * cellSize);
            }
        }
    }
}

package com.uneb.tetris.ui.components;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    /** Cache de cores associadas aos tipos de tetromino. */
    private static final Map<Integer, Color> COLOR_CACHE = new HashMap<>();

    /** Cache global de imagens por tipo de célula com tamanho padrão (30px). */
    private static final Map<Integer, Image> IMAGE_CACHE = new HashMap<>();

    static {
        COLOR_CACHE.put(1, Color.web("#00f0f0")); // I - Ciano
        COLOR_CACHE.put(2, Color.web("#1a75ff")); // J - Azul
        COLOR_CACHE.put(3, Color.web("#ff8c00")); // L - Laranja
        COLOR_CACHE.put(4, Color.web("#ffd700")); // O - Amarelo
        COLOR_CACHE.put(5, Color.web("#32cd32")); // S - Verde
        COLOR_CACHE.put(6, Color.web("#bf3eff")); // T - Roxo
        COLOR_CACHE.put(7, Color.web("#ffcbdb")); // Z - Rosa
        COLOR_CACHE.put(8, Color.web("#ff3030")); // X - Vermelho
        COLOR_CACHE.put(9, Color.web("rgba(255, 255, 255, 0.15)")); // Ghost

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
            Color tetroColor = getTetrominoColor(type);
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

    /**
     * Retorna a cor associada ao tipo de tetromino especificado.
     *
     * @param type tipo de peça (1 a 8) ou valor especial (9 = ghost)
     * @return cor correspondente ou {@link Color#TRANSPARENT} se inválido
     */
    private static Color getTetrominoColor(int type) {
        return COLOR_CACHE.getOrDefault(type, Color.TRANSPARENT);
    }
}

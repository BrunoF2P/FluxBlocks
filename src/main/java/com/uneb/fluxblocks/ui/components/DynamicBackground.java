package com.uneb.fluxblocks.ui.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.piece.entities.Cell;
import com.uneb.fluxblocks.piece.factory.BlockShapeFactory;
import com.uneb.fluxblocks.ui.theme.BlockShapeColors;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Componente de fundo dinâmico do jogo.
 * Desenha um fundo com blocos, partículas e gradientes.
 */
public class DynamicBackground {

    private final Entity backgroundEntity;
    private final DynamicBackgroundComponent backgroundComponent;


    public static class DynamicBackgroundComponent extends Component {
        private static final int MIN_BLOCK_SIZE = 40;
        private static final int MAX_BLOCK_SIZE = 120;
        private static final int CORNER_RADIUS = 8;

        private final Canvas canvas;
        private final GraphicsContext gc;
        private final Random random = new Random();
        private int blockSize;
        private int blockGap;
        private double width;
        private double height;

        public DynamicBackgroundComponent(double width, double height) {
            this.width = width;
            this.height = height;
            this.canvas = new Canvas(width, height);
            this.gc = canvas.getGraphicsContext2D();
            gc.setImageSmoothing(true);
            calculateBlockSize(width, height);
        }

        @Override
        public void onAdded() {
            entity.getViewComponent().addChild(canvas);
            initialize();
        }

        private void calculateBlockSize(double width, double height) {
            double screenArea = width * height;
            double baseSize = Math.sqrt(screenArea) * 0.02;

            this.blockSize = (int) Math.max(MIN_BLOCK_SIZE, Math.min(MAX_BLOCK_SIZE, baseSize));
            this.blockGap = Math.max(1, blockSize / 20);
        }

        private void initialize() {
            redraw();
        }

        public void resize(double newWidth, double newHeight) {
            this.width = newWidth;
            this.height = newHeight;
            canvas.setWidth(newWidth);
            canvas.setHeight(newHeight);
            calculateBlockSize(newWidth, newHeight);
            redraw();
        }

        private void redraw() {
            if (width <= 0 || height <= 0) return;

            gc.clearRect(0, 0, width, height);

            drawBackgroundGradient(width, height);
            drawGrid(width, height);
            drawStackedPieces(width, height);
            drawFloatingParticles(width, height);
        }

        private void drawBackgroundGradient(double width, double height) {
            gc.setFill(Color.rgb(15, 22, 30, 0.85));
            gc.fillRect(0, 0, width, height);

            RadialGradient mainRadial = new RadialGradient(
                    0, 0, width * 0.35, height * 0.35, Math.max(width, height) * 0.8,
                    false, CycleMethod.NO_CYCLE,
                    new Stop(0.0, Color.rgb(45, 60, 80, 0.9)),
                    new Stop(0.08, Color.rgb(40, 55, 75, 0.88)),
                    new Stop(0.16, Color.rgb(35, 50, 70, 0.86)),
                    new Stop(0.24, Color.rgb(30, 45, 65, 0.84)),
                    new Stop(0.32, Color.rgb(25, 40, 60, 0.82)),
                    new Stop(0.4, Color.rgb(22, 35, 55, 0.8)),
                    new Stop(0.5, Color.rgb(18, 30, 50, 0.78)),
                    new Stop(0.6, Color.rgb(15, 25, 45, 0.76)),
                    new Stop(0.7, Color.rgb(12, 22, 40, 0.74)),
                    new Stop(0.8, Color.rgb(10, 18, 35, 0.72)),
                    new Stop(0.9, Color.rgb(8, 15, 30, 0.7)),
                    new Stop(1.0, Color.rgb(5, 12, 25, 0.68))
            );
            gc.setFill(mainRadial);
            gc.fillRect(0, 0, width, height);

            RadialGradient sideRadial = new RadialGradient(
                    0, 0, width * 0.8, height * 0.2, Math.max(width, height) * 0.5,
                    false, CycleMethod.NO_CYCLE,
                    new Stop(0.0, Color.rgb(64, 224, 208, 0.10)),
                    new Stop(0.5, Color.rgb(64, 224, 208, 0.04)),
                    new Stop(1.0, Color.rgb(64, 224, 208, 0.0))
            );
            gc.setFill(sideRadial);
            gc.fillRect(0, 0, width, height);

            LinearGradient verticalOverlay = new LinearGradient(
                    0, 0, 0, height, false, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.rgb(100, 200, 255, 0.04)),
                    new Stop(0.2, Color.rgb(80, 180, 220, 0.03)),
                    new Stop(0.4, Color.rgb(60, 160, 200, 0.02)),
                    new Stop(0.6, Color.rgb(0, 0, 0, 0)),
                    new Stop(0.8, Color.rgb(120, 200, 180, 0.02)),
                    new Stop(1, Color.rgb(150, 220, 200, 0.03))
            );
            gc.setFill(verticalOverlay);
            gc.fillRect(0, 0, width, height);

            LinearGradient diagonalOverlay = new LinearGradient(
                    0, 0, width, height, false, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.rgb(255, 255, 255, 0.03)),
                    new Stop(0.5, Color.rgb(0, 0, 0, 0)),
                    new Stop(1, Color.rgb(64, 224, 208, 0.04))
            );
            gc.setFill(diagonalOverlay);
            gc.fillRect(0, 0, width, height);

            addSubtleNoise(width, height);
        }

        private void addSubtleNoise(double width, double height) {
            Random random = new Random();
            gc.setGlobalAlpha(0.008);

            for (int i = 0; i < (width * height) / 2000; i++) {
                double x = random.nextDouble() * width;
                double y = random.nextDouble() * height;

                int variation = random.nextInt(40) - 20;
                Color noiseColor = Color.rgb(
                        Math.max(0, Math.min(255, 20 + variation)),
                        Math.max(0, Math.min(255, 30 + variation)),
                        Math.max(0, Math.min(255, 40 + variation))
                );

                gc.setFill(noiseColor);
                gc.fillOval(x, y, 1, 1);
            }

            gc.setGlobalAlpha(1.0);
        }

        private void drawGrid(double width, double height) {
            gc.setStroke(Color.rgb(255, 255, 255, 0.08));
            gc.setLineWidth(0.5);

            for (double x = 0; x < width; x += blockSize) {
                gc.strokeLine(x, 0, x, height);
            }

            for (double y = 0; y < height; y += blockSize) {
                gc.strokeLine(0, y, width, y);
            }
        }

        private void drawStackedPieces(double width, double height) {
            int gridCols = (int) (width / blockSize);
            int gridRows = (int) (height / blockSize);

            boolean[][] occupiedGrid = new boolean[gridCols][gridRows];

            fillBaseLines(gridCols, gridRows, occupiedGrid);
            addFallingPieces(gridCols, gridRows, occupiedGrid);
        }

        private void fillBaseLines(int gridCols, int gridRows, boolean[][] occupiedGrid) {
            for (int row = gridRows - 1; row >= gridRows - 2; row--) {
                for (int col = 0; col < gridCols; col++) {
                    BlockShape singleBlock = createSingleBlock();

                    if (canPlacePiece(singleBlock, col, row, occupiedGrid)) {
                        markPiecePositions(singleBlock, col, row, occupiedGrid);
                        drawPiece(gc, singleBlock, col, row, false);
                    }
                }
            }

            int partialFill = (int) (gridCols * 0.7);
            for (int col = 0; col < partialFill; col++) {
                if (random.nextDouble() > 0.3) {
                    BlockShape singleBlock = createSingleBlock();

                    if (canPlacePiece(singleBlock, col, gridRows - 3, occupiedGrid)) {
                        markPiecePositions(singleBlock, col, gridRows - 3, occupiedGrid);
                        drawPiece(gc, singleBlock, col, gridRows - 3, false);
                    }
                }
            }
        }

        private BlockShape createSingleBlock() {
            List<Cell> cells = new ArrayList<>();
            int colorType = random.nextInt(7);
            cells.add(new Cell(0, 0, colorType));
            return new BlockShape(cells, colorType + 1);
        }

        private void addFallingPieces(int gridCols, int gridRows, boolean[][] occupiedGrid) {
            List<BlockShape.Type> availableTypes = List.of(
                    BlockShape.Type.I,
                    BlockShape.Type.O,
                    BlockShape.Type.T,
                    BlockShape.Type.L,
                    BlockShape.Type.J,
                    BlockShape.Type.S,
                    BlockShape.Type.Z
            );

            int numFallingPieces = Math.max(3, (gridCols * gridRows) / 100);

            for (int i = 0; i < numFallingPieces; i++) {
                int col = random.nextInt(gridCols - 4);
                int startRow = random.nextInt(gridRows / 3);

                BlockShape.Type type = availableTypes.get(random.nextInt(availableTypes.size()));
                BlockShape piece = BlockShapeFactory.createBlockShape(type);

                int rotations = random.nextInt(4);
                for (int r = 0; r < rotations; r++) {
                    piece = rotatePiece(piece);
                }

                int finalRow = dropPiece(piece, col, occupiedGrid);

                if (finalRow != -1) {
                    markPiecePositions(piece, col, finalRow, occupiedGrid);
                    drawPiece(gc, piece, col, finalRow, isTopPiece(piece, col, finalRow, occupiedGrid));
                }
            }
        }

        private int dropPiece(BlockShape piece, int col, boolean[][] occupiedGrid) {
            int gridRows = occupiedGrid[0].length;

            for (int row = 0; row < gridRows; row++) {
                if (canPlacePiece(piece, col, row, occupiedGrid)) {
                    if (isPieceGrounded(piece, col, row, occupiedGrid)) {
                        return row;
                    }
                }
            }
            return -1;
        }

        private boolean isPieceGrounded(BlockShape piece, int gridX, int gridY, boolean[][] occupiedGrid) {
            int gridRows = occupiedGrid[0].length;

            for (Cell cell : piece.getCells()) {
                int x = gridX + cell.getX();
                int y = gridY + cell.getY();

                if (y >= gridRows - 1) {
                    return true;
                }

                if (y + 1 < gridRows && occupiedGrid[x][y + 1]) {
                    return true;
                }
            }
            return false;
        }

        private boolean isTopPiece(BlockShape piece, int gridX, int gridY, boolean[][] occupiedGrid) {
            for (Cell cell : piece.getCells()) {
                int x = gridX + cell.getX();
                int y = gridY + cell.getY();

                if (y > 0 && occupiedGrid[x][y - 1]) {
                    return false;
                }
            }
            return true;
        }

        private BlockShape rotatePiece(BlockShape original) {
            List<Cell> rotatedCells = new ArrayList<>();

            for (Cell cell : original.getCells()) {
                rotatedCells.add(new Cell(-cell.getY(), cell.getX(), cell.getType()));
            }

            int minX = rotatedCells.stream().mapToInt(Cell::getX).min().orElse(0);
            int minY = rotatedCells.stream().mapToInt(Cell::getY).min().orElse(0);

            List<Cell> normalizedCells = new ArrayList<>();
            for (Cell cell : rotatedCells) {
                normalizedCells.add(new Cell(
                        cell.getX() - minX,
                        cell.getY() - minY,
                        cell.getType()
                ));
            }

            return new BlockShape(normalizedCells, original.getType());
        }

        private boolean canPlacePiece(BlockShape piece, int gridX, int gridY, boolean[][] occupiedGrid) {
            for (Cell cell : piece.getCells()) {
                int x = gridX + cell.getX();
                int y = gridY + cell.getY();

                if (x < 0 || x >= occupiedGrid.length || y < 0 || y >= occupiedGrid[0].length) {
                    return false;
                }

                if (occupiedGrid[x][y]) {
                    return false;
                }
            }
            return true;
        }

        private void markPiecePositions(BlockShape piece, int gridX, int gridY, boolean[][] occupiedGrid) {
            for (Cell cell : piece.getCells()) {
                int x = gridX + cell.getX();
                int y = gridY + cell.getY();

                if (x >= 0 && x < occupiedGrid.length && y >= 0 && y < occupiedGrid[0].length) {
                    occupiedGrid[x][y] = true;
                }
            }
        }

        private void drawPiece(GraphicsContext gc, BlockShape piece, int gridX, int gridY, boolean isTop) {
            for (Cell cell : piece.getCells()) {
                int x = gridX + cell.getX();
                int y = gridY + cell.getY();

                double canvasX = x * blockSize;
                double canvasY = y * blockSize;

                Color color = BlockShapeColors.getColor(cell.getType() + 1);

                drawBlock(gc, canvasX, canvasY, color, isTop);
            }

            if (isTop) {
                gc.restore();
            }
        }

        private void drawFloatingParticles(double width, double height) {
            int particleCount = (int) ((width * height) / 50000);

            for (int i = 0; i < particleCount; i++) {
                double x = random.nextDouble() * width;
                double y = random.nextDouble() * height;
                double size = 1 + random.nextDouble() * 3;

                RadialGradient particleGradient = new RadialGradient(
                        0, 0, x, y, size * 2, false, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.rgb(64, 224, 208, 0.3)),
                        new Stop(1, Color.rgb(64, 224, 208, 0))
                );

                gc.setFill(particleGradient);
                gc.fillOval(x - size, y - size, size * 2, size * 2);
            }
        }

        private void drawBlock(GraphicsContext gc, double x, double y, Color color, boolean isTop) {
            double actualSize = blockSize - blockGap;
            x += (double) blockGap / 2;
            y += (double) blockGap / 2;

            LinearGradient gradient = new LinearGradient(
                    x, y, x, y + actualSize, false, CycleMethod.NO_CYCLE,
                    new Stop(0, color.brighter()),
                    new Stop(0.3, color),
                    new Stop(1, color.darker())
            );

            gc.setFill(gradient);
            gc.fillRoundRect(x, y, actualSize, actualSize, CORNER_RADIUS, CORNER_RADIUS);
            gc.restore();

            gc.setStroke(Color.rgb(255, 255, 255, 0.25));
            gc.setLineWidth(1.5);
            gc.strokeRoundRect(x + 1, y + 1, actualSize - 2, actualSize - 2,
                    CORNER_RADIUS - 1, CORNER_RADIUS - 1);
        }

        public Canvas getCanvas() {
            return canvas;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }
    }


    public DynamicBackground(double width, double height) {
        this.backgroundComponent = new DynamicBackgroundComponent(width, height);

        this.backgroundEntity = FXGL.entityBuilder()
                .at(0, 0)
                .with(backgroundComponent)
                .buildAndAttach();
    }

    /**
     * Redimensiona o fundo dinâmico.
     *
     * @param width  Nova largura
     * @param height Nova altura
     */
    public void resize(double width, double height) {
        backgroundComponent.resize(width, height);
    }

    /**
     * Retorna o canvas do fundo dinâmico.
     *
     * @return Canvas do fundo
     */
    public Canvas getCanvas() {
        return backgroundComponent.getCanvas();
    }

    /**
     * Retorna a largura atual.
     *
     * @return Largura
     */
    public double getWidth() {
        return backgroundComponent.getWidth();
    }

    /**
     * Retorna a altura atual.
     *
     * @return Altura
     */
    public double getHeight() {
        return backgroundComponent.getHeight();
    }

    /**
     * Destrói a entidade e limpa recursos.
     */
    public void destroy() {
        if (backgroundEntity != null && backgroundEntity.isActive()) {
            backgroundEntity.removeFromWorld();
        }
    }


    public Entity getEntity() {
        return backgroundEntity;
    }


    public DynamicBackgroundComponent getComponent() {
        return backgroundComponent;
    }

}
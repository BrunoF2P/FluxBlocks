package com.uneb.fluxblocks.ui.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.uneb.fluxblocks.architecture.events.UiEvents;
import com.uneb.fluxblocks.architecture.mediators.GameMediator;
import com.uneb.fluxblocks.piece.entities.BlockShape;
import com.uneb.fluxblocks.piece.entities.Cell;
import com.uneb.fluxblocks.ui.theme.BlockShapeColors;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Componente responsável por exibir uma prévia da próxima peça no jogo.
 * Ele escuta eventos de atualização da próxima peça e renderiza a visualização correspondente.
 */
public class NextPiecePreview {

    private final Entity previewEntity;
    private final NextPiecePreviewComponent previewComponent;


    public static class NextPiecePreviewComponent extends Component {
        private final int playerId;
        private final GameMediator mediator;
        private final StackPane container;
        private final int cellSize = 30;
        private final GameMediator.Listener<UiEvents.NextPieceEvent> nextPieceListener;

        public NextPiecePreviewComponent(GameMediator mediator, StackPane container, int playerId) {
            this.mediator = mediator;
            this.container = container;
            this.playerId = playerId;
            this.nextPieceListener = ev -> {
                if (ev.playerId() != this.playerId) return;
                updateNextPiecePreview(ev.nextPiece());
            };
            initializePreview();
        }

        @Override
        public void onAdded() {
            mediator.receiver(UiEvents.NEXT_PIECE_UPDATE, nextPieceListener);
        }

        @Override
        public void onRemoved() {
            destroy();
        }

        private void initializePreview() {
            container.setAlignment(Pos.CENTER);
        }

        public void updateNextPiecePreview(BlockShape nextPiece) {
            container.getChildren().clear();

            if (nextPiece == null) {
                return;
            }

            int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
            int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

            for (Cell cell : nextPiece.getCells()) {
                minX = Math.min(minX, cell.getRelativeX());
                maxX = Math.max(maxX, cell.getRelativeX());
                minY = Math.min(minY, cell.getRelativeY());
                maxY = Math.max(maxY, cell.getRelativeY());
            }

            int pieceWidth = maxX - minX + 1;
            int pieceHeight = maxY - minY + 1;

            Canvas previewCanvas = new Canvas(pieceWidth * cellSize, pieceHeight * cellSize);
            GraphicsContext gc = previewCanvas.getGraphicsContext2D();

            for (Cell cell : nextPiece.getCells()) {
                int x = cell.getRelativeX() - minX;
                int y = cell.getRelativeY() - minY;
                int cellType = nextPiece.isGlass() ? 10 : cell.getType();
                drawCell(gc, x, y, cellType);
            }

            container.getChildren().add(previewCanvas);
        }

        private void drawCell(GraphicsContext gc, int x, int y, int type) {
            int spacing = 1;
            int innerSize = cellSize - (spacing * 2);

            if (type == 10) {
                Color glassColor = BlockShapeColors.getGlassColor();
                gc.setFill(glassColor);
                gc.fillRoundRect(
                        x * cellSize + spacing,
                        y * cellSize + spacing,
                        innerSize,
                        innerSize,
                        10, 10);
            
                // Gradiente de brilho
                gc.save();
                gc.setGlobalAlpha(0.35);
                javafx.scene.paint.LinearGradient shine = new javafx.scene.paint.LinearGradient(
                    x * cellSize + spacing, y * cellSize + spacing, 
                    x * cellSize + spacing, y * cellSize + spacing + innerSize / 2, 
                    false, javafx.scene.paint.CycleMethod.NO_CYCLE,
                    new javafx.scene.paint.Stop(0, Color.rgb(255, 255, 255, 0.9)),
                    new javafx.scene.paint.Stop(1, Color.rgb(255, 255, 255, 0.0))
                );
                gc.setFill(shine);
                gc.fillRoundRect(
                        x * cellSize + spacing,
                        y * cellSize + spacing,
                        innerSize,
                        innerSize / 2,
                        10, 10);
                gc.restore();
            
                // Rachaduras
                gc.save();
                gc.setStroke(Color.rgb(255, 255, 255, 0.25));
                gc.setLineWidth(0.8);
            
                double cx = x * cellSize + spacing + innerSize / 2;
                double cy = y * cellSize + spacing + innerSize / 2;
                int cracks = 6;
                double radius = innerSize / 2;
            
                for (int i = 0; i < cracks; i++) {
                    double angle = Math.toRadians(360.0 / cracks * i + Math.random() * 15 - 7.5);
                    double ex = cx + Math.cos(angle) * radius;
                    double ey = cy + Math.sin(angle) * radius;
                    gc.strokeLine(cx, cy, ex, ey);
            
                    double branchAngle1 = angle + Math.toRadians(20 + Math.random() * 10);
                    double bx1 = cx + Math.cos(branchAngle1) * (radius * 0.5);
                    double by1 = cy + Math.sin(branchAngle1) * (radius * 0.5);
                    gc.strokeLine((cx + ex) / 2, (cy + ey) / 2, bx1, by1);
            
                    double branchAngle2 = angle - Math.toRadians(20 + Math.random() * 10);
                    double bx2 = cx + Math.cos(branchAngle2) * (radius * 0.4);
                    double by2 = cy + Math.sin(branchAngle2) * (radius * 0.4);
                    gc.strokeLine((cx + ex) / 2, (cy + ey) / 2, bx2, by2);
                }
            
                gc.restore();
            } else {
                Color tetroColor = BlockShapeColors.getColor(type);

                gc.setFill(tetroColor);
                gc.fillRoundRect(
                        x * cellSize + spacing,
                        y * cellSize + spacing,
                        innerSize,
                        innerSize,
                        10, 10);

                gc.setStroke(Color.web("#ffffff", 0.3));
                gc.setLineWidth(0.5);
                gc.strokeRoundRect(
                        x * cellSize + spacing,
                        y * cellSize + spacing,
                        innerSize,
                        innerSize,
                        10, 10);
            }
        }

        public void destroy() {
            if (container != null) {
                container.getChildren().clear();
            }

            if (mediator != null) {
                mediator.removeReceiver(UiEvents.NEXT_PIECE_UPDATE, nextPieceListener);
            }
        }

        public StackPane getContainer() {
            return container;
        }

        public int getPlayerId() {
            return playerId;
        }

        public GameMediator getMediator() {
            return mediator;
        }
    }


    public NextPiecePreview(GameMediator mediator, StackPane container, int playerId) {
        this.previewComponent = new NextPiecePreviewComponent(mediator, container, playerId);

        this.previewEntity = FXGL.entityBuilder()
                .at(0, 0)
                .with(previewComponent)
                .buildAndAttach();
    }

    /**
     * Atualiza a prévia da próxima peça.
     *
     * @param nextPiece Próxima peça a ser exibida
     */
    public void updateNextPiecePreview(BlockShape nextPiece) {
        previewComponent.updateNextPiecePreview(nextPiece);
    }

    /**
     * Retorna o container da prévia.
     *
     * @return StackPane do container
     */
    public StackPane getContainer() {
        return previewComponent.getContainer();
    }

    /**
     * Retorna o ID do jogador.
     *
     * @return ID do jogador
     */
    public int getPlayerId() {
        return previewComponent.getPlayerId();
    }

    /**
     * Retorna o mediator.
     *
     * @return GameMediator
     */
    public GameMediator getMediator() {
        return previewComponent.getMediator();
    }

    /**
     * Destrói a entidade e limpa recursos.
     */
    public void destroy() {
        if (previewEntity != null && previewEntity.isActive()) {
            previewEntity.removeFromWorld();
        }
    }


    public Entity getEntity() {
        return previewEntity;
    }

}
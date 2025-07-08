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
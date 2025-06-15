package com.uneb.tetris.ui.components;

import com.uneb.tetris.architecture.events.UiEvents;
import com.uneb.tetris.architecture.mediators.GameMediator;
import com.uneb.tetris.piece.entities.Cell;
import com.uneb.tetris.piece.entities.Tetromino;
import com.uneb.tetris.ui.theme.TetrominoColors;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Componente visual responsável por exibir a próxima peça que será inserida no tabuleiro.
 * Utiliza um {@link StackPane} para desenhar dinamicamente a prévia com base na peça recebida via eventos do {@link GameMediator}.
 * <p>
 * @author Bruno Bispo
 */
public class NextPiecePreview {
    private final int playerId;
    private final GameMediator mediator;
    private final StackPane container;
    private final int cellSize = 30;
    private final GameMediator.Listener<UiEvents.NextPieceEvent> nextPieceListener;
    /**
     * Construtor do componente de pré-visualização da próxima peça.
     *
     * @param mediator  Instância do {@link GameMediator} responsável por gerenciar os eventos do jogo.
     * @param container Componente gráfico onde a peça será desenhada.
     */
    public NextPiecePreview(GameMediator mediator, StackPane container, int playerId) {
        this.mediator = mediator;
        this.container = container;
        this.playerId = playerId;
        this.nextPieceListener = ev -> {
            if (ev.playerId() != this.playerId) return;
            updateNextPiecePreview(ev.nextPiece());
        };
        initializePreview();
    }

    /**
     * Inicializa o componente, registrando os eventos necessários.
     * Deve ser chamado após a injeção do componente na hierarquia da UI.
     */
    public void initialize() {
        mediator.receiver(UiEvents.NEXT_PIECE_UPDATE, nextPieceListener);
    }

    /**
     * Define o alinhamento central do container para a renderização da próxima peça.
     */
    private void initializePreview() {
        container.setAlignment(Pos.CENTER);
    }


    /**
     * Atualiza a renderização da próxima peça na área de preview.
     *
     * @param nextPiece Instância da {@link Tetromino} que será exibida. Pode ser {@code null}.
     */
    public void updateNextPiecePreview(Tetromino nextPiece) {
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
            drawCell(gc, x, y, cell.getType());
        }

        container.getChildren().add(previewCanvas);
    }

    /**
     * Desenha uma célula individual da peça no {@link GraphicsContext}.
     *
     * @param gc   Contexto gráfico para renderização.
     * @param x    Coordenada X relativa da célula.
     * @param y    Coordenada Y relativa da célula.
     * @param type Tipo da célula (identificador da peça).
     */
    private void drawCell(GraphicsContext gc, int x, int y, int type) {
        int spacing = 1;
        int innerSize = cellSize - (spacing * 2);

        Color tetroColor = TetrominoColors.getColor(type);

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

    /**
     * Limpa recursos e listeners para evitar vazamentos de memória.
     * Chame este método quando o componente não for mais necessário.
     */
    public void destroy() {
        // Limpa o container
        if (container != null) {
            container.getChildren().clear();
        }

        // Remove o listener do mediator
        if (mediator != null) {
            mediator.removeReceiver(UiEvents.NEXT_PIECE_UPDATE, nextPieceListener);
        }
    }
}

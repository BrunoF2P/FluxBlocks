package com.uneb.tetris.ui.components;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Componente ultra-otimizado para exibição de tempo com alta taxa de atualização.
 * Usa um único Canvas com buffer interno para máxima performance.
 */
public class TimeDisplay extends StackPane {
    
    private Canvas canvas;
    private GraphicsContext gc;
    private String currentTime = "00:00:000";
    private Color textColor = Color.web("#fcd34d");
    private Color shadowColor = Color.color(0, 0, 0, 0.6);
    private Font timeFont;
    
    private double textX;
    private double textY;
    private double shadowX;
    private double shadowY;
    
    private final Text textMeasurer = new Text();
    
    /**
     * Cria um novo componente de exibição de tempo otimizado.
     * 
     * @param width Largura do componente
     * @param height Altura do componente
     */
    public TimeDisplay(double width, double height) {
        super();
        setPrefSize(width, height);
        
        canvas = new Canvas(width, height);
        
        timeFont = Font.loadFont(getClass().getResourceAsStream("/assets/ui/fonts/thatsoundsgreat.ttf"), 24);
        
        gc = canvas.getGraphicsContext2D();
        gc.setFont(timeFont);
        gc.setTextAlign(TextAlignment.CENTER);
        
        textMeasurer.setFont(timeFont);
        textMeasurer.setText(currentTime);
        
        calculateTextPositions(width, height);
        
        renderTime();
        
        getChildren().add(canvas);
        
        getStyleClass().add("score-text");
    }
    
    /**
     * Calcula as posições do texto para centralizá-lo no canvas
     */
    private void calculateTextPositions(double width, double height) {
        textX = width / 2;
        shadowX = textX + 1;
        
        textY = height / 2 + 10;
        shadowY = textY + 1;
    }
    
    /**
     * Renderiza o tempo no canvas com limpeza completa.
     */
    private void renderTime() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        gc.setFill(shadowColor);
        gc.fillText(currentTime, shadowX, shadowY);
        
        gc.setFill(textColor);
        gc.fillText(currentTime, textX, textY);
    }
    
    /**
     * Atualiza o tempo exibido.
     * Este método é otimizado para ser chamado frequentemente.
     * 
     * @param newTime O novo tempo a ser exibido
     */
    public void updateTime(String newTime) {
        if (!currentTime.equals(newTime)) {
            currentTime = newTime;
            renderTime();
        }
    }
    
    /**
     * Ajusta as cores do texto para corresponder ao estilo da UI.
     * 
     * @param textColor Cor principal do texto
     * @param shadowColor Cor da sombra do texto
     */
    public void setColors(Color textColor, Color shadowColor) {
        this.textColor = textColor;
        this.shadowColor = shadowColor;
        renderTime();
    }
    
    @Override
    public void resize(double width, double height) {
        super.resize(width, height);
        
        if (canvas.getWidth() != width || canvas.getHeight() != height) {
            canvas.setWidth(width);
            canvas.setHeight(height);
            calculateTextPositions(width, height);
            renderTime();
        }
    }
}
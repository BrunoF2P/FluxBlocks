package com.uneb.fluxblocks.ui.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.uneb.fluxblocks.configuration.GameConfig;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Componente responsável por exibir o tempo no jogo.
 * Ele renderiza o tempo atual em um canvas com efeitos de sombra e cor.
 */
public class TimeDisplay {

    private final Entity timeEntity;
    private final TimeDisplayComponent timeComponent;


    public static class TimeDisplayComponent extends Component {
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
        private final double defaultScale = GameConfig.UI_SCALE;
        private final double width;
        private final double height;

        public TimeDisplayComponent(double width, double height) {
            this.width = width;
            this.height = height;
            setupCanvas();
        }

        @Override
        public void onAdded() {
            entity.getViewComponent().addChild(canvas);
        }

        private void setupCanvas() {
            canvas = new Canvas(width * defaultScale, height * defaultScale);
            timeFont = Font.loadFont(getClass().getResourceAsStream("/assets/ui/fonts/thatsoundsgreat.ttf"), 24 * defaultScale);
            assert canvas != null;
            gc = canvas.getGraphicsContext2D();
            gc.setFont(timeFont);
            gc.setTextAlign(TextAlignment.CENTER);

            textMeasurer.setFont(timeFont);
            textMeasurer.setText(currentTime);

            calculateTextPositions(width * defaultScale, height * defaultScale);
            renderTime();

            canvas.getStyleClass().add("score-text");
        }

        private void calculateTextPositions(double width, double height) {
            textX = width / 2;
            shadowX = textX + 1;

            textY = height / 2 + 10;
            shadowY = textY + 1;
        }

        private void renderTime() {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

            gc.setFill(shadowColor);
            gc.fillText(currentTime, shadowX, shadowY);

            gc.setFill(textColor);
            gc.fillText(currentTime, textX, textY);
        }

        public void updateTime(String newTime) {
            if (!currentTime.equals(newTime)) {
                currentTime = newTime;
                renderTime();
            }
        }

        public void setColors(Color textColor, Color shadowColor) {
            this.textColor = textColor;
            this.shadowColor = shadowColor;
            renderTime();
        }

        public void resize(double newWidth, double newHeight) {
            if (canvas.getWidth() != newWidth || canvas.getHeight() != newHeight) {
                canvas.setWidth(newWidth);
                canvas.setHeight(newHeight);
                calculateTextPositions(newWidth, newHeight);
                renderTime();
            }
        }

        @Override
        public void onRemoved() {
            destroy();
        }

        public void destroy() {
            if (gc != null) {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                gc = null;
            }

            if (canvas != null) {
                entity.getViewComponent().removeChild(canvas);
                canvas = null;
            }

            textMeasurer.setText("");
            textColor = null;
            shadowColor = null;
            timeFont = null;
            currentTime = null;
        }

        public Canvas getCanvas() {
            return canvas;
        }

        public String getCurrentTime() {
            return currentTime;
        }
    }


    public TimeDisplay(double width, double height) {
        this.timeComponent = new TimeDisplayComponent(width, height);

        this.timeEntity = FXGL.entityBuilder()
                .at(0, 0)
                .with(timeComponent)
                .buildAndAttach();
    }

    /**
     * Atualiza o tempo exibido.
     *
     * @param newTime Novo tempo
     */
    public void updateTime(String newTime) {
        timeComponent.updateTime(newTime);
    }

    /**
     * Define as cores do texto.
     *
     * @param textColor   Cor do texto
     * @param shadowColor Cor da sombra
     */
    public void setColors(Color textColor, Color shadowColor) {
        timeComponent.setColors(textColor, shadowColor);
    }

    /**
     * Redimensiona o componente.
     *
     * @param width  Nova largura
     * @param height Nova altura
     */
    public void resize(double width, double height) {
        timeComponent.resize(width, height);
    }

    /**
     * Retorna o canvas do tempo.
     *
     * @return Canvas do tempo
     */
    public Canvas getCanvas() {
        return timeComponent.getCanvas();
    }

    /**
     * Retorna o tempo atual.
     *
     * @return Tempo atual
     */
    public String getCurrentTime() {
        return timeComponent.getCurrentTime();
    }

    /**
     * Destrói a entidade e limpa recursos.
     */
    public void destroy() {
        if (timeEntity != null && timeEntity.isActive()) {
            timeEntity.removeFromWorld();
        }
    }

    public Entity getEntity() {
        return timeEntity;
    }

}
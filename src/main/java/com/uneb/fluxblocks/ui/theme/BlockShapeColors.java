package com.uneb.fluxblocks.ui.theme;

import javafx.scene.paint.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilitária que centraliza as definições de cores dos tetrominós.
 * Evita duplicação de código e mantém consistência visual em toda a aplicação.
 *
 * @author Bruno Bispo
 */
public final class BlockShapeColors {
    private static final Map<Integer, Color> COLOR_CACHE = new HashMap<>();

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
    }

    private BlockShapeColors() {
        // Construtor privado para evitar instanciação
    }

    /**
     * Retorna a cor associada ao tipo de tetrominó especificado.
     *
     * @param type tipo de peça (1 a 8) ou valor especial (9 = ghost)
     * @return cor correspondente ou {@link Color#TRANSPARENT} se inválido
     */
    public static Color getColor(int type) {
        if (type == 10) {
            return getGlassColor();
        }
        return COLOR_CACHE.getOrDefault(type, Color.TRANSPARENT);
    }

    public static Color getGlassColor() {
        return Color.web("rgba(120, 200, 255, 0.45)");
    }
}

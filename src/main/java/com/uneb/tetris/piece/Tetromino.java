package com.uneb.tetris.piece;

import java.util.ArrayList;
import java.util.List;

public class Tetromino implements Shape {
    public enum Type {
        EMPTY(0),
        I(1),
        J(2),
        L(3),
        O(4),
        S(5),
        T(6),
        Z(7);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static Type fromOrdinal(int ordinal) {
            for (Type type : values()) {
                if (type.ordinal() == ordinal) {
                    return type;
                }
            }
            return EMPTY;
        }
    }

    private int centerX;
    private int centerY;
    private final List<Cell> cells;
    private final int typeValue;
    private final Type type;

    /**
     * Construtor que recebe uma lista de células e o valor do tipo.
     * Este construtor é chamado pela TetrominoFactory.
     */
    public Tetromino(List<Cell> cells, int typeValue) {
        this.cells = new ArrayList<>(cells);
        this.typeValue = typeValue;
        this.type = Type.fromOrdinal(typeValue);
        updateCellPositions();
    }

    @Override
    public int getX() {
        return centerX;
    }

    @Override
    public int getY() {
        return centerY;
    }

    public void setY(int y) {
        this.centerY = y;
        updateCellPositions();
    }

    @Override
    public void setPosition(int x, int y) {
        this.centerX = x;
        this.centerY = y;
        updateCellPositions();
    }

    @Override
    public void move(int deltaX, int deltaY) {
        this.centerX += deltaX;
        this.centerY += deltaY;
        updateCellPositions();
    }

    private void updateCellPositions() {
        for (Cell cell : cells) {
            cell.setPosition(centerX + cell.getRelativeX(), centerY + cell.getRelativeY());
        }
    }

    @Override
    public void rotate() {
        if (type == Type.O) {
            return;
        }

        for (Cell cell : cells) {
            cell.rotate();
        }

        updateCellPositions();
    }

    @Override
    public int getType() {
        return typeValue;
    }

    @Override
    public List<Cell> getCells() {
        return cells;
    }

    public List<int[]> getCellPositions() {
        List<int[]> positions = new ArrayList<>();
        for (Cell cell : cells) {
            positions.add(new int[]{ cell.getX(), cell.getY() });
        }
        return positions;
    }
}
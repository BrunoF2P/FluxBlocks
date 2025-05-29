package com.uneb.tetris.piece.entities;

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
    private final List<int[]> initialRelativePositions;
    /**
     * Construtor que recebe uma lista de células e o valor do tipo.
     * Este construtor é chamado pela TetrominoFactory.
     */
    public Tetromino(List<Cell> cells, int typeValue) {
        this.cells = new ArrayList<>(cells);
        this.typeValue = typeValue;
        this.type = Type.fromOrdinal(typeValue);

        this.initialRelativePositions = new ArrayList<>(cells.size());
        for (Cell cell : cells) {
            initialRelativePositions.add(new int[]{cell.getRelativeX(), cell.getRelativeY()});
        }

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

    public void resetState() {
        this.centerX = 0;
        this.centerY = 0;
        resetRotation();
    }

    public void resetRotation() {
        if (type == Type.O) return;

        for (int i = 0; i < cells.size(); i++) {
            Cell cell = cells.get(i);
            int[] initialPos = initialRelativePositions.get(i);
            cell.setRelativePosition(initialPos[0], initialPos[1]);
        }
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
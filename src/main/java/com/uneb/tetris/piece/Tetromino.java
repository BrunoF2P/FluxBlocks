package com.uneb.tetris.piece;

import java.util.ArrayList;
import java.util.Arrays;
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
        Z(7),
        X(8);

        private final int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private int centerX;
    private int centerY;
    private final List<Cell> cells = new ArrayList<>();
    private final Type type;

    public Tetromino(Type type) {
        this.type = type;
        initializeCells();
    }

    private void initializeCells() {
        cells.clear();

        switch (type) {
            case I -> {
                cells.add(new Cell(-1, 0, type.getValue()));
                cells.add(new Cell(0, 0, type.getValue()));
                cells.add(new Cell(1, 0, type.getValue()));
                cells.add(new Cell(2, 0, type.getValue()));
            }
            case J -> {
                cells.add(new Cell(-1, -1, type.getValue()));
                cells.add(new Cell(-1, 0, type.getValue()));
                cells.add(new Cell(0, 0, type.getValue()));
                cells.add(new Cell(1, 0, type.getValue()));
            }
            case L -> {
                cells.add(new Cell(1, -1, type.getValue()));
                cells.add(new Cell(-1, 0, type.getValue()));
                cells.add(new Cell(0, 0, type.getValue()));
                cells.add(new Cell(1, 0, type.getValue()));
            }
            case O -> {
                cells.add(new Cell(0, 0, type.getValue()));
                cells.add(new Cell(1, 0, type.getValue()));
                cells.add(new Cell(0, 1, type.getValue()));
                cells.add(new Cell(1, 1, type.getValue()));
            }
            case S -> {
                cells.add(new Cell(0, -1, type.getValue()));
                cells.add(new Cell(1, -1, type.getValue()));
                cells.add(new Cell(-1, 0, type.getValue()));
                cells.add(new Cell(0, 0, type.getValue()));
            }
            case T -> {
                cells.add(new Cell(0, -1, type.getValue()));
                cells.add(new Cell(-1, 0, type.getValue()));
                cells.add(new Cell(0, 0, type.getValue()));
                cells.add(new Cell(1, 0, type.getValue()));
            }
            case Z -> {
                cells.add(new Cell(-1, -1, type.getValue()));
                cells.add(new Cell(0, -1, type.getValue()));
                cells.add(new Cell(0, 0, type.getValue()));
                cells.add(new Cell(1, 0, type.getValue()));
            }
            case X -> {
                cells.add(new Cell(0, -1, type.getValue()));
                cells.add(new Cell(-1, 0, type.getValue()));
                cells.add(new Cell(0, 0, type.getValue()));
                cells.add(new Cell(1, 0, type.getValue()));
                cells.add(new Cell(0, 1, type.getValue()));
            }
        }
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
        for (Type type1 : Arrays.asList(Type.O, Type.X)) {
            if (type == type1) {
                return;
            }
        }

        for (Cell cell : cells) {
            cell.rotate();
        }

        updateCellPositions();
    }

    @Override
    public int getType() {
        return type.getValue();
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

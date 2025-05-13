package com.uneb.tetris.piece;

import java.util.Collections;
import java.util.List;


public class Cell implements Shape {
    private int x;
    private int y;
    private int relativeX;
    private int relativeY;
    private final int type;

    public Cell(int relativeX, int relativeY, int type) {
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.type = type;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void move(int deltaX, int deltaY) {
        this.x += deltaX;
        this.y += deltaY;
    }

    @Override
    public void rotate() {
        int temp = relativeX;
        relativeX = relativeY;
        relativeY = -temp;
    }

    public int getRelativeX() {
        return relativeX;
    }

    public int getRelativeY() {
        return relativeY;
    }

    @Override
    public int getType() {
        return type;
    }

    public void setRelativeX(int relativeX) {
        this.relativeX = relativeX;
    }

    public void setRelativeY(int relativeY) {
        this.relativeY = relativeY;
    }


    @Override
    public List<Cell> getCells() {
        return Collections.singletonList(this);
    }
}
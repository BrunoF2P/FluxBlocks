package com.uneb.tetris.piece.entities;

import java.util.List;

public interface Shape {
    int getX();
    int getY();
    void setPosition(int x, int y);
    void move(int deltaX, int deltaY);
    void rotate();
    int getType();
    List<Cell> getCells();
}
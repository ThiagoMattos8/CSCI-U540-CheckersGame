package com.vague.checkersgame;

/**
 * Piece.java
 * Represents a single checkers piece.
 * color   : 0 = black, 1 = red
 * row     : 0-7, board row
 * col     : 0-7, board column
 * isTaken : 0 = active, 1 = captured
 * isKing  : 0 = normal, 1 = king
 */

public class Piece {

    int id;
    int color;
    int row;
    int col;
    int isTaken;
    int isKing;

    public Piece(int id, int color) {
        this.id = id;
        this.color = color;
        this.row = 0;
        this.col = 0;
        this.isTaken = 0;
        this.isKing = 0;
    }

    public Piece(int id, int color, int row, int col) {
        this.id = id;
        this.color = color;
        this.row = row;
        this.col = col;
        this.isTaken = 0;
        this.isKing = 0;
    }

    public Piece(int id, int color, int row, int col, int isTaken, int isKing) {
        this.id = id;
        this.color = color;
        this.row = row;
        this.col = col;
        this.isTaken = isTaken;
        this. isKing = isKing;
    }

    // getters
    public int getId() { return this.id; }

    public int getColor() {
        return this.color;
    }

    public int getRow() { return this.row; }

    public int getCol() { return this.col; }

    public int getIsTaken() {
        return this.isTaken;
    }

    public int getIsKing() {
        return this.isKing;
    }

    // setters
    public void setId(int id) {
        this.id = id;
    }

    public void setColor(int color) {
        if (color == 1 | color == 0) {
            this.color = color;
        }
    }

    public void setCoords(int row, int col) {
        if (row >= 0 && row <= 7) {
            this.row = row;
        }
        if (col >= 0 && row <= 7) {
            this.col = col;
        }
    }

    public void setIsTaken(int taken) {
        if (taken == 1) {
            this.isTaken = 1;
        }
    }

    public void setIsKing(int king) {
        if (king == 1) {
            this.isKing = 1;
        }
    }

    @Override
    public String toString() {
        return String.format(
                "CheckersPiece[color=%d, row=%d, column=%d, isTaken=%d, isKing=%d]",
                color, row, col, isTaken, isKing
        );
    }

}

package com.vague.checkersgame;

/**
 * Piece.java
 * Represents a single checkers piece.
 * color   : 0 = black, 1 = red
 * row     : 1-8, board row
 * column  : 'a'-'h', board column
 * isTaken : 0 = active, 1 = captured
 * isKing  : 0 = normal, 1 = king
 */

// considering combining row & column into "coords", and make column a 1-8 int instead of char idkidk

public class Piece {

    int color;
    int row;
    int col;
    int isTaken;
    int isKing;

    public Piece(int color, int row, int col) {
        this.color = color;
        this.row = row;
        this.col = col;
        this.isTaken = 0;
        this.isKing = 0;
    }

    public Piece(int color, int row, int col, int isTaken, int isKing) {
        this.color = color;
        this.row = row;
        this.col = col;
        this.isTaken = isTaken;
        this. isKing = isKing;
    }

    // getters
    public int getColor() {
        return this.color;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public int getIsTaken() {
        return this.isTaken;
    }

    public int getIsKing() {
        return this.isKing;
    }

    // setters
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

    public String toString() {
        return String.format(
                "CheckersPiece[color=%d, row=%d, column=%c, isTaken=%d, isKing=%d]",
                color, row, col, isTaken, isKing
        );
    }

}

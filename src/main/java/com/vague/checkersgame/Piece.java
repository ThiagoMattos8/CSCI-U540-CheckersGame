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
    char column;
    int isTaken;
    int isKing;

    public Piece(int color, int row, char column, int isTaken, int isKing) {
        this.color = color;
        this.row = row;
        this.column = column;
        this.isTaken = isTaken;
        this. isKing = isKing;
    }

    // getters
    public int getColor() {
        return this.color;
    }

    public String getCoords() {
        return "" + this.row + this.column;
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

    public void setCoords(int row, String column) {
        if (row > 0 && row < 9) {
            this.row = row;
        }
        if (column.matches("[a-h]")) {
            this.column = column.charAt(0);
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
                color, row, column, isTaken, isKing
        );
    }

}

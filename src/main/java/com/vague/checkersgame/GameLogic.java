package com.vague.checkersgame;

public class GameLogic {
    //Import Board and Piece
    private Board board;
    private Piece piece;

    // Game Logic Functions

    // Create pieces
    public Piece[][] createPieces(Board board)
    {

        Piece[][] pieces = new Piece[board.size][board.size];

        // creates pieces with starting location coords
        int i=0;
        int j=0;

        for (int x = 0; x < 3; x++) { //add black pieces at top
            for (int y = 0; y < board.size; y++)
                if (y % 2 == 0 && x % 2 == 1) {
                    piece = new Piece(i, 0, x, y, 0, 0);
                    pieces[x][y] = piece;
                    i++;
                    board.addPiece(x, y, piece);
                }
                else if (y % 2 == 1 && x % 2 == 0) {
                    piece = new Piece(i, 0, x, y, 0, 0);
                    pieces[x][y] = piece;
                    i++;
                    board.addPiece(x, y, piece);
                }
        }
        for (int x = board.size - 3; x < board.size; x++) { //add red pieces at bottom
            for (int y = 0; y < board.size; y++)
                if (y % 2 == 0 && x % 2 == 1) {
                    piece = new Piece(j, 1, x, y, 0, 0);
                    pieces[x][y] = piece;
                    j++;
                    board.addPiece(x, y, piece);
                }
                else if (y % 2 == 1 && x % 2 == 0) {
                    piece = new Piece(j, 1, x, y, 0, 0);
                    pieces[x][y] = piece;
                    j++;
                    board.addPiece(x, y, piece);
                }
        }
        return pieces;
    }


    //Piece Move function

    //Jump Function

    //Switch Turn function

    //
}

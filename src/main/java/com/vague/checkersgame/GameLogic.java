package com.vague.checkersgame;

public class GameLogic {
    //Import Board and Piece
    private Board board;
    private Piece piece;

    // Game Logic Functions

    //Program Start places pieces on empty Board
    public void setUpInitialBoard(Board board)
    {
        for (int x = 0; x < 3; x++) { //add black pieces at top
            for (int y = 0; y < board.size; y++)
                if (y % 2 == 0 && x % 2 == 1)
                    board.addPiece(x, y, new Piece(y,0, x, y, 0, 0));
                else if (y % 2 == 1 && x % 2 == 0)
                    board.addPiece(x, y, new Piece(y, 0, x, y, 0, 0));
        }
        for (int x = board.size - 3; x < board.size; x++) { //add red pieces at bottom
            for (int y = 0; y < board.size; y++)
                if (y % 2 == 0 && x % 2 == 1)
                    board.addPiece(x, y, new Piece(y,1, x, y, 0, 0));
                else if (y % 2 == 1 && x % 2 == 0)
                    board.addPiece(x, y, new Piece(y, 1, x, y, 0, 0));
        }
    }


    //Piece Move function

    //Jump Function

    //Switch Turn function

    //
}

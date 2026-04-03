package com.vague.checkersgame;

public class GameLogic {
    // This stores the current board state.
    // If a square has a piece on it, that piece is saved in this array.
    private Piece[][] pieces;

    // This stores the piece the player clicked first.
    // The next click is where that piece should move.
    private Piece selectedPiece;

    // Create the starting pieces and place them on the board.
    public Piece[][] createPieces(Board board) {
        pieces = new Piece[board.size][board.size];

        int blackId = 0;
        int redId = 0;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < board.size; col++) {
                if ((row + col) % 2 == 1) {
                    Piece piece = new Piece(blackId, 0, row, col, 0, 0);
                    pieces[row][col] = piece;
                    blackId++;
                    board.addPiece(row, col, piece);
                }
            }
        }

        for (int row = board.size - 3; row < board.size; row++) {
            for (int col = 0; col < board.size; col++) {
                if ((row + col) % 2 == 1) {
                    Piece piece = new Piece(redId, 1, row, col, 0, 0);
                    pieces[row][col] = piece;
                    redId++;
                    board.addPiece(row, col, piece);
                }
            }
        }

        return pieces;
    }

    // Simple click flow:
    // 1. Click a piece to select it.
    // 2. Click an empty square to move it there.
    public void handleTileClick(int row, int col, Board board) {
        if (pieces == null) {
            return;
        }

        Piece clickedPiece = pieces[row][col];

        if (selectedPiece == null) {
            if (clickedPiece != null) {
                selectedPiece = clickedPiece;
            }
            return;
        }

        // Do not move onto an occupied square.
        // If the user clicks another piece, just switch the selection.
        if (clickedPiece != null) {
            selectedPiece = clickedPiece;
            return;
        }

        moveSelectedPiece(row, col);
        board.redrawPieces(pieces);
    }

    // This moves the same Piece object.
    // This way the position updates instead of creating a whole new piece
    private void moveSelectedPiece(int newRow, int newCol) {
        if (selectedPiece == null) {
            return;
        }

        int oldRow = selectedPiece.getRow();
        int oldCol = selectedPiece.getCol();

        pieces[oldRow][oldCol] = null;
        selectedPiece.setCoords(newRow, newCol);
        pieces[newRow][newCol] = selectedPiece;
        selectedPiece = null;
    }
}

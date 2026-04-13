package com.vague.checkersgame;

public class GameLogic {
    // This stores the current board state.
    // If a square has a piece on it, that piece is saved in this array.
    private Piece[][] pieces;

    // This stores the piece the player clicked first.
    // The next click is where that piece should move.
    private Piece selectedPiece;

    // True when the last move was a jump, so we can check for a chain capture.
    private boolean lastMoveWasJump = false;

    //Current Player
    //Alternate Turns
    //Starting Player Red
    private int currentPlayer = 1;
    private void switchTurn() {
        currentPlayer = (currentPlayer == 0) ? 1 : 0;
    }

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

        // During a chain jump the piece is already locked in — skip the turn guard.
        if (!lastMoveWasJump) {
            if (currentPlayer == 1 && selectedPiece.getColor() == 0) {
                selectedPiece = clickedPiece;
                return;
            }
            else if (currentPlayer == 0 && selectedPiece.getColor() == 1) {
                selectedPiece = clickedPiece;
                return;
            }
        }

        // Do not move onto an occupied square.
        // If the user clicks another piece, just switch the selection.
        if (clickedPiece != null) {
            selectedPiece = clickedPiece;
            return;
        }

        // Do not move onto white square
        else if ((row % 2 == 0) && (col % 2 == 0)) {
            selectedPiece = clickedPiece;
            return;
        }
        else if ((row % 2 == 1) && (col % 2 == 1)) {
            selectedPiece = clickedPiece;
            return;
        }


       /* if (!isValidJumpMove(selectedPiece,row,col)){
            return;
        }*/
        //Only allow a one-square diagonal move in the correct direction.
        else if (!isValidMove(selectedPiece, row, col,board)) {
            return;
        }


        // Remember whether this move was a jump before moveSelectedPiece clears selectedPiece.
        boolean wasJump = lastMoveWasJump;
        Piece movedPiece = selectedPiece;

        moveSelectedPiece(row, col);
        board.redrawPieces(pieces);

        // If the move was a jump, check whether the same piece can jump again.
        // If it can, keep it selected and let the current player go again.
        if (wasJump && canJumpAgain(movedPiece, board)) {
            selectedPiece = movedPiece;
            return;
        }

        //Player Turn Label
        //Switch After Moving Piece
        lastMoveWasJump = false;
        switchTurn();
        updateTurnLabel(board);
    }

    // A valid move is exactly 1 row diagonally and 1 column diagonally.
    // Black moves downward. Red moves upward.
  /*  private boolean isValidJumpMove(Piece piece, int newRow, int newCol) {
        if (piece == null) {
            return false;
        }

        int oldRow = piece.getRow();        method currently moves pieces by 2 in appropriate direciton
        int oldCol = piece.getCol();        conflicts with isValidSimpleMove and does not allow piece movement

        int rowDifference = newRow - oldRow;
        int colDifference = newCol - oldCol;

        if (Math.abs(colDifference) != 2) {
            return false;
        }
        if (piece.getColor() == 0) {
            return rowDifference == 2;
        }

        if (piece.getColor() == 1) {
            return rowDifference == -2;
        }
        return false;
    } */
    private boolean isValidMove(Piece piece, int newRow, int newCol,Board board) {
        if (piece == null) {
            return false;
        }

        int oldRow = piece.getRow();
        int oldCol = piece.getCol();

        int rowDifference = newRow - oldRow;
        int colDifference = newCol - oldCol;

        if (Math.abs(colDifference) != 1) {
            if (Math.abs(colDifference)==2){
                if (piece.getColor()==0){
                    // jumping right: land at oldCol+2, capture at oldCol+1
                    if (newCol == oldCol+2 && oldCol+1 < board.size && pieces[oldRow+1][oldCol+1]!=null && pieces[oldRow+1][oldCol+1].getColor()!=0){
                        removePiece(oldRow+1,oldCol+1,board);
                        lastMoveWasJump = true;
                        return rowDifference==2;
                    }
                    // jumping left: land at oldCol-2, capture at oldCol-1
                    if (newCol == oldCol-2 && oldCol-1 >= 0 && pieces[oldRow+1][oldCol-1]!=null && pieces[oldRow+1][oldCol-1].getColor()!=0) {
                        removePiece(oldRow+1,oldCol-1,board);
                        lastMoveWasJump = true;
                        return rowDifference==2;
                    }
                } else if (piece.getColor()==1) {
                    // jumping right: land at oldCol+2, capture at oldCol+1
                    if (newCol == oldCol+2 && oldCol+1 < board.size && pieces[oldRow-1][oldCol+1]!=null && pieces[oldRow-1][oldCol+1].getColor()!=1) {
                        removePiece(oldRow-1,oldCol+1,board);
                        lastMoveWasJump = true;
                        return rowDifference==-2;
                    }
                    // jumping left: land at oldCol-2, capture at oldCol-1
                    if (newCol == oldCol-2 && oldCol-1 >= 0 && pieces[oldRow-1][oldCol-1]!=null && pieces[oldRow-1][oldCol-1].getColor()!=1) {
                        removePiece(oldRow-1,oldCol-1,board);
                        lastMoveWasJump = true;
                        return rowDifference==-2;
                    }
                }
            }else {
                return false;}

        }
        lastMoveWasJump = false;

        if (piece.getColor() == 0) {
            return rowDifference == 1;
        }

        if (piece.getColor() == 1) {
            return rowDifference == -1;
        }

        return false;
    }

    //Update Turn Label
    private void updateTurnLabel(Board board) {
        String playerText = (currentPlayer == 0) ? "Black" : "Red";
        board.updateTurnDisplay("Current Player: " + playerText);
    }

    // Check whether a piece that just jumped can jump again from its current position.
    private boolean canJumpAgain(Piece piece, Board board) {
        if (piece == null) return false;
        int r = piece.getRow();
        int c = piece.getCol();
        int color = piece.getColor();
        int fwd = (color == 0) ? 1 : -1; // black moves down, red moves up

        // Check both diagonal jump targets in the forward direction.
        int[][] jumps = {{r + fwd, c + 1, r + 2 * fwd, c + 2},
                {r + fwd, c - 1, r + 2 * fwd, c - 2}};

        for (int[] j : jumps) {
            int midRow = j[0], midCol = j[1], landRow = j[2], landCol = j[3];
            if (midRow < 0 || midRow >= board.size) continue;
            if (midCol < 0 || midCol >= board.size) continue;
            if (landRow < 0 || landRow >= board.size) continue;
            if (landCol < 0 || landCol >= board.size) continue;

            Piece middle = pieces[midRow][midCol];
            Piece landing = pieces[landRow][landCol];

            if (middle != null && middle.getColor() != color && landing == null) {
                return true;
            }
        }
        return false;
    }

    //Reset Button Logic
    public Piece[][] resetGame(Board board) {
        selectedPiece = null;
        currentPlayer = 1;
        lastMoveWasJump = false;
        return createPieces(board);
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
    private void removePiece(int row,int col, Board board){
        board.removePiece(pieces[row][col]);
        pieces[row][col]=null;

    }
}
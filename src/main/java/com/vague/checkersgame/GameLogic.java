package com.vague.checkersgame;

import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    // This stores the current board state.
    private Piece[][] pieces;

    // This stores the piece the player clicked first.
    private Piece selectedPiece;

    // True only while the same piece must keep jumping.
    private boolean lastMoveWasJump = false;

    // Starting player is Red.
    private int currentPlayer = 1;

    // Counts only successful moves.
    private int moveCount = 0;
    private String gameOverMessage;

    private void switchTurn() {
        currentPlayer = (currentPlayer == 0) ? 1 : 0;
    }

    public int getMoveCount() {
        return moveCount;
    }

    /** Gives the AI read access to the live board state. */
    public Piece[][] getPieces() {
        return pieces;
    }

    public boolean isGameOver() {
        return gameOverMessage != null;
    }

    public String getGameOverMessage() {
        return gameOverMessage;
    }

    public String getCurrentPlayerName() {
        return currentPlayer == 0 ? "Black" : "Red";
    }

    public List<int[]> getSelectablePiecePositions(Board board) {
        List<int[]> positions = new ArrayList<>();

        if (pieces == null) {
            return positions;
        }

        // During a chain jump, only the same piece may continue.
        if (lastMoveWasJump && selectedPiece != null) {
            positions.add(new int[]{selectedPiece.getRow(), selectedPiece.getCol()});
            return positions;
        }

        boolean forcedJumpExists = playerHasJump(currentPlayer, board);

        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece == null || piece.getColor() != currentPlayer) {
                    continue;
                }

                if (forcedJumpExists) {
                    if (pieceHasJump(piece, board)) {
                        positions.add(new int[]{piece.getRow(), piece.getCol()});
                    }
                } else if (pieceHasAnyLegalMove(piece, board)) {
                    positions.add(new int[]{piece.getRow(), piece.getCol()});
                }
            }
        }

        return positions;
    }

    public List<int[]> getSelectedPieceDestinations(Board board) {
        List<int[]> positions = new ArrayList<>();

        if (pieces == null || selectedPiece == null) {
            return positions;
        }

        boolean forcedJumpExists = playerHasJump(currentPlayer, board) || lastMoveWasJump;

        if (forcedJumpExists) {
            addJumpDestinations(selectedPiece, board, positions);
        } else {
            addSimpleMoveDestinations(selectedPiece, board, positions);
        }

        return positions;
    }

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

    public void handleTileClick(int row, int col, Board board) {
        if (pieces == null) {
            return;
        }

        Piece clickedPiece = pieces[row][col];

        // First click: only allow selecting one of the current player's pieces.
        // If a jump is available, only allow selecting a piece that can jump.
        if (selectedPiece == null) {
            trySelectPiece(clickedPiece, board);
            return;
        }

        // During a chain jump, the same piece must continue.
        // Ignore clicks on other pieces until the chain is finished.
        if (lastMoveWasJump && clickedPiece != null && clickedPiece != selectedPiece) {
            return;
        }

        // Clicking another piece changes selection only when that piece belongs
        // to the current player and is allowed under the forced-jump rule.
        if (clickedPiece != null) {
            trySelectPiece(clickedPiece, board);
            return;
        }

        if (!isDarkSquare(row, col)) {
            return;
        }

        boolean forcedJumpExists = playerHasJump(currentPlayer, board);

        if (forcedJumpExists) {
            if (!isValidJumpMove(selectedPiece, row, col, board)) {
                return;
            }

            Piece movedPiece = selectedPiece;
            performJumpMove(movedPiece, row, col);
            handlePromotion(movedPiece, row, board);
            board.redrawPieces(pieces);

            if (canJumpAgain(movedPiece, board)) {
                // Keep the same player's turn and force this piece to continue jumping.
                selectedPiece = movedPiece;
                lastMoveWasJump = true;
                return;
            }

            finishTurnAfterMove(board);
            return;
        }

        if (!isValidSimpleMove(selectedPiece, row, col)) {
            return;
        }

        Piece movedPiece = selectedPiece;
        moveSelectedPiece(row, col);
        handlePromotion(movedPiece, row, board);
        board.redrawPieces(pieces);
        finishTurnAfterMove(board);
    }

    private void trySelectPiece(Piece clickedPiece, Board board) {
        if (clickedPiece == null) {
            return;
        }

        if (clickedPiece.getColor() != currentPlayer) {
            return;
        }

        // If a jump is forced, only pieces that can jump are selectable.
        if (playerHasJump(currentPlayer, board) && !pieceHasJump(clickedPiece, board)) {
            return;
        }

        // During a chain jump, the same piece must stay selected.
        if (lastMoveWasJump && selectedPiece != null && clickedPiece != selectedPiece) {
            return;
        }

        selectedPiece = clickedPiece;
    }

    private boolean isDarkSquare(int row, int col) {
        return (row + col) % 2 == 1;
    }

    private void addSimpleMoveDestinations(Piece piece, Board board, List<int[]> positions) {
        int[] rowDirections = piece.getIsKing() == 1
                ? new int[]{-1, 1}
                : new int[]{piece.getColor() == 0 ? 1 : -1};

        for (int rowDirection : rowDirections) {
            for (int colDirection : new int[]{-1, 1}) {
                int newRow = piece.getRow() + rowDirection;
                int newCol = piece.getCol() + colDirection;

                if (newRow < 0 || newRow >= board.size || newCol < 0 || newCol >= board.size) {
                    continue;
                }

                if (pieces[newRow][newCol] == null && isDarkSquare(newRow, newCol)
                        && isValidSimpleMove(piece, newRow, newCol)) {
                    positions.add(new int[]{newRow, newCol});
                }
            }
        }
    }

    // One-square move for a non-capturing move.
    private boolean isValidSimpleMove(Piece piece, int newRow, int newCol) {
        if (piece == null) {
            return false;
        }

        int oldRow = piece.getRow();
        int oldCol = piece.getCol();

        int rowDifference = newRow - oldRow;
        int colDifference = newCol - oldCol;

        if (Math.abs(colDifference) != 1) {
            return false;
        }

        if (piece.getColor() == 0) {
            return rowDifference == 1 || (piece.getIsKing() == 1 && rowDifference == -1);
        }

        if (piece.getColor() == 1) {
            return rowDifference == -1 || (piece.getIsKing() == 1 && rowDifference == 1);
        }

        return false;
    }

    // Two-square move for a capture move.
    private boolean isValidJumpMove(Piece piece, int newRow, int newCol, Board board) {
        if (piece == null) {
            return false;
        }

        int oldRow = piece.getRow();
        int oldCol = piece.getCol();

        int rowDifference = newRow - oldRow;
        int colDifference = newCol - oldCol;

        if (Math.abs(rowDifference) != 2 || Math.abs(colDifference) != 2) {
            return false;
        }

        int midRow = oldRow + (rowDifference / 2);
        int midCol = oldCol + (colDifference / 2);

        if (midRow < 0 || midRow >= board.size || midCol < 0 || midCol >= board.size) {
            return false;
        }

        Piece middle = pieces[midRow][midCol];
        if (middle == null || middle.getColor() == piece.getColor()) {
            return false;
        }

        // Normal pieces may only jump forward. Kings may jump both ways.
        if (piece.getIsKing() == 0) {
            if (piece.getColor() == 0 && rowDifference != 2) {
                return false;
            }
            if (piece.getColor() == 1 && rowDifference != -2) {
                return false;
            }
        }

        return true;
    }

    private boolean pieceHasJump(Piece piece, Board board) {
        if (piece == null) {
            return false;
        }

        int[] rowDirections = piece.getIsKing() == 1
                ? new int[]{-2, 2}
                : new int[]{piece.getColor() == 0 ? 2 : -2};

        for (int rowDirection : rowDirections) {
            for (int colDirection : new int[]{-2, 2}) {
                int newRow = piece.getRow() + rowDirection;
                int newCol = piece.getCol() + colDirection;

                if (newRow < 0 || newRow >= board.size || newCol < 0 || newCol >= board.size) {
                    continue;
                }

                if (pieces[newRow][newCol] == null && isValidJumpMove(piece, newRow, newCol, board)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void addJumpDestinations(Piece piece, Board board, List<int[]> positions) {
        int[] rowDirections = piece.getIsKing() == 1
                ? new int[]{-2, 2}
                : new int[]{piece.getColor() == 0 ? 2 : -2};

        for (int rowDirection : rowDirections) {
            for (int colDirection : new int[]{-2, 2}) {
                int newRow = piece.getRow() + rowDirection;
                int newCol = piece.getCol() + colDirection;

                if (newRow < 0 || newRow >= board.size || newCol < 0 || newCol >= board.size) {
                    continue;
                }

                if (pieces[newRow][newCol] == null && isValidJumpMove(piece, newRow, newCol, board)) {
                    positions.add(new int[]{newRow, newCol});
                }
            }
        }
    }

    private boolean playerHasJump(int color, Board board) {
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece != null && piece.getColor() == color && pieceHasJump(piece, board)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean pieceHasAnyLegalMove(Piece piece, Board board) {
        if (piece == null) {
            return false;
        }

        int[] rowDirections = piece.getIsKing() == 1
                ? new int[]{-1, 1}
                : new int[]{piece.getColor() == 0 ? 1 : -1};

        for (int rowDirection : rowDirections) {
            for (int colDirection : new int[]{-1, 1}) {
                int newRow = piece.getRow() + rowDirection;
                int newCol = piece.getCol() + colDirection;

                if (newRow < 0 || newRow >= board.size || newCol < 0 || newCol >= board.size) {
                    continue;
                }

                if (pieces[newRow][newCol] == null && isDarkSquare(newRow, newCol)
                        && isValidSimpleMove(piece, newRow, newCol)) {
                    return true;
                }
            }
        }

        return pieceHasJump(piece, board);
    }

    private boolean playerHasAnyLegalMove(int color, Board board) {
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece != null && piece.getColor() == color && pieceHasAnyLegalMove(piece, board)) {
                    return true;
                }
            }
        }
        return false;
    }

    private int countPieces(int color) {
        int count = 0;
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece != null && piece.getColor() == color) {
                    count++;
                }
            }
        }
        return count;
    }

    private void finishTurnAfterMove(Board board) {
        lastMoveWasJump = false;
        selectedPiece = null;

        int opponent = currentPlayer == 0 ? 1 : 0;
        String winnerText = currentPlayer == 0 ? "Black wins!" : "Red wins!";

        // Win if the opponent has no pieces left.
        if (countPieces(opponent) == 0) {
            gameOverMessage = winnerText;
            board.updateTurnDisplay(winnerText);
            pieces = null;
            return;
        }

        // Win if the opponent has no legal moves left.
        if (!playerHasAnyLegalMove(opponent, board)) {
            gameOverMessage = winnerText;
            board.updateTurnDisplay(winnerText);
            pieces = null;
            return;
        }

        switchTurn();
        updateTurnLabel(board);
    }

    private void handlePromotion(Piece movedPiece, int newRow, Board board) {
        if (movedPiece.getIsKing() == 0) {
            if (movedPiece.getColor() == 1 && newRow == 0) {
                movedPiece.setIsKing(1);
            } else if (movedPiece.getColor() == 0 && newRow == board.size - 1) {
                movedPiece.setIsKing(1);
            }
        }
    }

    private void updateTurnLabel(Board board) {
        String playerText = (currentPlayer == 0) ? "Black" : "Red";
        board.updateTurnDisplay("Current Player: " + playerText);
    }

    public Piece[][] resetGame(Board board) {
        selectedPiece = null;
        currentPlayer = 1;
        lastMoveWasJump = false;
        moveCount = 0;
        gameOverMessage = null;
        return createPieces(board);
    }

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
        moveCount++;
    }

    private void performJumpMove(Piece piece, int newRow, int newCol) {
        int oldRow = piece.getRow();
        int oldCol = piece.getCol();
        int midRow = (oldRow + newRow) / 2;
        int midCol = (oldCol + newCol) / 2;

        pieces[midRow][midCol] = null;
        selectedPiece = piece;
        moveSelectedPiece(newRow, newCol);
        lastMoveWasJump = true;
    }

    private boolean canJumpAgain(Piece piece, Board board) {
        return pieceHasJump(piece, board);
    }
}
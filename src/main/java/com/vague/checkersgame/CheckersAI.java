package com.vague.checkersgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * CheckersAI.java
 *
 * Minimax with alpha-beta pruning. The AI plays as Black (color 0).
 * All logic operates on plain Piece[][] snapshots — no Board/JavaFX involved,
 * so it never touches the real game state during search.
 *
 * Difficulty levels control search depth:
 *   EASY   → depth 2
 *   MEDIUM → depth 4
 *   HARD   → depth 6
 */
public class CheckersAI {

    // ── Public API ──────────────────────────────────────────────────────────

    /**
     * A single move: one piece moves from (fromRow,fromCol) to (toRow,toCol).
     * For chain jumps the AI returns moves one hop at a time and GameMain
     * keeps calling getBestMove until no more jumps remain.
     */
    public static class Move {
        public final int fromRow, fromCol, toRow, toCol;
        Move(int fromRow, int fromCol, int toRow, int toCol) {
            this.fromRow = fromRow; this.fromCol = fromCol;
            this.toRow   = toRow;   this.toCol   = toCol;
        }
    }

    private static final int DEPTH       = 4;
    private static final int AI_COLOR    = 0; // black
    private static final int HUMAN_COLOR = 1; // red

    public CheckersAI() {}

    /**
     * Given the current board, return the best move for the AI (Black).
     * Returns null if the AI has no legal moves (game already over).
     *
     * For chain jumps: after applying the returned move, call this method
     * again with the updated board if the moved piece can still jump —
     * GameMain handles this loop.
     */
    public Move getBestMove(Piece[][] board) {
        Piece[][] copy = deepCopy(board);
        List<Move> allMoves = getAllMoves(copy, AI_COLOR);
        if (allMoves.isEmpty()) return null;

        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;

        // Shuffle so equal-scored moves vary rather than always picking the
        // same piece, which makes the AI feel more natural.
        Collections.shuffle(allMoves);

        for (Move move : allMoves) {
            Piece[][] next = applyMove(copy, move);
            int score = minimax(next, DEPTH - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            if (score > bestScore) {
                bestScore = score;
                bestMove  = move;
            }
        }

        return bestMove;
    }

    // ── Minimax ─────────────────────────────────────────────────────────────

    private int minimax(Piece[][] board, int depth, int alpha, int beta, boolean maximizing) {
        List<Move> moves = getAllMoves(board, maximizing ? AI_COLOR : HUMAN_COLOR);

        if (depth == 0 || moves.isEmpty()) {
            return evaluate(board);
        }

        if (maximizing) {
            int value = Integer.MIN_VALUE;
            for (Move move : moves) {
                Piece[][] next = applyMove(board, move);
                value = Math.max(value, minimax(next, depth - 1, alpha, beta, false));
                alpha = Math.max(alpha, value);
                if (alpha >= beta) break; // β cut-off
            }
            return value;
        } else {
            int value = Integer.MAX_VALUE;
            for (Move move : moves) {
                Piece[][] next = applyMove(board, move);
                value = Math.min(value, minimax(next, depth - 1, alpha, beta, true));
                beta = Math.min(beta, value);
                if (beta <= alpha) break; // α cut-off
            }
            return value;
        }
    }

    // ── Evaluation function ─────────────────────────────────────────────────

    /**
     * Positive scores favour the AI (Black). Negative scores favour the human (Red).
     *
     * Factors (all from Black's perspective):
     *   - Piece count difference (captures are the most important thing)
     *   - King bonus: kings are worth more than regular pieces
     *   - Advancement: pieces closer to promotion are more valuable
     *   - Center control: center columns are slightly preferred
     *   - Back row defense: keeping a piece on the back row prevents the
     *     opponent from kinging easily
     */
    private int evaluate(Piece[][] board) {
        int score = 0;
        int size = board.length;

        int blackPieces = 0, redPieces = 0;

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Piece p = board[row][col];
                if (p == null) continue;

                int pieceScore = 0;

                // Base value
                pieceScore += (p.getIsKing() == 1) ? 300 : 100;

                // Advancement bonus: black advances downward (row increases)
                if (p.getIsKing() == 0) {
                    if (p.getColor() == AI_COLOR) {
                        pieceScore += row * 5; // closer to red's back row
                    } else {
                        pieceScore += (size - 1 - row) * 5; // closer to black's back row
                    }
                }

                // Center control: columns 2-5 are better than edges
                int colBonus = Math.min(col, size - 1 - col); // 0 at edge, 3 at center
                pieceScore += colBonus * 2;

                // Back row defense: a piece on the home back row blocks kinging
                if (p.getIsKing() == 0) {
                    if (p.getColor() == AI_COLOR    && row == 0)        pieceScore += 15;
                    if (p.getColor() == HUMAN_COLOR && row == size - 1) pieceScore += 15;
                }

                if (p.getColor() == AI_COLOR) {
                    score += pieceScore;
                    blackPieces++;
                } else {
                    score -= pieceScore;
                    redPieces++;
                }
            }
        }

        // Large bonus/penalty for wiping out the opponent
        if (redPieces   == 0) return  100_000;
        if (blackPieces == 0) return -100_000;

        return score;
    }

    // ── Move generation ─────────────────────────────────────────────────────

    /**
     * Returns all legal moves for the given color.
     * Jumps are mandatory: if any jump exists, only jumps are returned.
     * Chain jumps are expanded fully so the tree sees the complete capture.
     */
    private List<Move> getAllMoves(Piece[][] board, int color) {
        List<Move> jumps  = new ArrayList<>();
        List<Move> simple = new ArrayList<>();
        int size = board.length;

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Piece p = board[row][col];
                if (p == null || p.getColor() != color) continue;
                jumps.addAll(getJumpsForPiece(board, p));
                simple.addAll(getSimpleMovesForPiece(board, p));
            }
        }

        return jumps.isEmpty() ? simple : jumps;
    }

    private List<Move> getSimpleMovesForPiece(Piece[][] board, Piece p) {
        List<Move> moves = new ArrayList<>();
        int size = board.length;
        int[] rowDirs = (p.getIsKing() == 1)
                ? new int[]{-1, 1}
                : new int[]{p.getColor() == AI_COLOR ? 1 : -1};

        for (int rd : rowDirs) {
            for (int cd : new int[]{-1, 1}) {
                int nr = p.getRow() + rd;
                int nc = p.getCol() + cd;
                if (inBounds(nr, nc, size) && board[nr][nc] == null) {
                    moves.add(new Move(p.getRow(), p.getCol(), nr, nc));
                }
            }
        }
        return moves;
    }

    private List<Move> getJumpsForPiece(Piece[][] board, Piece p) {
        List<Move> moves = new ArrayList<>();
        int size = board.length;
        int[] rowDirs = (p.getIsKing() == 1)
                ? new int[]{-2, 2}
                : new int[]{p.getColor() == AI_COLOR ? 2 : -2};

        for (int rd : rowDirs) {
            for (int cd : new int[]{-2, 2}) {
                int nr  = p.getRow() + rd;
                int nc  = p.getCol() + cd;
                int mr  = p.getRow() + rd / 2;
                int mc  = p.getCol() + cd / 2;
                if (!inBounds(nr, nc, size)) continue;
                if (board[nr][nc] != null)   continue;
                Piece mid = board[mr][mc];
                if (mid != null && mid.getColor() != p.getColor()) {
                    moves.add(new Move(p.getRow(), p.getCol(), nr, nc));
                }
            }
        }
        return moves;
    }

    // ── Board manipulation ──────────────────────────────────────────────────

    /**
     * Apply a move to a board copy and return the new board.
     * Handles captures and promotion automatically.
     * For chain jumps the tree calls applyMove repeatedly.
     */
    private Piece[][] applyMove(Piece[][] board, Move move) {
        Piece[][] next = deepCopy(board);
        int size = next.length;

        Piece p = next[move.fromRow][move.fromCol];
        next[move.fromRow][move.fromCol] = null;

        // Create a new Piece at the destination so we don't mutate the copy's
        // original object (deepCopy already gave us independent objects, but
        // be explicit about coords).
        int isKing = p.getIsKing();
        // Promote if reaching the back row
        if (p.getColor() == AI_COLOR    && move.toRow == size - 1) isKing = 1;
        if (p.getColor() == HUMAN_COLOR && move.toRow == 0)        isKing = 1;

        next[move.toRow][move.toCol] = new Piece(p.getId(), p.getColor(),
                move.toRow, move.toCol, 0, isKing);

        // Remove captured piece for jumps
        if (Math.abs(move.toRow - move.fromRow) == 2) {
            int midRow = (move.fromRow + move.toRow) / 2;
            int midCol = (move.fromCol + move.toCol) / 2;
            next[midRow][midCol] = null;
        }

        return next;
    }

    private Piece[][] deepCopy(Piece[][] board) {
        int size = board.length;
        Piece[][] copy = new Piece[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Piece p = board[r][c];
                if (p != null) {
                    copy[r][c] = new Piece(p.getId(), p.getColor(),
                            p.getRow(), p.getCol(), p.getIsTaken(), p.getIsKing());
                }
            }
        }
        return copy;
    }

    private boolean inBounds(int row, int col, int size) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }
}
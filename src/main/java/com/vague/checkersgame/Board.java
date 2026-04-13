package com.vague.checkersgame;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.util.List;

public class Board {
    int size;
    Pane gameBoard = new Pane();
    private static final double TILE = 70;
    private static final Color LIGHT = Color.web("#F0D9B5");
    private static final Color DARK = Color.web("#B58863");
    int[][] coords;
    Rectangle[][] tiles;
    private static final Color SELECTABLE_HIGHLIGHT = Color.rgb(255, 215, 0, 0.85);
    private static final Color DESTINATION_HIGHLIGHT = Color.rgb(46, 204, 113, 0.9);

    //Player Turn label
    //Updater and Setter
    private Label turnLabel;
    public void setTurnLabel(Label label) {
        this.turnLabel = label;
    }
    public void updateTurnDisplay(String text) {
        if (turnLabel != null) {
            turnLabel.setText(text);
        }
    }

    public Board(int size) {
        this.size = size;
        this.coords = new int[size][size];
        this.tiles = new Rectangle[size][size];
    }

    public PieceView addPiece(int row, int col, Piece model) {
        PieceView view = new PieceView(model, TILE);
        view.setTranslateX(col * TILE + TILE / 2.0);
        view.setTranslateY(row * TILE + TILE / 2.0);
        gameBoard.getChildren().add(view);
        return view;
    }

    public void displayBoard() {
        gameBoard.setPrefSize(size * TILE, size * TILE);

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                Rectangle tile = new Rectangle(c * TILE, r * TILE, TILE, TILE);
                tile.setFill((r + c) % 2 == 0 ? LIGHT : DARK);
                // Keep a constant inside stroke so highlighting does not change layout bounds.
                tile.setStroke(Color.TRANSPARENT);
                tile.setStrokeWidth(4);
                tile.setStrokeType(StrokeType.INSIDE);
                tiles[r][c] = tile;
                gameBoard.getChildren().add(tile);
            }
        }
    }

    // This redraws the pieces using the current board data.
    // We leave the squares alone and only remove old piece views.
    public void redrawPieces(Piece[][] pieces) {
        gameBoard.getChildren().removeIf(node -> node instanceof PieceView);

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (pieces[row][col] != null && pieces[row][col].getIsTaken() == 0) {
                    addPiece(row, col, pieces[row][col]);
                }
            }
        }
    }

    // This lets the main game attach a click action to each square.
    public void setTileClickHandler(int row, int col, EventHandler<javafx.scene.input.MouseEvent> handler) {
        tiles[row][col].setOnMouseClicked(handler);
    }

    //For Resetting Board
    public void clearBoard() {
        gameBoard.getChildren().clear();
        displayBoard(); // redraw empty grid
    }

    public void clearHighlights() {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                tiles[row][col].setStroke(Color.TRANSPARENT);
            }
        }
    }

    // Highlight squares that contain pieces the current player may act with.
    public void highlightSelectablePieces(List<int[]> positions) {
        for (int[] position : positions) {
            Rectangle tile = tiles[position[0]][position[1]];
            tile.setStroke(SELECTABLE_HIGHLIGHT);
        }
    }

    // Highlight squares a selected piece may move or jump to.
    public void highlightDestinationSquares(List<int[]> positions) {
        for (int[] position : positions) {
            Rectangle tile = tiles[position[0]][position[1]];
            tile.setStroke(DESTINATION_HIGHLIGHT);
        }
    }
}

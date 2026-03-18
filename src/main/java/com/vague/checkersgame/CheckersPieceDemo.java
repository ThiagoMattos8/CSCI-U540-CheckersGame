package com.vague.checkersgame;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * CheckersPieceDemo.java
 * Previews Piece + PieceView on a small 4x2 board.
 */
public class CheckersPieceDemo extends Application {

    private static final double TILE  = 90;
    private static final Color  LIGHT = Color.web("#F0D9B5");
    private static final Color  DARK  = Color.web("#B58863");

    private Label statusLabel;

    @Override
    public void start(Stage stage) {

        Pane board = new Pane();
        board.setPrefSize(4 * TILE, 2 * TILE);

        // Draw tiles
        for (int r = 0; r < 2; r++) {
            for (int c = 0; c < 4; c++) {
                Rectangle tile = new Rectangle(c * TILE, r * TILE, TILE, TILE);
                tile.setFill((r + c) % 2 == 0 ? LIGHT : DARK);
                board.getChildren().add(tile);
            }
        }

        // Black pieces — row 0, columns b and d
        addPiece(board, new Piece(0, 0, 'b', 0, 0), 0, 1);
        addPiece(board, new Piece(0, 0, 'd', 0, 0), 0, 3);

        // Red pieces — row 1, columns a and c
        PieceView redA = addPiece(board, new Piece(1, 1, 'a', 0, 0), 1, 0);
        PieceView redC = addPiece(board, new Piece(1, 1, 'c', 0, 0), 1, 2);

        // Status label
        statusLabel = new Label("Click a piece to inspect it.");
        statusLabel.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");

        // Promote button — promotes whichever red piece was last clicked
        PieceView[] lastSelected = {null};
        for (PieceView v : new PieceView[]{redA, redC}) {
            v.setOnMouseClicked(e -> {
                lastSelected[0] = v;
                statusLabel.setText(v.getModel().toString());
                e.consume();
            });
        }

        Button promoteBtn = new Button("👑  Promote last selected RED to King");
        promoteBtn.setStyle(
                "-fx-background-color: #F4D03F; -fx-font-weight: bold; -fx-padding: 7 14;");
        promoteBtn.setOnAction(e -> {
            if (lastSelected[0] != null) {
                lastSelected[0].getModel().setIsKing(1);
                lastSelected[0].refresh();
                statusLabel.setText(lastSelected[0].getModel().toString());
            } else {
                statusLabel.setText("Click a red piece first.");
            }
        });

        VBox root = new VBox(14,
                new Label("CheckersPiece Demo") {{ setStyle("-fx-font-size: 16px; -fx-font-weight: bold;"); }},
                board,
                promoteBtn,
                statusLabel
        );
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #FAFAFA;");

        stage.setScene(new Scene(root));
        stage.setTitle("CheckersPiece Demo");
        stage.setResizable(false);
        stage.show();
    }

    private PieceView addPiece(Pane board, Piece model, int row, int col) {
        PieceView view = new PieceView(model, TILE);
        view.setTranslateX(col * TILE + TILE / 2.0);
        view.setTranslateY(row * TILE + TILE / 2.0);
        board.getChildren().add(view);
        return view;
    }

    public static void main(String[] args) { launch(args); }
}

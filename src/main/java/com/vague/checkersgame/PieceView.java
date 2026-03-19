package com.vague.checkersgame;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

/**
 * PieceView.java
 * JavaFX visual for a CheckersPiece.
 * Renders as a solid red or black circle with a white 'X' for kings.
 * Usage:
 *   CheckersPiece model = new CheckersPiece(1, 1, 2, 0, 0);
 *   CheckersPieceView view = new CheckersPieceView(model, tileSize);
 *   pane.getChildren().add(view);
 *   view.setTranslateX(col * tileSize + tileSize / 2.0);
 *   view.setTranslateY(row * tileSize + tileSize / 2.0);
 */
public class PieceView extends Group {

    private final Piece model;
    private final double radius;

    private final Line xLine1;
    private final Line xLine2;

    public PieceView(Piece model, double tileSize) {
        this.model  = model;
        this.radius = tileSize * 0.40;

        // Body
        Circle body = new Circle(radius);
        body.setFill(model.getColor() == 1 ? Color.RED : Color.BLACK);
        body.setStroke(Color.WHITE);
        body.setStrokeWidth(1.5);

        // 'X' for king
        double arm = radius * 0.55;

        xLine1 = new Line(-arm, -arm, arm, arm);
        xLine2 = new Line( arm, -arm, -arm, arm);
        xLine1.setStroke(Color.WHITE);
        xLine2.setStroke(Color.WHITE);
        xLine1.setStrokeWidth(3);
        xLine2.setStrokeWidth(3);

        boolean king = model.getIsKing() == 1;
        xLine1.setVisible(king);
        xLine2.setVisible(king);

        getChildren().addAll(body, xLine1, xLine2);
    }

    // Call after model.setIsKing() to sync the X marker.
    public void refresh() {
        boolean king = model.getIsKing() == 1;
        xLine1.setVisible(king);
        xLine2.setVisible(king);
    }

    public Piece getModel() { return model; }
}
package ajedrez.core;

import java.util.ArrayList;
import java.util.List;

public class PawnMovement implements MovementStrategy {

    @Override
    public List<Position> reachableSquares(Board board, Position from, PieceColor color) {
        List<Position> squares = new ArrayList<>();
        Direction forward = new Direction(forwardDelta(color), 0);

        Position oneStep = from.plus(forward);
        if (board.size().contains(oneStep) && board.isEmpty(oneStep)) {
            squares.add(oneStep);
            Position twoSteps = oneStep.plus(forward);
            if (from.row() == startingRow(board, color)
                    && board.size().contains(twoSteps) && board.isEmpty(twoSteps)) {
                squares.add(twoSteps);
            }
        }

        // En diagonal solo se mueve si hay una pieza que capturar.
        for (Position diagonal : attackedSquares(board, from, color)) {
            if (!board.isEmpty(diagonal)) {
                squares.add(diagonal);
            }
        }
        return squares;
    }

    // Ataca las dos diagonales hacia adelante, haya o no pieza en ellas.
    @Override
    public List<Position> attackedSquares(Board board, Position from, PieceColor color) {
        int forward = forwardDelta(color);
        return List.of(new Direction(forward, -1), new Direction(forward, 1)).stream()
                .map(from::plus)
                .filter(board.size()::contains)
                .toList();
    }

    // Las blancas arrancan en las filas bajas y avanzan hacia filas mayores; las negras al revés.
    private int forwardDelta(PieceColor color) {
        return color == PieceColor.WHITE ? 1 : -1;
    }

    private int startingRow(Board board, PieceColor color) {
        return color == PieceColor.WHITE ? 1 : board.size().rows() - 2;
    }
}

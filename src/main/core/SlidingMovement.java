package ajedrez.core;

import java.util.ArrayList;
import java.util.List;

public class SlidingMovement implements MovementStrategy {

    private final List<Direction> directions;
    private final int maxSteps;

    public SlidingMovement(List<Direction> directions, int maxSteps) {
        if (maxSteps < 1) {
            throw new IllegalArgumentException("maxSteps debe ser al menos 1: " + maxSteps);
        }
        this.directions = List.copyOf(directions);
        this.maxSteps = maxSteps;
    }

    // Sin límite de pasos: el borde del tablero corta el recorrido, sea cual sea su tamaño.
    public SlidingMovement(List<Direction> directions) {
        this(directions, Integer.MAX_VALUE);
    }

    @Override
    public List<Position> reachableSquares(Board board, Position from, PieceColor color) {
        List<Position> squares = new ArrayList<>();
        for (Direction direction : directions) {
            Position current = from.plus(direction);
            for (int step = 1; step <= maxSteps && board.size().contains(current); step++) {
                squares.add(current);
                if (!board.isEmpty(current)) {
                    break;
                }
                current = current.plus(direction);
            }
        }
        return squares;
    }
}

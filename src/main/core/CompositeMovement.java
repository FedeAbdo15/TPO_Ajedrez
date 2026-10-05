package ajedrez.core;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class CompositeMovement implements MovementStrategy {

    private final List<MovementStrategy> strategies;

    public CompositeMovement(List<MovementStrategy> strategies) {
        if (strategies.size() < 2) {
            throw new IllegalArgumentException("Una composición necesita al menos dos estrategias");
        }
        this.strategies = List.copyOf(strategies);
    }

    @Override
    public List<Position> reachableSquares(Board board, Position from, PieceColor color) {
        Set<Position> squares = new LinkedHashSet<>();
        strategies.forEach(s -> squares.addAll(s.reachableSquares(board, from, color)));
        return List.copyOf(squares);
    }

    // Delega en el attackedSquares de cada parte: con el default, una pieza que compone
    // un peón atacaría las casillas a las que avanza en vez de sus diagonales.
    @Override
    public List<Position> attackedSquares(Board board, Position from, PieceColor color) {
        Set<Position> squares = new LinkedHashSet<>();
        strategies.forEach(s -> squares.addAll(s.attackedSquares(board, from, color)));
        return List.copyOf(squares);
    }
}

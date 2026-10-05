package ajedrez.core;

import java.util.List;

public class JumpMovement implements MovementStrategy {

    private final List<Direction> offsets;

    public JumpMovement(List<Direction> offsets) {
        this.offsets = List.copyOf(offsets);
    }

    // No mira casillas intermedias: por eso salta por encima de otras piezas.
    @Override
    public List<Position> reachableSquares(Board board, Position from, PieceColor color) {
        return offsets.stream()
                .map(from::plus)
                .filter(board.size()::contains)
                .toList();
    }
}

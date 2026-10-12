package ajedrez.core;

// También cubre el origen fuera del tablero: ahí no puede haber pieza.
public class SourceHasPieceRule implements MoveRule {

    @Override
    public boolean isSatisfiedBy(Board board, Move move, PieceColor turn) {
        return board.size().contains(move.from()) && !board.isEmpty(move.from());
    }

    @Override
    public String reason() {
        return "No hay una pieza en la casilla de origen";
    }
}

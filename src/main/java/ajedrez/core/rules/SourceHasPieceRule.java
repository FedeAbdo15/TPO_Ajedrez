package ajedrez.core.rules;

import ajedrez.core.domain.Board;
import ajedrez.core.domain.Move;
import ajedrez.core.domain.PieceColor;

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

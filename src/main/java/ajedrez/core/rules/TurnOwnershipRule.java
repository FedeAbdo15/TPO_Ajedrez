package ajedrez.core.rules;

import ajedrez.core.domain.Board;
import ajedrez.core.domain.Move;
import ajedrez.core.domain.PieceColor;

// Asume que SourceHasPieceRule ya comprobó que hay una pieza en el origen.
public class TurnOwnershipRule implements MoveRule {

    @Override
    public boolean isSatisfiedBy(Board board, Move move, PieceColor turn) {
        return board.pieceAt(move.from()).color() == turn;
    }

    @Override
    public String reason() {
        return "La pieza no pertenece al jugador que tiene el turno";
    }
}

package ajedrez.core.rules;

import ajedrez.core.domain.Board;
import ajedrez.core.domain.Move;
import ajedrez.core.domain.PieceColor;

public interface MoveRule {

    boolean isSatisfiedBy(Board board, Move move, PieceColor turn);

    // Motivo del rechazo cuando la regla no se cumple.
    String reason();
}

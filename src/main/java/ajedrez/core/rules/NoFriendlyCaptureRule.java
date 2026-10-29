package ajedrez.core.rules;

import ajedrez.core.domain.Board;
import ajedrez.core.domain.Move;
import ajedrez.core.domain.Piece;
import ajedrez.core.domain.PieceColor;

// Asume que el destino está dentro del tablero (lo garantiza PieceMovementRule antes en la cadena).
public class NoFriendlyCaptureRule implements MoveRule {

    @Override
    public boolean isSatisfiedBy(Board board, Move move, PieceColor turn) {
        Piece target = board.pieceAt(move.to());
        return target == null || board.pieceAt(move.from()).isOpponentOf(target);
    }

    @Override
    public String reason() {
        return "No se puede capturar una pieza propia";
    }
}

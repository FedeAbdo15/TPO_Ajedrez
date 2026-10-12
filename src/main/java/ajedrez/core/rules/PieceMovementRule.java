package ajedrez.core.rules;

import ajedrez.core.domain.Board;
import ajedrez.core.domain.Move;
import ajedrez.core.domain.Piece;
import ajedrez.core.domain.PieceColor;

// Las estrategias solo devuelven casillas dentro del tablero, así que esta regla
// también rechaza un destino fuera de él.
public class PieceMovementRule implements MoveRule {

    @Override
    public boolean isSatisfiedBy(Board board, Move move, PieceColor turn) {
        Piece piece = board.pieceAt(move.from());
        return piece.reachableSquares(board, move.from()).contains(move.to());
    }

    @Override
    public String reason() {
        return "La pieza no puede moverse a esa casilla";
    }
}

package ajedrez.core;

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

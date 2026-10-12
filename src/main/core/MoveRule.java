package ajedrez.core;

public interface MoveRule {

    boolean isSatisfiedBy(Board board, Move move, PieceColor turn);

    // Motivo del rechazo cuando la regla no se cumple.
    String reason();
}

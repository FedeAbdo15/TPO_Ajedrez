package ajedrez.core;

// Va última en la cadena: asume que el movimiento ya pasó las demás reglas.
public class KingSafetyRule implements MoveRule {

    private final CheckDetector detector;

    public KingSafetyRule(CheckDetector detector) {
        this.detector = detector;
    }

    // Simula el movimiento sobre una copia para no tocar el tablero real.
    @Override
    public boolean isSatisfiedBy(Board board, Move move, PieceColor turn) {
        Board simulated = board.copy();
        simulated.movePiece(move.from(), move.to());
        return !detector.isInCheck(simulated, turn);
    }

    @Override
    public String reason() {
        return "El movimiento deja al propio rey en jaque";
    }
}

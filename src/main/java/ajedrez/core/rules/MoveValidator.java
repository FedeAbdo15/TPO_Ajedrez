package ajedrez.core.rules;

import ajedrez.core.domain.Board;
import ajedrez.core.domain.Move;
import ajedrez.core.domain.PieceColor;

import java.util.List;

public class MoveValidator {

    private final List<MoveRule> rules;

    public MoveValidator(List<MoveRule> rules) {
        if (rules.isEmpty()) {
            throw new IllegalArgumentException("El validador necesita al menos una regla");
        }
        this.rules = List.copyOf(rules);
    }

    // Evalúa las reglas en el orden recibido y se detiene en la primera que no se cumple:
    // cada regla puede asumir que las anteriores pasaron.
    public ValidationResult validate(Board board, Move move, PieceColor turn) {
        for (MoveRule rule : rules) {
            if (!rule.isSatisfiedBy(board, move, turn)) {
                return new ValidationResult(false, rule.reason());
            }
        }
        return new ValidationResult(true, "");
    }
}

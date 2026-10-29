package ajedrez.core.rules;

import ajedrez.core.domain.Board;
import ajedrez.core.domain.Move;
import ajedrez.core.domain.PieceColor;
import ajedrez.core.domain.Position;

// Decide si el jugador del turno dado tiene al menos un movimiento legal disponible.
// Se usa en Game.status() para detectar jaque mate y ahogado sin recursión:
// MoveValidator → KingSafetyRule → CheckDetector, y LegalMoveChecker llama al
// MoveValidator desde afuera de esa cadena, así que no hay ciclo.
public class LegalMoveChecker {

    // Recorre todas las piezas del color dado y comprueba si alguna tiene al menos
    // un destino que pase el validador completo (incluido KingSafetyRule).
    public boolean hasAnyLegalMove(Board board, PieceColor color, MoveValidator validator) {
        for (Position from : board.positionsOf(color)) {
            for (Position to : board.pieceAt(from).reachableSquares(board, from)) {
                if (validator.validate(board, new Move(from, to), color).valid()) {
                    return true;
                }
            }
        }
        return false;
    }
}

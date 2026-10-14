package ajedrez.core.rules;

import ajedrez.core.domain.Board;
import ajedrez.core.domain.PieceColor;
import ajedrez.core.domain.Position;

public class AttackBasedCheckDetector implements CheckDetector {

    // Pregunta qué casillas atacan las piezas rivales, no a dónde pueden moverse: así el peón da jaque
    // en diagonal y no hacia adelante. Y como usa las estrategias de movimiento y nunca el MoveValidator,
    // KingSafetyRule puede llamarlo sin caer en una recursión infinita.
    @Override
    public boolean isInCheck(Board board, PieceColor color) {
        Position king = board.kingPosition(color);
        return board.positionsOf(color.opposite()).stream()
                .anyMatch(position -> board.pieceAt(position).attackedSquares(board, position).contains(king));
    }
}

package ajedrez.core;

import java.util.List;

public interface MovementStrategy {

    // Casillas a las que la pieza puede llegar. Incluye la primera pieza que bloquea, sea propia o rival:
    // descartar las propias es responsabilidad de las reglas, no de la geometría del movimiento.
    List<Position> reachableSquares(Board board, Position from, PieceColor color);

    // Casillas que la pieza amenaza. Coincide con reachableSquares salvo en piezas que capturan
    // distinto de como se mueven (el peón).
    default List<Position> attackedSquares(Board board, Position from, PieceColor color) {
        return reachableSquares(board, from, color);
    }
}

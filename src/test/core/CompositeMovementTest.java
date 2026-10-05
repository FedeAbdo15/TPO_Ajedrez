package ajedrez.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompositeMovementTest {

    private final Board board = new Board(new BoardSize(8, 8));

    // La reina no tiene código propio: combina la estrategia de la torre y la del alfil.
    private static Piece queen() {
        MovementStrategy movement = new CompositeMovement(List.of(
                new SlidingMovement(Directions.ORTHOGONAL),
                new SlidingMovement(Directions.DIAGONAL)));
        return new Piece(PieceType.QUEEN, PieceColor.WHITE, movement);
    }

    private static Piece blocker() {
        return new Piece(PieceType.PAWN, PieceColor.BLACK, new PawnMovement());
    }

    @Test
    @DisplayName("La reina se mueve en línea recta y en diagonal")
    void laReinaSeMueveEnLineaRectaYEnDiagonal() {
        List<Position> reachable = queen().reachableSquares(board, new Position(3, 3));

        assertEquals(27, reachable.size());
        assertTrue(reachable.contains(new Position(3, 7)));
        assertTrue(reachable.contains(new Position(7, 7)));
    }

    @Test
    @DisplayName("La reina no salta como el caballo")
    void laReinaNoSaltaComoElCaballo() {
        List<Position> reachable = queen().reachableSquares(board, new Position(3, 3));

        assertFalse(reachable.contains(new Position(5, 4)));
    }

    @Test
    @DisplayName("La reina queda bloqueada por piezas intermedias en ambas estrategias")
    void laReinaQuedaBloqueadaPorPiezasIntermediasEnAmbasEstrategias() {
        board.placePiece(blocker(), new Position(3, 5));
        board.placePiece(blocker(), new Position(5, 5));

        List<Position> reachable = queen().reachableSquares(board, new Position(3, 3));

        assertFalse(reachable.contains(new Position(3, 6)));
        assertFalse(reachable.contains(new Position(6, 6)));
    }

    @Test
    @DisplayName("Una composición con peón ataca con el ataque del peón, no con su avance")
    void unaComposicionConPeonAtacaConElAtaqueDelPeon() {
        MovementStrategy movement = new CompositeMovement(List.of(
                new PawnMovement(), new JumpMovement(Directions.KNIGHT_OFFSETS)));

        List<Position> attacked = movement.attackedSquares(board, new Position(3, 4), PieceColor.WHITE);

        assertFalse(attacked.contains(new Position(4, 4)));
        assertTrue(attacked.containsAll(Set.of(new Position(4, 3), new Position(4, 5))));
    }
}

package ajedrez.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JumpMovementTest {

    private final Board board = new Board(new BoardSize(8, 8));

    private static Piece knight() {
        return new Piece(PieceType.KNIGHT, PieceColor.WHITE, new JumpMovement(Directions.KNIGHT_OFFSETS));
    }

    private static Piece blocker() {
        return new Piece(PieceType.PAWN, PieceColor.WHITE, new PawnMovement());
    }

    @Test
    @DisplayName("El caballo alcanza las ocho casillas en L desde el centro")
    void elCaballoAlcanzaLasOchoCasillasEnLDesdeElCentro() {
        List<Position> reachable = knight().reachableSquares(board, new Position(3, 3));

        assertEquals(8, reachable.size());
        assertTrue(reachable.contains(new Position(5, 4)));
        assertTrue(reachable.contains(new Position(2, 1)));
    }

    @Test
    @DisplayName("El caballo no se mueve en línea recta ni en diagonal")
    void elCaballoNoSeMueveEnLineaRectaNiEnDiagonal() {
        List<Position> reachable = knight().reachableSquares(board, new Position(3, 3));

        assertFalse(reachable.contains(new Position(4, 4)));
        assertFalse(reachable.contains(new Position(5, 3)));
    }

    @Test
    @DisplayName("El caballo salta por encima de las piezas que lo rodean")
    void elCaballoSaltaPorEncimaDeLasPiezasQueLoRodean() {
        Position from = new Position(0, 1);
        for (Position neighbour : List.of(new Position(0, 0), new Position(0, 2),
                new Position(1, 0), new Position(1, 1), new Position(1, 2))) {
            board.placePiece(blocker(), neighbour);
        }

        List<Position> reachable = knight().reachableSquares(board, from);

        assertTrue(reachable.contains(new Position(2, 2)));
    }

    @Test
    @DisplayName("Desde una esquina el caballo descarta las casillas fuera del tablero")
    void desdeUnaEsquinaElCaballoDescartaLasCasillasFueraDelTablero() {
        List<Position> reachable = knight().reachableSquares(board, new Position(0, 0));

        assertEquals(List.of(new Position(2, 1), new Position(1, 2)), reachable);
    }
}

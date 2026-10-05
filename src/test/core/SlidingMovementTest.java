package ajedrez.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlidingMovementTest {

    private final Board board = new Board(new BoardSize(8, 8));

    private static Piece rook() {
        return new Piece(PieceType.ROOK, PieceColor.WHITE, new SlidingMovement(Directions.ORTHOGONAL));
    }

    private static Piece bishop() {
        return new Piece(PieceType.BISHOP, PieceColor.WHITE, new SlidingMovement(Directions.DIAGONAL));
    }

    private static Piece king() {
        List<Direction> all = new ArrayList<>(Directions.ORTHOGONAL);
        all.addAll(Directions.DIAGONAL);
        return new Piece(PieceType.KING, PieceColor.WHITE, new SlidingMovement(all, 1));
    }

    private static Piece blocker() {
        return new Piece(PieceType.PAWN, PieceColor.BLACK, new PawnMovement());
    }

    @Test
    @DisplayName("La torre recorre su fila y su columna hasta el borde")
    void laTorreRecorreSuFilaYSuColumnaHastaElBorde() {
        Position from = new Position(3, 3);

        List<Position> reachable = rook().reachableSquares(board, from);

        assertEquals(14, reachable.size());
        assertTrue(reachable.contains(new Position(3, 7)));
        assertTrue(reachable.contains(new Position(0, 3)));
    }

    @Test
    @DisplayName("La torre no se mueve en diagonal")
    void laTorreNoSeMueveEnDiagonal() {
        List<Position> reachable = rook().reachableSquares(board, new Position(3, 3));

        assertFalse(reachable.contains(new Position(4, 4)));
    }

    @Test
    @DisplayName("La torre se detiene en la pieza que la bloquea, incluyéndola")
    void laTorreSeDetieneEnLaPiezaQueLaBloquea() {
        board.placePiece(blocker(), new Position(0, 3));

        List<Position> reachable = rook().reachableSquares(board, new Position(0, 0));

        assertTrue(reachable.contains(new Position(0, 3)));
        assertFalse(reachable.contains(new Position(0, 4)));
    }

    @Test
    @DisplayName("El alfil recorre sus diagonales hasta el borde")
    void elAlfilRecorreSusDiagonalesHastaElBorde() {
        List<Position> reachable = bishop().reachableSquares(board, new Position(3, 3));

        assertEquals(13, reachable.size());
        assertTrue(reachable.contains(new Position(7, 7)));
        assertTrue(reachable.contains(new Position(0, 0)));
    }

    @Test
    @DisplayName("El alfil no se mueve en línea recta")
    void elAlfilNoSeMueveEnLineaRecta() {
        List<Position> reachable = bishop().reachableSquares(board, new Position(3, 3));

        assertFalse(reachable.contains(new Position(3, 5)));
    }

    @Test
    @DisplayName("El alfil no atraviesa la pieza que lo bloquea")
    void elAlfilNoAtraviesaLaPiezaQueLoBloquea() {
        board.placePiece(blocker(), new Position(5, 5));

        List<Position> reachable = bishop().reachableSquares(board, new Position(3, 3));

        assertTrue(reachable.contains(new Position(5, 5)));
        assertFalse(reachable.contains(new Position(6, 6)));
    }

    @Test
    @DisplayName("El rey alcanza las ocho casillas vecinas")
    void elReyAlcanzaLasOchoCasillasVecinas() {
        List<Position> reachable = king().reachableSquares(board, new Position(3, 3));

        assertEquals(8, reachable.size());
        assertTrue(reachable.contains(new Position(4, 4)));
        assertTrue(reachable.contains(new Position(2, 3)));
    }

    @Test
    @DisplayName("El rey no avanza dos casillas")
    void elReyNoAvanzaDosCasillas() {
        List<Position> reachable = king().reachableSquares(board, new Position(3, 3));

        assertFalse(reachable.contains(new Position(5, 3)));
    }

    @Test
    @DisplayName("En un tablero más grande la torre llega hasta su borde real")
    void enUnTableroMasGrandeLaTorreLlegaHastaSuBordeReal() {
        Board largeBoard = new Board(new BoardSize(10, 10));

        List<Position> reachable = rook().reachableSquares(largeBoard, new Position(0, 0));

        assertEquals(18, reachable.size());
        assertTrue(reachable.contains(new Position(0, 9)));
    }
}

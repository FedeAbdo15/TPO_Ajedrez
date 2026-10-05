package ajedrez.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PawnMovementTest {

    private final Board board = new Board(new BoardSize(8, 8));

    private static Piece pawn(PieceColor color) {
        return new Piece(PieceType.PAWN, color, new PawnMovement());
    }

    @Test
    @DisplayName("El peón blanco en su fila inicial avanza una o dos casillas")
    void elPeonBlancoEnSuFilaInicialAvanzaUnaODosCasillas() {
        List<Position> reachable = pawn(PieceColor.WHITE).reachableSquares(board, new Position(1, 4));

        assertEquals(List.of(new Position(2, 4), new Position(3, 4)), reachable);
    }

    @Test
    @DisplayName("El peón negro avanza hacia las filas menores")
    void elPeonNegroAvanzaHaciaLasFilasMenores() {
        List<Position> reachable = pawn(PieceColor.BLACK).reachableSquares(board, new Position(6, 4));

        assertEquals(List.of(new Position(5, 4), new Position(4, 4)), reachable);
    }

    @Test
    @DisplayName("Fuera de la fila inicial el peón no puede avanzar dos casillas")
    void fueraDeLaFilaInicialElPeonNoPuedeAvanzarDosCasillas() {
        List<Position> reachable = pawn(PieceColor.WHITE).reachableSquares(board, new Position(2, 4));

        assertEquals(List.of(new Position(3, 4)), reachable);
    }

    @Test
    @DisplayName("El avance doble queda bloqueado si hay una pieza en la casilla intermedia")
    void elAvanceDobleQuedaBloqueadoSiHayUnaPiezaEnLaCasillaIntermedia() {
        board.placePiece(pawn(PieceColor.BLACK), new Position(2, 4));

        List<Position> reachable = pawn(PieceColor.WHITE).reachableSquares(board, new Position(1, 4));

        assertFalse(reachable.contains(new Position(3, 4)));
    }

    @Test
    @DisplayName("El peón no captura hacia adelante")
    void elPeonNoCapturaHaciaAdelante() {
        board.placePiece(pawn(PieceColor.BLACK), new Position(4, 4));

        List<Position> reachable = pawn(PieceColor.WHITE).reachableSquares(board, new Position(3, 4));

        assertTrue(reachable.isEmpty());
    }

    @Test
    @DisplayName("El peón captura en diagonal cuando hay una pieza")
    void elPeonCapturaEnDiagonalCuandoHayUnaPieza() {
        board.placePiece(pawn(PieceColor.BLACK), new Position(4, 5));

        List<Position> reachable = pawn(PieceColor.WHITE).reachableSquares(board, new Position(3, 4));

        assertTrue(reachable.contains(new Position(4, 5)));
    }

    @Test
    @DisplayName("El peón no se mueve en diagonal a una casilla vacía")
    void elPeonNoSeMueveEnDiagonalAUnaCasillaVacia() {
        List<Position> reachable = pawn(PieceColor.WHITE).reachableSquares(board, new Position(3, 4));

        assertFalse(reachable.contains(new Position(4, 5)));
        assertFalse(reachable.contains(new Position(4, 3)));
    }

    @Test
    @DisplayName("El peón ataca sus diagonales aunque estén vacías, y no la casilla a la que avanza")
    void elPeonAtacaSusDiagonalesYNoLaCasillaALaQueAvanza() {
        List<Position> attacked = pawn(PieceColor.WHITE).attackedSquares(board, new Position(3, 4));

        assertEquals(Set.of(new Position(4, 3), new Position(4, 5)), Set.copyOf(attacked));
    }

    @Test
    @DisplayName("En un tablero de 10 filas el peón negro arranca en la fila 8")
    void enUnTableroDeDiezFilasElPeonNegroArrancaEnLaFilaOcho() {
        Board largeBoard = new Board(new BoardSize(10, 10));

        List<Position> reachable = pawn(PieceColor.BLACK).reachableSquares(largeBoard, new Position(8, 0));

        assertTrue(reachable.contains(new Position(6, 0)));
    }
}

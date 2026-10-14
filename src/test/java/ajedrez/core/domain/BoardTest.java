package ajedrez.core.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardTest {

    private final BoardSize size = new BoardSize(8, 8);

    // El tablero no depende de cómo se mueven las piezas: alcanza con una estrategia vacía.
    private static Piece piece(PieceType type, PieceColor color) {
        return new Piece(type, color, (board, from, pieceColor) -> List.of());
    }

    @Test
    @DisplayName("Poner una pieza la deja disponible en esa posición")
    void ponerUnaPiezaLaDejaDisponibleEnEsaPosicion() {
        Board board = new Board(size);
        Piece rook = piece(PieceType.ROOK, PieceColor.WHITE);
        Position position = new Position(0, 0);

        board.placePiece(rook, position);

        assertEquals(rook, board.pieceAt(position));
    }

    @Test
    @DisplayName("Sacar una pieza la quita del tablero y la devuelve")
    void sacarUnaPiezaLaQuitaDelTableroYLaDevuelve() {
        Board board = new Board(size);
        Piece rook = piece(PieceType.ROOK, PieceColor.WHITE);
        Position position = new Position(0, 0);
        board.placePiece(rook, position);

        Piece removed = board.removePiece(position);

        assertEquals(rook, removed);
        assertTrue(board.isEmpty(position));
    }

    @Test
    @DisplayName("Una casilla sin pieza está vacía")
    void unaCasillaSinPiezaEstaVacia() {
        Board board = new Board(size);
        Position position = new Position(3, 3);

        assertTrue(board.isEmpty(position));
        assertNull(board.pieceAt(position));
    }

    @Test
    @DisplayName("Mover una pieza la traslada de origen a destino")
    void moverUnaPiezaLaTrasladaDeOrigenADestino() {
        Board board = new Board(size);
        Piece knight = piece(PieceType.KNIGHT, PieceColor.BLACK);
        Position from = new Position(1, 1);
        Position to = new Position(2, 3);
        board.placePiece(knight, from);

        board.movePiece(from, to);

        assertTrue(board.isEmpty(from));
        assertEquals(knight, board.pieceAt(to));
    }

    @Test
    @DisplayName("La copia del tablero es independiente del original")
    void laCopiaDelTableroEsIndependienteDelOriginal() {
        Board board = new Board(size);
        Piece bishop = piece(PieceType.BISHOP, PieceColor.WHITE);
        Position position = new Position(2, 2);
        board.placePiece(bishop, position);

        Board copy = board.copy();
        copy.removePiece(position);

        assertEquals(bishop, board.pieceAt(position));
        assertTrue(copy.isEmpty(position));
        assertEquals(board, board.copy());
    }

    @Test
    @DisplayName("Una posición fuera del tablero es rechazada")
    void unaPosicionFueraDelTableroEsRechazada() {
        Board board = new Board(size);
        Position outOfBounds = new Position(8, 0);

        assertThrows(IllegalArgumentException.class, () -> board.pieceAt(outOfBounds));
    }

    @Test
    @DisplayName("Las posiciones de un color incluyen solo sus piezas")
    void lasPosicionesDeUnColorIncluyenSoloSusPiezas() {
        Board board = new Board(size);
        board.placePiece(piece(PieceType.ROOK, PieceColor.WHITE), new Position(0, 0));
        board.placePiece(piece(PieceType.KING, PieceColor.WHITE), new Position(0, 4));
        board.placePiece(piece(PieceType.KING, PieceColor.BLACK), new Position(7, 4));

        List<Position> white = board.positionsOf(PieceColor.WHITE);

        assertEquals(Set.of(new Position(0, 0), new Position(0, 4)), Set.copyOf(white));
    }

    @Test
    @DisplayName("El tablero encuentra la posición del rey de cada color")
    void elTableroEncuentraLaPosicionDelReyDeCadaColor() {
        Board board = new Board(size);
        board.placePiece(piece(PieceType.KING, PieceColor.WHITE), new Position(0, 4));
        board.placePiece(piece(PieceType.KING, PieceColor.BLACK), new Position(7, 4));

        assertEquals(new Position(0, 4), board.kingPosition(PieceColor.WHITE));
        assertEquals(new Position(7, 4), board.kingPosition(PieceColor.BLACK));
    }

    @Test
    @DisplayName("Buscar el rey en un tablero que no lo tiene es un error")
    void buscarElReyEnUnTableroQueNoLoTieneEsUnError() {
        Board board = new Board(size);
        board.placePiece(piece(PieceType.ROOK, PieceColor.WHITE), new Position(0, 0));

        assertThrows(IllegalStateException.class, () -> board.kingPosition(PieceColor.WHITE));
    }
}

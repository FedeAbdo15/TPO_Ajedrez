package ajedrez.core.setup;

import ajedrez.core.domain.Board;
import ajedrez.core.domain.BoardSize;
import ajedrez.core.domain.Piece;
import ajedrez.core.domain.PieceColor;
import ajedrez.core.domain.PieceType;
import ajedrez.core.domain.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StandardPieceFactoryTest {

    private final PieceFactory factory = new StandardPieceFactory();
    private final Board board = new Board(new BoardSize(8, 8));

    @Test
    @DisplayName("La fábrica crea una pieza del tipo y color pedidos")
    void laFabricaCreaUnaPiezaDelTipoYColorPedidos() {
        Piece piece = factory.create(PieceType.KNIGHT, PieceColor.BLACK);

        assertEquals(PieceType.KNIGHT, piece.type());
        assertEquals(PieceColor.BLACK, piece.color());
    }

    @Test
    @DisplayName("Cada tipo de pieza sale de la fábrica con su movimiento")
    void cadaTipoDePiezaSaleDeLaFabricaConSuMovimiento() {
        Position from = new Position(3, 3);

        assertEquals(14, factory.create(PieceType.ROOK, PieceColor.WHITE).reachableSquares(board, from).size());
        assertEquals(13, factory.create(PieceType.BISHOP, PieceColor.WHITE).reachableSquares(board, from).size());
        assertEquals(27, factory.create(PieceType.QUEEN, PieceColor.WHITE).reachableSquares(board, from).size());
        assertEquals(8, factory.create(PieceType.KING, PieceColor.WHITE).reachableSquares(board, from).size());
        assertEquals(8, factory.create(PieceType.KNIGHT, PieceColor.WHITE).reachableSquares(board, from).size());
        assertEquals(List.of(new Position(4, 3)),
                factory.create(PieceType.PAWN, PieceColor.WHITE).reachableSquares(board, from));
    }

    @Test
    @DisplayName("El mismo tipo de pieza respeta el color con el que se creó")
    void elMismoTipoDePiezaRespetaElColorConElQueSeCreo() {
        Position from = new Position(3, 3);

        List<Position> white = factory.create(PieceType.PAWN, PieceColor.WHITE).reachableSquares(board, from);
        List<Position> black = factory.create(PieceType.PAWN, PieceColor.BLACK).reachableSquares(board, from);

        assertEquals(List.of(new Position(4, 3)), white);
        assertEquals(List.of(new Position(2, 3)), black);
    }

    @Test
    @DisplayName("La fábrica estándar rechaza un tipo de pieza que no conoce")
    void laFabricaEstandarRechazaUnTipoQueNoConoce() {
        PieceType unknown = new PieceType("CHANCELLOR");

        assertThrows(IllegalArgumentException.class, () -> factory.create(unknown, PieceColor.WHITE));
    }
}

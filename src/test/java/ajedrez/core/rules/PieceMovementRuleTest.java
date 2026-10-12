package ajedrez.core.rules;

import ajedrez.core.domain.Board;
import ajedrez.core.domain.BoardSize;
import ajedrez.core.domain.Move;
import ajedrez.core.domain.PieceColor;
import ajedrez.core.domain.PieceType;
import ajedrez.core.domain.Position;
import ajedrez.core.setup.PieceFactory;
import ajedrez.core.setup.StandardPieceFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PieceMovementRuleTest {

    private final MoveRule rule = new PieceMovementRule();
    private final PieceFactory factory = new StandardPieceFactory();
    private final Board board = new Board(new BoardSize(8, 8));
    private final Position from = new Position(0, 0);

    @Test
    @DisplayName("Se cumple si la pieza puede llegar al destino")
    void seCumpleSiLaPiezaPuedeLlegarAlDestino() {
        board.placePiece(factory.create(PieceType.ROOK, PieceColor.WHITE), from);

        assertTrue(rule.isSatisfiedBy(board, new Move(from, new Position(0, 7)), PieceColor.WHITE));
    }

    @Test
    @DisplayName("Rechaza un destino que la pieza no alcanza con su movimiento")
    void rechazaUnDestinoQueLaPiezaNoAlcanza() {
        board.placePiece(factory.create(PieceType.ROOK, PieceColor.WHITE), from);

        assertFalse(rule.isSatisfiedBy(board, new Move(from, new Position(3, 3)), PieceColor.WHITE));
    }

    @Test
    @DisplayName("Rechaza un destino fuera del tablero sin lanzar excepción")
    void rechazaUnDestinoFueraDelTablero() {
        board.placePiece(factory.create(PieceType.ROOK, PieceColor.WHITE), from);

        assertFalse(rule.isSatisfiedBy(board, new Move(from, new Position(0, 8)), PieceColor.WHITE));
    }
}

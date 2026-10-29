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

class NoFriendlyCaptureRuleTest {

    private final MoveRule rule = new NoFriendlyCaptureRule();
    private final PieceFactory factory = new StandardPieceFactory();
    private final Board board = new Board(new BoardSize(8, 8));
    private final Move move = new Move(new Position(0, 0), new Position(0, 5));

    @Test
    @DisplayName("Se cumple si el destino está vacío")
    void seCumpleSiElDestinoEstaVacio() {
        board.placePiece(factory.create(PieceType.ROOK, PieceColor.WHITE), move.from());

        assertTrue(rule.isSatisfiedBy(board, move, PieceColor.WHITE));
    }

    @Test
    @DisplayName("Se cumple si el destino tiene una pieza rival")
    void seCumpleSiElDestinoTieneUnaPiezaRival() {
        board.placePiece(factory.create(PieceType.ROOK, PieceColor.WHITE), move.from());
        board.placePiece(factory.create(PieceType.KNIGHT, PieceColor.BLACK), move.to());

        assertTrue(rule.isSatisfiedBy(board, move, PieceColor.WHITE));
    }

    @Test
    @DisplayName("Rechaza capturar una pieza propia")
    void rechazaCapturarUnaPiezaPropia() {
        board.placePiece(factory.create(PieceType.ROOK, PieceColor.WHITE), move.from());
        board.placePiece(factory.create(PieceType.KNIGHT, PieceColor.WHITE), move.to());

        assertFalse(rule.isSatisfiedBy(board, move, PieceColor.WHITE));
    }
}

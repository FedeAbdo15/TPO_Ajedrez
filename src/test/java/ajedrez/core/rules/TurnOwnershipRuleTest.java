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

class TurnOwnershipRuleTest {

    private final MoveRule rule = new TurnOwnershipRule();
    private final PieceFactory factory = new StandardPieceFactory();
    private final Board board = new Board(new BoardSize(8, 8));
    private final Move move = new Move(new Position(0, 0), new Position(0, 5));

    @Test
    @DisplayName("Se cumple si la pieza es del jugador que tiene el turno")
    void seCumpleSiLaPiezaEsDelJugadorQueTieneElTurno() {
        board.placePiece(factory.create(PieceType.ROOK, PieceColor.WHITE), move.from());

        assertTrue(rule.isSatisfiedBy(board, move, PieceColor.WHITE));
    }

    @Test
    @DisplayName("Rechaza mover una pieza del rival")
    void rechazaMoverUnaPiezaDelRival() {
        board.placePiece(factory.create(PieceType.ROOK, PieceColor.BLACK), move.from());

        assertFalse(rule.isSatisfiedBy(board, move, PieceColor.WHITE));
    }
}

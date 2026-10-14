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

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KingSafetyRuleTest {

    private final MoveRule rule = new KingSafetyRule(new AttackBasedCheckDetector());
    private final PieceFactory factory = new StandardPieceFactory();
    private final Board board = new Board(new BoardSize(8, 8));

    private void place(PieceType type, PieceColor color, int row, int column) {
        board.placePiece(factory.create(type, color), new Position(row, column));
    }

    private static Move move(int fromRow, int fromColumn, int toRow, int toColumn) {
        return new Move(new Position(fromRow, fromColumn), new Position(toRow, toColumn));
    }

    @Test
    @DisplayName("Rechaza sacar de la línea una pieza que protege al rey")
    void rechazaSacarDeLaLineaUnaPiezaQueProtegeAlRey() {
        place(PieceType.KING, PieceColor.WHITE, 0, 4);
        place(PieceType.ROOK, PieceColor.WHITE, 1, 4);
        place(PieceType.ROOK, PieceColor.BLACK, 7, 4);

        assertFalse(rule.isSatisfiedBy(board, move(1, 4, 1, 0), PieceColor.WHITE));
    }

    @Test
    @DisplayName("Acepta mover la pieza que protege al rey sin salir de la línea")
    void aceptaMoverLaPiezaQueProtegeAlReySinSalirDeLaLinea() {
        place(PieceType.KING, PieceColor.WHITE, 0, 4);
        place(PieceType.ROOK, PieceColor.WHITE, 1, 4);
        place(PieceType.ROOK, PieceColor.BLACK, 7, 4);

        assertTrue(rule.isSatisfiedBy(board, move(1, 4, 5, 4), PieceColor.WHITE));
    }

    @Test
    @DisplayName("Rechaza mover el rey a una casilla atacada")
    void rechazaMoverElReyAUnaCasillaAtacada() {
        place(PieceType.KING, PieceColor.WHITE, 0, 4);
        place(PieceType.ROOK, PieceColor.BLACK, 7, 3);

        assertFalse(rule.isSatisfiedBy(board, move(0, 4, 0, 3), PieceColor.WHITE));
    }

    @Test
    @DisplayName("Acepta sacar al rey del jaque moviéndolo")
    void aceptaSacarAlReyDelJaqueMoviendolo() {
        place(PieceType.KING, PieceColor.WHITE, 0, 4);
        place(PieceType.ROOK, PieceColor.BLACK, 7, 4);

        assertTrue(rule.isSatisfiedBy(board, move(0, 4, 0, 3), PieceColor.WHITE));
    }

    @Test
    @DisplayName("Acepta sacar al rey del jaque interponiendo una pieza")
    void aceptaSacarAlReyDelJaqueInterponiendoUnaPieza() {
        place(PieceType.KING, PieceColor.WHITE, 0, 4);
        place(PieceType.ROOK, PieceColor.WHITE, 3, 0);
        place(PieceType.ROOK, PieceColor.BLACK, 7, 4);

        assertTrue(rule.isSatisfiedBy(board, move(3, 0, 3, 4), PieceColor.WHITE));
    }

    @Test
    @DisplayName("Acepta sacar al rey del jaque capturando a la pieza atacante")
    void aceptaSacarAlReyDelJaqueCapturandoALaPiezaAtacante() {
        place(PieceType.KING, PieceColor.WHITE, 0, 4);
        place(PieceType.ROOK, PieceColor.WHITE, 7, 0);
        place(PieceType.ROOK, PieceColor.BLACK, 7, 4);

        assertTrue(rule.isSatisfiedBy(board, move(7, 0, 7, 4), PieceColor.WHITE));
    }

    @Test
    @DisplayName("Rechaza un movimiento que no resuelve el jaque")
    void rechazaUnMovimientoQueNoResuelveElJaque() {
        place(PieceType.KING, PieceColor.WHITE, 0, 4);
        place(PieceType.KNIGHT, PieceColor.WHITE, 0, 1);
        place(PieceType.ROOK, PieceColor.BLACK, 7, 4);

        assertFalse(rule.isSatisfiedBy(board, move(0, 1, 2, 2), PieceColor.WHITE));
    }

    @Test
    @DisplayName("La simulación no modifica el tablero real")
    void laSimulacionNoModificaElTableroReal() {
        place(PieceType.KING, PieceColor.WHITE, 0, 4);
        place(PieceType.ROOK, PieceColor.WHITE, 7, 0);
        place(PieceType.ROOK, PieceColor.BLACK, 7, 4);
        Board before = board.copy();

        rule.isSatisfiedBy(board, move(7, 0, 7, 4), PieceColor.WHITE);

        assertEquals(before, board);
    }

    @Test
    @DisplayName("Al final de la cadena del validador rechaza dejar al rey en jaque, sin recursión")
    void alFinalDeLaCadenaDelValidadorRechazaDejarAlReyEnJaque() {
        MoveValidator validator = new MoveValidator(List.of(
                new SourceHasPieceRule(), new TurnOwnershipRule(), new PieceMovementRule(),
                new NoFriendlyCaptureRule(), rule));
        place(PieceType.KING, PieceColor.WHITE, 0, 4);
        place(PieceType.ROOK, PieceColor.WHITE, 1, 4);
        place(PieceType.KING, PieceColor.BLACK, 7, 0);
        place(PieceType.ROOK, PieceColor.BLACK, 7, 4);

        ValidationResult result = validator.validate(board, move(1, 4, 1, 0), PieceColor.WHITE);

        assertFalse(result.valid());
        assertEquals(rule.reason(), result.reason());
    }
}

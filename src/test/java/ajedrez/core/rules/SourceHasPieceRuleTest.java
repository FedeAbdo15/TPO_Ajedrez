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

class SourceHasPieceRuleTest {

    private final MoveRule rule = new SourceHasPieceRule();
    private final PieceFactory factory = new StandardPieceFactory();
    private final Board board = new Board(new BoardSize(8, 8));

    @Test
    @DisplayName("Se cumple si hay una pieza en el origen")
    void seCumpleSiHayUnaPiezaEnElOrigen() {
        board.placePiece(factory.create(PieceType.ROOK, PieceColor.WHITE), new Position(0, 0));

        assertTrue(rule.isSatisfiedBy(board, new Move(new Position(0, 0), new Position(0, 5)), PieceColor.WHITE));
    }

    @Test
    @DisplayName("Rechaza mover desde una casilla vacía")
    void rechazaMoverDesdeUnaCasillaVacia() {
        assertFalse(rule.isSatisfiedBy(board, new Move(new Position(3, 3), new Position(4, 3)), PieceColor.WHITE));
    }

    @Test
    @DisplayName("Rechaza un origen fuera del tablero sin lanzar excepción")
    void rechazaUnOrigenFueraDelTablero() {
        assertFalse(rule.isSatisfiedBy(board, new Move(new Position(-1, 0), new Position(0, 0)), PieceColor.WHITE));
    }
}

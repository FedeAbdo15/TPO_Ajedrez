package ajedrez.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoveCommandTest {

    private final PieceFactory factory = new StandardPieceFactory();
    private final Board board = new Board(new BoardSize(8, 8));
    private final Piece rook = factory.create(PieceType.ROOK, PieceColor.WHITE);
    private final Position from = new Position(0, 0);
    private final Position to = new Position(0, 5);

    @Test
    @DisplayName("Ejecutar mueve la pieza de origen a destino")
    void ejecutarMueveLaPiezaDeOrigenADestino() {
        board.placePiece(rook, from);

        new MoveCommand(board, new Move(from, to)).execute();

        assertTrue(board.isEmpty(from));
        assertEquals(rook, board.pieceAt(to));
    }

    @Test
    @DisplayName("Deshacer un movimiento sin captura deja el tablero como estaba")
    void deshacerUnMovimientoSinCapturaDejaElTableroComoEstaba() {
        board.placePiece(rook, from);
        Board before = board.copy();
        Command command = new MoveCommand(board, new Move(from, to));
        command.execute();

        command.undo();

        assertEquals(before, board);
    }

    @Test
    @DisplayName("Deshacer una captura devuelve la pieza capturada a su casilla")
    void deshacerUnaCapturaDevuelveLaPiezaCapturada() {
        Piece knight = factory.create(PieceType.KNIGHT, PieceColor.BLACK);
        board.placePiece(rook, from);
        board.placePiece(knight, to);
        Board before = board.copy();
        Command command = new MoveCommand(board, new Move(from, to));
        command.execute();

        command.undo();

        assertEquals(before, board);
        assertEquals(knight, board.pieceAt(to));
    }

    @Test
    @DisplayName("Deshacer sin haber ejecutado lanza IllegalStateException")
    void deshacerSinHaberEjecutadoLanzaExcepcion() {
        board.placePiece(rook, from);
        Command command = new MoveCommand(board, new Move(from, to));

        assertThrows(IllegalStateException.class, command::undo);
    }

    @Test
    @DisplayName("Ejecutar dos veces el mismo comando lanza IllegalStateException")
    void ejecutarDosVecesElMismoComandoLanzaExcepcion() {
        board.placePiece(rook, from);
        Command command = new MoveCommand(board, new Move(from, to));
        command.execute();

        assertThrows(IllegalStateException.class, command::execute);
    }
}

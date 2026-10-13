package ajedrez.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {

    private final PieceFactory factory = new StandardPieceFactory();
    private final CheckDetector detector = new AttackBasedCheckDetector();
    private final MoveValidator validator = new MoveValidator(List.of(
            new SourceHasPieceRule(), new TurnOwnershipRule(), new PieceMovementRule(),
            new NoFriendlyCaptureRule(), new KingSafetyRule(detector)));

    private Game gameOn(Board board) {
        return new Game(board, validator, detector, new MoveHistory());
    }

    private static Position at(int row, int column) {
        return new Position(row, column);
    }

    @Test
    @DisplayName("La partida empieza con el turno de las blancas y sin jaque")
    void laPartidaEmpiezaConElTurnoDeLasBlancas() {
        Game game = gameOn(new StandardChessSetup(factory).createInitialBoard());

        assertEquals(PieceColor.WHITE, game.currentTurn());
        assertEquals(GameStatus.IN_PROGRESS, game.status());
    }

    @Test
    @DisplayName("Los turnos se alternan después de cada movimiento aplicado")
    void losTurnosSeAlternanDespuesDeCadaMovimiento() {
        Game game = gameOn(new StandardChessSetup(factory).createInitialBoard());

        game.move(at(1, 4), at(3, 4));
        PieceColor afterWhite = game.currentTurn();
        game.move(at(6, 4), at(4, 4));

        assertEquals(PieceColor.BLACK, afterWhite);
        assertEquals(PieceColor.WHITE, game.currentTurn());
    }

    @Test
    @DisplayName("Mover una pieza del rival es rechazado y no cambia el turno ni el tablero")
    void moverUnaPiezaDelRivalEsRechazado() {
        Board board = new StandardChessSetup(factory).createInitialBoard();
        Board before = board.copy();
        Game game = gameOn(board);

        MoveResult result = game.move(at(6, 4), at(4, 4));

        assertFalse(result.applied());
        assertEquals(new TurnOwnershipRule().reason(), result.reason());
        assertEquals(PieceColor.WHITE, game.currentTurn());
        assertEquals(before, board);
    }

    @Test
    @DisplayName("Una captura deja a la pieza que captura en la casilla de la capturada")
    void unaCapturaDejaALaPiezaQueCapturaEnLaCasillaDeLaCapturada() {
        Board board = new Board(new BoardSize(8, 8));
        board.placePiece(factory.create(PieceType.KING, PieceColor.WHITE), at(0, 4));
        board.placePiece(factory.create(PieceType.KING, PieceColor.BLACK), at(7, 4));
        board.placePiece(factory.create(PieceType.ROOK, PieceColor.WHITE), at(0, 0));
        board.placePiece(factory.create(PieceType.KNIGHT, PieceColor.BLACK), at(5, 0));
        Game game = gameOn(board);

        MoveResult result = game.move(at(0, 0), at(5, 0));

        assertTrue(result.applied());
        assertTrue(board.isEmpty(at(0, 0)));
        assertEquals(factory.create(PieceType.ROOK, PieceColor.WHITE), board.pieceAt(at(5, 0)));
        assertEquals(1, board.positionsOf(PieceColor.BLACK).size());
    }

    @Test
    @DisplayName("Undo restaura el tablero exacto, con la pieza capturada, y devuelve el turno")
    void undoRestauraElTableroExactoYDevuelveElTurno() {
        Board board = new StandardChessSetup(factory).createInitialBoard();
        Game game = gameOn(board);
        game.move(at(1, 4), at(3, 4));
        game.move(at(6, 3), at(4, 3));
        Board beforeCapture = board.copy();
        game.move(at(3, 4), at(4, 3));

        game.undo();

        assertEquals(beforeCapture, board);
        assertEquals(PieceColor.WHITE, game.currentTurn());
    }

    @Test
    @DisplayName("Varios undo seguidos vuelven a la posición inicial")
    void variosUndoSeguidosVuelvenALaPosicionInicial() {
        Board board = new StandardChessSetup(factory).createInitialBoard();
        Board initial = board.copy();
        Game game = gameOn(board);
        game.move(at(1, 4), at(3, 4));
        game.move(at(6, 3), at(4, 3));
        game.move(at(3, 4), at(4, 3));

        game.undo();
        game.undo();
        game.undo();

        assertEquals(initial, board);
        assertEquals(PieceColor.WHITE, game.currentTurn());
    }

    @Test
    @DisplayName("Undo sin movimientos lanza IllegalStateException y no cambia el turno")
    void undoSinMovimientosLanzaExcepcion() {
        Game game = gameOn(new StandardChessSetup(factory).createInitialBoard());

        assertThrows(IllegalStateException.class, game::undo);
        assertEquals(PieceColor.WHITE, game.currentTurn());
    }

    @Test
    @DisplayName("Después de dar jaque el estado es CHECK para el rival")
    void despuesDeDarJaqueElEstadoEsCheck() {
        Game game = gameOn(new StandardChessSetup(factory).createInitialBoard());
        game.move(at(1, 4), at(3, 4));
        game.move(at(6, 5), at(5, 5));

        MoveResult result = game.move(at(0, 3), at(4, 7));

        assertTrue(result.applied());
        assertEquals(GameStatus.CHECK, result.status());
        assertEquals(GameStatus.CHECK, game.status());
    }

    @Test
    @DisplayName("Estando en jaque, un movimiento que no lo resuelve es rechazado")
    void estandoEnJaqueUnMovimientoQueNoLoResuelveEsRechazado() {
        Game game = gameOn(new StandardChessSetup(factory).createInitialBoard());
        game.move(at(1, 4), at(3, 4));
        game.move(at(6, 5), at(5, 5));
        game.move(at(0, 3), at(4, 7));

        MoveResult result = game.move(at(6, 0), at(5, 0));

        assertFalse(result.applied());
        assertEquals(new KingSafetyRule(detector).reason(), result.reason());
        assertEquals(GameStatus.CHECK, result.status());
        assertEquals(PieceColor.BLACK, game.currentTurn());
    }

    @Test
    @DisplayName("Estando en jaque, interponer una pieza lo resuelve")
    void estandoEnJaqueInterponerUnaPiezaLoResuelve() {
        Game game = gameOn(new StandardChessSetup(factory).createInitialBoard());
        game.move(at(1, 4), at(3, 4));
        game.move(at(6, 5), at(5, 5));
        game.move(at(0, 3), at(4, 7));

        MoveResult result = game.move(at(6, 6), at(5, 6));

        assertTrue(result.applied());
        assertEquals(GameStatus.IN_PROGRESS, result.status());
    }
}

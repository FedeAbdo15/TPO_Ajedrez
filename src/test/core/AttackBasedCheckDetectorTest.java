package ajedrez.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AttackBasedCheckDetectorTest {

    private final CheckDetector detector = new AttackBasedCheckDetector();
    private final PieceFactory factory = new StandardPieceFactory();
    private final Board board = new Board(new BoardSize(8, 8));

    private void place(PieceType type, PieceColor color, int row, int column) {
        board.placePiece(factory.create(type, color), new Position(row, column));
    }

    @Test
    @DisplayName("Detecta el jaque de una torre rival en la misma columna")
    void detectaElJaqueDeUnaTorreRival() {
        place(PieceType.KING, PieceColor.WHITE, 0, 4);
        place(PieceType.ROOK, PieceColor.BLACK, 7, 4);

        assertTrue(detector.isInCheck(board, PieceColor.WHITE));
    }

    @Test
    @DisplayName("No hay jaque si ninguna pieza rival ataca al rey")
    void noHayJaqueSiNingunaPiezaRivalAtacaAlRey() {
        place(PieceType.KING, PieceColor.WHITE, 0, 4);
        place(PieceType.ROOK, PieceColor.BLACK, 7, 0);

        assertFalse(detector.isInCheck(board, PieceColor.WHITE));
    }

    @Test
    @DisplayName("Una pieza intermedia bloquea el jaque")
    void unaPiezaIntermediaBloqueaElJaque() {
        place(PieceType.KING, PieceColor.WHITE, 0, 4);
        place(PieceType.PAWN, PieceColor.WHITE, 1, 4);
        place(PieceType.ROOK, PieceColor.BLACK, 7, 4);

        assertFalse(detector.isInCheck(board, PieceColor.WHITE));
    }

    @Test
    @DisplayName("El peón rival da jaque en diagonal")
    void elPeonRivalDaJaqueEnDiagonal() {
        place(PieceType.KING, PieceColor.WHITE, 3, 4);
        place(PieceType.PAWN, PieceColor.BLACK, 4, 5);

        assertTrue(detector.isInCheck(board, PieceColor.WHITE));
    }

    @Test
    @DisplayName("El peón rival justo enfrente del rey no le da jaque")
    void elPeonRivalEnfrenteDelReyNoLeDaJaque() {
        place(PieceType.KING, PieceColor.WHITE, 3, 4);
        place(PieceType.PAWN, PieceColor.BLACK, 4, 4);

        assertFalse(detector.isInCheck(board, PieceColor.WHITE));
    }

    @Test
    @DisplayName("El caballo da jaque saltando por encima de las piezas")
    void elCaballoDaJaqueSaltandoPorEncimaDeLasPiezas() {
        place(PieceType.KING, PieceColor.WHITE, 0, 4);
        place(PieceType.PAWN, PieceColor.WHITE, 1, 4);
        place(PieceType.PAWN, PieceColor.WHITE, 1, 5);
        place(PieceType.KNIGHT, PieceColor.BLACK, 2, 5);

        assertTrue(detector.isInCheck(board, PieceColor.WHITE));
    }

    @Test
    @DisplayName("Las piezas propias no dan jaque")
    void lasPiezasPropiasNoDanJaque() {
        place(PieceType.KING, PieceColor.WHITE, 0, 4);
        place(PieceType.ROOK, PieceColor.WHITE, 7, 4);

        assertFalse(detector.isInCheck(board, PieceColor.WHITE));
    }

    @Test
    @DisplayName("También detecta el jaque al rey negro")
    void tambienDetectaElJaqueAlReyNegro() {
        place(PieceType.KING, PieceColor.BLACK, 7, 7);
        place(PieceType.BISHOP, PieceColor.WHITE, 3, 3);

        assertTrue(detector.isInCheck(board, PieceColor.BLACK));
    }

    @Test
    @DisplayName("En la posición inicial ningún rey está en jaque")
    void enLaPosicionInicialNingunReyEstaEnJaque() {
        Board initial = new StandardChessSetup(factory).createInitialBoard();

        assertFalse(detector.isInCheck(initial, PieceColor.WHITE));
        assertFalse(detector.isInCheck(initial, PieceColor.BLACK));
    }
}

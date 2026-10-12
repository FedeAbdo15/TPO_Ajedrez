package ajedrez.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StandardChessSetupTest {

    private static final List<PieceType> BACK_RANK = List.of(
            PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
            PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK);

    private final BoardSetup setup = new StandardChessSetup(new StandardPieceFactory());

    private static int countPieces(Board board) {
        int count = 0;
        for (int row = 0; row < board.size().rows(); row++) {
            for (int column = 0; column < board.size().columns(); column++) {
                if (!board.isEmpty(new Position(row, column))) {
                    count++;
                }
            }
        }
        return count;
    }

    private static void assertPiece(Board board, int row, int column, PieceType type, PieceColor color) {
        Piece piece = board.pieceAt(new Position(row, column));
        assertEquals(type, piece.type(), "tipo en " + row + "," + column);
        assertEquals(color, piece.color(), "color en " + row + "," + column);
    }

    @Test
    @DisplayName("El tablero inicial es de 8x8")
    void elTableroInicialEsDeOchoPorOcho() {
        Board board = setup.createInitialBoard();

        assertEquals(new BoardSize(8, 8), board.size());
    }

    @Test
    @DisplayName("El tablero inicial tiene 32 piezas")
    void elTableroInicialTiene32Piezas() {
        Board board = setup.createInitialBoard();

        assertEquals(32, countPieces(board));
    }

    @Test
    @DisplayName("Las piezas blancas arrancan en las filas 0 y 1, cada una en su casilla")
    void lasPiezasBlancasArrancanEnSuCasilla() {
        Board board = setup.createInitialBoard();

        for (int column = 0; column < 8; column++) {
            assertPiece(board, 0, column, BACK_RANK.get(column), PieceColor.WHITE);
            assertPiece(board, 1, column, PieceType.PAWN, PieceColor.WHITE);
        }
    }

    @Test
    @DisplayName("Las piezas negras arrancan en las filas 7 y 6, enfrentadas a las blancas")
    void lasPiezasNegrasArrancanEnSuCasilla() {
        Board board = setup.createInitialBoard();

        for (int column = 0; column < 8; column++) {
            assertPiece(board, 7, column, BACK_RANK.get(column), PieceColor.BLACK);
            assertPiece(board, 6, column, PieceType.PAWN, PieceColor.BLACK);
        }
    }

    @Test
    @DisplayName("Las filas del medio arrancan vacías")
    void lasFilasDelMedioArrancanVacias() {
        Board board = setup.createInitialBoard();

        for (int row = 2; row <= 5; row++) {
            for (int column = 0; column < 8; column++) {
                assertTrue(board.isEmpty(new Position(row, column)));
            }
        }
    }

    @Test
    @DisplayName("En la posición inicial los peones pueden avanzar una o dos casillas")
    void enLaPosicionInicialLosPeonesPuedenAvanzarUnaODos() {
        Board board = setup.createInitialBoard();

        List<Position> white = board.pieceAt(new Position(1, 4)).reachableSquares(board, new Position(1, 4));
        List<Position> black = board.pieceAt(new Position(6, 4)).reachableSquares(board, new Position(6, 4));

        assertEquals(List.of(new Position(2, 4), new Position(3, 4)), white);
        assertEquals(List.of(new Position(5, 4), new Position(4, 4)), black);
    }

    @Test
    @DisplayName("En la posición inicial el caballo salta por encima de los peones")
    void enLaPosicionInicialElCaballoSaltaPorEncimaDeLosPeones() {
        Board board = setup.createInitialBoard();
        Position knight = new Position(0, 1);

        List<Position> reachable = board.pieceAt(knight).reachableSquares(board, knight);

        assertTrue(reachable.containsAll(List.of(new Position(2, 0), new Position(2, 2))));
    }

    @Test
    @DisplayName("Cada llamada arma un tablero nuevo e independiente")
    void cadaLlamadaArmaUnTableroNuevoEIndependiente() {
        Board first = setup.createInitialBoard();
        Board second = setup.createInitialBoard();

        first.removePiece(new Position(1, 4));

        assertNotSame(first, second);
        assertEquals(32, countPieces(second));
    }
}

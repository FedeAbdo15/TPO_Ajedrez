package ajedrez.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoveValidatorTest {

    // El orden importa: cada regla asume que las anteriores pasaron.
    private static List<MoveRule> standardRules() {
        return List.of(new SourceHasPieceRule(), new TurnOwnershipRule(),
                new PieceMovementRule(), new NoFriendlyCaptureRule());
    }

    private final MoveValidator validator = new MoveValidator(standardRules());
    private final Board board = new StandardChessSetup(new StandardPieceFactory()).createInitialBoard();

    @Test
    @DisplayName("Acepta un movimiento legal desde la posición inicial")
    void aceptaUnMovimientoLegal() {
        ValidationResult result = validator.validate(board,
                new Move(new Position(1, 4), new Position(3, 4)), PieceColor.WHITE);

        assertTrue(result.valid());
        assertEquals("", result.reason());
    }

    @Test
    @DisplayName("Rechaza un movimiento con origen fuera del tablero")
    void rechazaUnMovimientoConOrigenFueraDelTablero() {
        ValidationResult result = validator.validate(board,
                new Move(new Position(8, 0), new Position(5, 0)), PieceColor.WHITE);

        assertFalse(result.valid());
        assertEquals(new SourceHasPieceRule().reason(), result.reason());
    }

    @Test
    @DisplayName("Rechaza un movimiento con destino fuera del tablero")
    void rechazaUnMovimientoConDestinoFueraDelTablero() {
        ValidationResult result = validator.validate(board,
                new Move(new Position(0, 1), new Position(-2, 0)), PieceColor.WHITE);

        assertFalse(result.valid());
        assertEquals(new PieceMovementRule().reason(), result.reason());
    }

    @Test
    @DisplayName("Informa el motivo de la primera regla que no se cumple")
    void informaElMotivoDeLaPrimeraReglaQueNoSeCumple() {
        ValidationResult result = validator.validate(board,
                new Move(new Position(6, 4), new Position(4, 4)), PieceColor.WHITE);

        assertFalse(result.valid());
        assertEquals(new TurnOwnershipRule().reason(), result.reason());
    }

    @Test
    @DisplayName("Rechaza capturar una pieza propia")
    void rechazaCapturarUnaPiezaPropia() {
        Board custom = new Board(new BoardSize(8, 8));
        PieceFactory factory = new StandardPieceFactory();
        custom.placePiece(factory.create(PieceType.ROOK, PieceColor.WHITE), new Position(0, 0));
        custom.placePiece(factory.create(PieceType.KNIGHT, PieceColor.WHITE), new Position(0, 3));

        ValidationResult result = validator.validate(custom,
                new Move(new Position(0, 0), new Position(0, 3)), PieceColor.WHITE);

        assertFalse(result.valid());
        assertEquals(new NoFriendlyCaptureRule().reason(), result.reason());
    }

    @Test
    @DisplayName("Una regla nueva se suma a la lista sin modificar el validador")
    void unaReglaNuevaSeSumaALaListaSinModificarElValidador() {
        MoveRule noKnightMoves = new MoveRule() {
            @Override
            public boolean isSatisfiedBy(Board board, Move move, PieceColor turn) {
                return board.pieceAt(move.from()).type() != PieceType.KNIGHT;
            }

            @Override
            public String reason() {
                return "En esta variante los caballos no se mueven";
            }
        };
        List<MoveRule> rules = new ArrayList<>(standardRules());
        rules.add(noKnightMoves);
        MoveValidator extended = new MoveValidator(rules);

        ValidationResult result = extended.validate(board,
                new Move(new Position(0, 1), new Position(2, 2)), PieceColor.WHITE);

        assertFalse(result.valid());
        assertEquals("En esta variante los caballos no se mueven", result.reason());
    }

    @Test
    @DisplayName("Un validador sin reglas es rechazado al construirse")
    void unValidadorSinReglasEsRechazado() {
        assertThrows(IllegalArgumentException.class, () -> new MoveValidator(List.of()));
    }
}

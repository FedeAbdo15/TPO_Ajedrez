package ajedrez.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Prueba de la arquitectura: piezas inventadas armadas solo por composición, sin tocar ninguna clase del núcleo.
// Todo lo nuevo vive en este archivo: dos tipos, dos composiciones de estrategias existentes, una fábrica
// y un armado de tablero de la variante.
class PieceByCompositionTest {

    // Canciller = caballo + torre.
    private static final PieceType CHANCELLOR = new PieceType("CHANCELLOR");
    private static final MovementStrategy CHANCELLOR_MOVEMENT = new CompositeMovement(List.of(
            new JumpMovement(Directions.KNIGHT_OFFSETS),
            new SlidingMovement(Directions.ORTHOGONAL)));

    // Arzobispo = caballo + alfil.
    private static final PieceType ARCHBISHOP = new PieceType("ARCHBISHOP");
    private static final MovementStrategy ARCHBISHOP_MOVEMENT = new CompositeMovement(List.of(
            new JumpMovement(Directions.KNIGHT_OFFSETS),
            new SlidingMovement(Directions.DIAGONAL)));

    // La "entrada en la fábrica": una fábrica de la variante que conoce las piezas nuevas y delega
    // el resto en la estándar. PieceFactory tiene un solo método, así que alcanza con una lambda.
    private static final PieceFactory STANDARD = new StandardPieceFactory();
    private static final PieceFactory VARIANT = (type, color) -> {
        if (type.equals(CHANCELLOR)) {
            return new Piece(type, color, CHANCELLOR_MOVEMENT);
        }
        if (type.equals(ARCHBISHOP)) {
            return new Piece(type, color, ARCHBISHOP_MOVEMENT);
        }
        return STANDARD.create(type, color);
    };

    // Ajedrez Capablanca: tablero de 8 filas y 10 columnas, con canciller y arzobispo en la fila de atrás.
    private static final List<PieceType> CAPABLANCA_BACK_RANK = List.of(
            PieceType.ROOK, PieceType.KNIGHT, ARCHBISHOP, PieceType.BISHOP, PieceType.QUEEN,
            PieceType.KING, PieceType.BISHOP, CHANCELLOR, PieceType.KNIGHT, PieceType.ROOK);

    private static final BoardSetup CAPABLANCA_SETUP = () -> {
        Board board = new Board(new BoardSize(8, CAPABLANCA_BACK_RANK.size()));
        for (int column = 0; column < CAPABLANCA_BACK_RANK.size(); column++) {
            PieceType type = CAPABLANCA_BACK_RANK.get(column);
            board.placePiece(VARIANT.create(type, PieceColor.WHITE), new Position(0, column));
            board.placePiece(VARIANT.create(PieceType.PAWN, PieceColor.WHITE), new Position(1, column));
            board.placePiece(VARIANT.create(PieceType.PAWN, PieceColor.BLACK), new Position(6, column));
            board.placePiece(VARIANT.create(type, PieceColor.BLACK), new Position(7, column));
        }
        return board;
    };

    private static Game newGame(Board board) {
        CheckDetector detector = new AttackBasedCheckDetector();
        MoveValidator validator = new MoveValidator(List.of(
                new SourceHasPieceRule(), new TurnOwnershipRule(), new PieceMovementRule(),
                new NoFriendlyCaptureRule(), new KingSafetyRule(detector)));
        return new Game(board, validator, detector, new MoveHistory());
    }

    private static Position at(int row, int column) {
        return new Position(row, column);
    }

    @Test
    @DisplayName("El canciller se mueve como torre y como caballo")
    void elCancillerSeMueveComoTorreYComoCaballo() {
        Board board = new Board(new BoardSize(8, 8));
        Piece chancellor = VARIANT.create(CHANCELLOR, PieceColor.WHITE);

        List<Position> reachable = chancellor.reachableSquares(board, at(3, 3));

        assertEquals(14 + 8, reachable.size());
        assertTrue(reachable.contains(at(3, 7)));
        assertTrue(reachable.contains(at(5, 4)));
        assertFalse(reachable.contains(at(4, 4)));
    }

    @Test
    @DisplayName("El canciller queda bloqueado como torre pero salta como caballo")
    void elCancillerQuedaBloqueadoComoTorrePeroSaltaComoCaballo() {
        Board board = new Board(new BoardSize(8, 8));
        for (Position neighbour : List.of(at(2, 3), at(4, 3), at(3, 2), at(3, 4))) {
            board.placePiece(VARIANT.create(PieceType.PAWN, PieceColor.WHITE), neighbour);
        }

        List<Position> reachable = VARIANT.create(CHANCELLOR, PieceColor.WHITE).reachableSquares(board, at(3, 3));

        assertFalse(reachable.contains(at(5, 3)));
        assertTrue(reachable.contains(at(5, 4)));
    }

    @Test
    @DisplayName("El canciller da jaque sin que el detector sepa que existe")
    void elCancillerDaJaqueSinQueElDetectorSepaQueExiste() {
        Board board = new Board(new BoardSize(8, 8));
        board.placePiece(VARIANT.create(PieceType.KING, PieceColor.BLACK), at(7, 4));
        board.placePiece(VARIANT.create(CHANCELLOR, PieceColor.WHITE), at(5, 5));

        assertTrue(new AttackBasedCheckDetector().isInCheck(board, PieceColor.BLACK));
    }

    @Test
    @DisplayName("El arzobispo se mueve como alfil y como caballo, pero no en línea recta")
    void elArzobispoSeMueveComoAlfilYComoCaballo() {
        Board board = new Board(new BoardSize(8, 8));

        List<Position> reachable = VARIANT.create(ARCHBISHOP, PieceColor.WHITE).reachableSquares(board, at(3, 3));

        assertEquals(13 + 8, reachable.size());
        assertTrue(reachable.contains(at(7, 7)));
        assertFalse(reachable.contains(at(3, 7)));
    }

    @Test
    @DisplayName("El tablero Capablanca es de 8x10 y arranca con 40 piezas sin jaque")
    void elTableroCapablancaArrancaCon40Piezas() {
        Board board = CAPABLANCA_SETUP.createInitialBoard();

        assertEquals(new BoardSize(8, 10), board.size());
        assertEquals(20, board.positionsOf(PieceColor.WHITE).size());
        assertEquals(20, board.positionsOf(PieceColor.BLACK).size());
        assertEquals(CHANCELLOR, board.pieceAt(at(0, 7)).type());
        assertEquals(GameStatus.IN_PROGRESS, newGame(board).status());
    }

    @Test
    @DisplayName("En una partida Capablanca las piezas nuevas juegan, capturan y se deshacen como cualquier otra")
    void enUnaPartidaCapablancaLasPiezasNuevasJueganComoCualquierOtra() {
        Board board = CAPABLANCA_SETUP.createInitialBoard();
        Board initial = board.copy();
        Game game = newGame(board);

        MoveResult chancellorJumps = game.move(at(0, 7), at(2, 6));
        MoveResult archbishopJumps = game.move(at(7, 2), at(5, 3));
        MoveResult chancellorSlides = game.move(at(2, 6), at(2, 0));
        MoveResult archbishopCaptures = game.move(at(5, 3), at(1, 7));

        assertTrue(chancellorJumps.applied());
        assertTrue(archbishopJumps.applied());
        assertTrue(chancellorSlides.applied());
        assertTrue(archbishopCaptures.applied());
        assertEquals(ARCHBISHOP, board.pieceAt(at(1, 7)).type());

        game.undo();
        game.undo();
        game.undo();
        game.undo();

        assertEquals(initial, board);
        assertEquals(PieceColor.WHITE, game.currentTurn());
    }

    @Test
    @DisplayName("Las reglas existentes rechazan un movimiento imposible para el canciller")
    void lasReglasExistentesRechazanUnMovimientoImposibleParaElCanciller() {
        Game game = newGame(CAPABLANCA_SETUP.createInitialBoard());
        game.move(at(1, 8), at(3, 8));
        game.move(at(6, 0), at(5, 0));

        MoveResult diagonal = game.move(at(0, 7), at(1, 8));

        assertFalse(diagonal.applied());
        assertEquals(new PieceMovementRule().reason(), diagonal.reason());
    }
}

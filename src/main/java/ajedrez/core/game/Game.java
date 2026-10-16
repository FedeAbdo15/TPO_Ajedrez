package ajedrez.core.game;

import ajedrez.core.domain.Board;
import ajedrez.core.domain.Move;
import ajedrez.core.domain.PieceColor;
import ajedrez.core.domain.Position;
import ajedrez.core.rules.CheckDetector;
import ajedrez.core.rules.LegalMoveChecker;
import ajedrez.core.rules.MoveValidator;
import ajedrez.core.rules.ValidationResult;

public class Game {

    private final Board board;
    private final MoveValidator validator;
    private final CheckDetector detector;
    private final LegalMoveChecker legalMoveChecker;
    private final MoveHistory history;
    private PieceColor turn;

    // Empiezan las blancas.
    public Game(Board board, MoveValidator validator, CheckDetector detector,
                LegalMoveChecker legalMoveChecker, MoveHistory history) {
        this.board = board;
        this.validator = validator;
        this.detector = detector;
        this.legalMoveChecker = legalMoveChecker;
        this.history = history;
        this.turn = PieceColor.WHITE;
    }

    // Un movimiento rechazado no cambia ni el tablero ni el turno.
    // Si la partida ya terminó (jaque mate o ahogado), no se aceptan más jugadas.
    public MoveResult move(Position from, Position to) {
        GameStatus current = status();
        if (current == GameStatus.CHECKMATE || current == GameStatus.STALEMATE) {
            return new MoveResult(false, "La partida ya terminó: " + current, current);
        }
        Move move = new Move(from, to);
        ValidationResult validation = validator.validate(board, move, turn);
        if (!validation.valid()) {
            return new MoveResult(false, validation.reason(), current);
        }
        Command command = new MoveCommand(board, move);
        command.execute();
        history.push(command);
        turn = turn.opposite();
        return new MoveResult(true, "", status());
    }

    public void undo() {
        history.undoLast();
        turn = turn.opposite();
    }

    public PieceColor currentTurn() {
        return turn;
    }

    // Se calcula en cada consulta en vez de guardarse: así undo no tiene que restaurarlo.
    // Orden: primero se comprueba si hay movimientos legales (la parte costosa) solo
    // cuando se sabe que no queda ninguno, para no pagar el costo en el caso normal.
    public GameStatus status() {
        boolean inCheck = detector.isInCheck(board, turn);
        boolean hasLegal = legalMoveChecker.hasAnyLegalMove(board, turn, validator);
        if (!hasLegal) {
            return inCheck ? GameStatus.CHECKMATE : GameStatus.STALEMATE;
        }
        return inCheck ? GameStatus.CHECK : GameStatus.IN_PROGRESS;
    }
}

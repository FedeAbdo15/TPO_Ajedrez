package ajedrez.core;

public class Game {

    private final Board board;
    private final MoveValidator validator;
    private final CheckDetector detector;
    private final MoveHistory history;
    private PieceColor turn;

    // Empiezan las blancas.
    public Game(Board board, MoveValidator validator, CheckDetector detector, MoveHistory history) {
        this.board = board;
        this.validator = validator;
        this.detector = detector;
        this.history = history;
        this.turn = PieceColor.WHITE;
    }

    // Un movimiento rechazado no cambia ni el tablero ni el turno.
    public MoveResult move(Position from, Position to) {
        Move move = new Move(from, to);
        ValidationResult validation = validator.validate(board, move, turn);
        if (!validation.valid()) {
            return new MoveResult(false, validation.reason(), status());
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
    public GameStatus status() {
        return detector.isInCheck(board, turn) ? GameStatus.CHECK : GameStatus.IN_PROGRESS;
    }
}

package ajedrez.core.game;

import ajedrez.core.domain.Board;
import ajedrez.core.domain.Move;
import ajedrez.core.domain.Piece;

public class MoveCommand implements Command {

    private final Board board;
    private final Move move;
    private Piece capturedPiece;
    private boolean executed;

    public MoveCommand(Board board, Move move) {
        this.board = board;
        this.move = move;
    }

    // Guarda lo que hay en el destino antes de mover: movePiece lo pisa, y undo lo necesita para restaurarlo.
    @Override
    public void execute() {
        if (executed) {
            throw new IllegalStateException("El movimiento ya fue ejecutado");
        }
        capturedPiece = board.pieceAt(move.to());
        board.movePiece(move.from(), move.to());
        executed = true;
    }

    @Override
    public void undo() {
        if (!executed) {
            throw new IllegalStateException("No se puede deshacer un movimiento que no se ejecutó");
        }
        board.movePiece(move.to(), move.from());
        if (capturedPiece != null) {
            board.placePiece(capturedPiece, move.to());
        }
        capturedPiece = null;
        executed = false;
    }
}

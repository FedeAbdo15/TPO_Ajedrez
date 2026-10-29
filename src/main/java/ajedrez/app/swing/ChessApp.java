package ajedrez.app.swing;

import ajedrez.core.domain.Board;
import ajedrez.core.game.Game;
import ajedrez.core.game.MoveHistory;
import ajedrez.core.rules.AttackBasedCheckDetector;
import ajedrez.core.rules.CheckDetector;
import ajedrez.core.rules.KingSafetyRule;
import ajedrez.core.rules.LegalMoveChecker;
import ajedrez.core.rules.MoveValidator;
import ajedrez.core.rules.NoFriendlyCaptureRule;
import ajedrez.core.rules.PieceMovementRule;
import ajedrez.core.rules.SourceHasPieceRule;
import ajedrez.core.rules.TurnOwnershipRule;
import ajedrez.core.setup.PieceFactory;
import ajedrez.core.setup.StandardChessSetup;
import ajedrez.core.setup.StandardPieceFactory;

import javax.swing.*;
import java.util.List;

public class ChessApp {

    public static void main(String[] args) {
        PieceFactory factory = new StandardPieceFactory();
        Board board = new StandardChessSetup(factory).createInitialBoard();

        CheckDetector detector = new AttackBasedCheckDetector();
        MoveValidator validator = new MoveValidator(List.of(
                new SourceHasPieceRule(),
                new TurnOwnershipRule(),
                new PieceMovementRule(),
                new NoFriendlyCaptureRule(),
                new KingSafetyRule(detector)));
        Game game = new Game(board, validator, detector,
                new LegalMoveChecker(), new MoveHistory());

        SwingUtilities.invokeLater(() -> {
            ChessFrame frame = new ChessFrame(game, board);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

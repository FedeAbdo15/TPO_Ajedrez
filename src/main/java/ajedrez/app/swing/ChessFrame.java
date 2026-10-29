package ajedrez.app.swing;

import ajedrez.core.domain.Board;
import ajedrez.core.game.Game;
import ajedrez.core.game.MoveResult;

import javax.swing.*;
import java.awt.*;

public class ChessFrame extends JFrame {

    public ChessFrame(Game game, Board board) {
        super("Ajedrez");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        DefaultListModel<String> historyModel = new DefaultListModel<>();
        PieceImageLoader imageLoader = new PieceImageLoader();

        BoardPanel boardPanel = new BoardPanel(game, board, imageLoader);
        SidePanel sidePanel = new SidePanel(game, boardPanel, historyModel);

        boardPanel.setMoveHandler((from, to) -> {
            MoveResult result = game.move(from, to);
            if (result.applied()) {
                sidePanel.recordMove(from, to);
                sidePanel.refreshStatus();
            }
            return result.applied();
        });

        add(boardPanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);
    }
}

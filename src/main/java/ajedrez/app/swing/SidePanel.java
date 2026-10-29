package ajedrez.app.swing;

import ajedrez.core.domain.PieceColor;
import ajedrez.core.domain.Position;
import ajedrez.core.game.Game;
import ajedrez.core.game.GameStatus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SidePanel extends JPanel {

    private final Game game;
    private final BoardPanel boardPanel;
    private final DefaultListModel<String> historyModel;

    private final JLabel turnLabel = new JLabel();
    private final JLabel statusLabel = new JLabel();
    private final JList<String> historyList = new JList<>();
    private final JButton undoButton = new JButton("Deshacer");
    private final JCheckBox flipCheck = new JCheckBox("Voltear tablero tras cada jugada", false);

    public SidePanel(Game game, BoardPanel boardPanel, DefaultListModel<String> historyModel) {
        this.game = game;
        this.boardPanel = boardPanel;
        this.historyModel = historyModel;
        historyList.setModel(historyModel);
        historyList.setVisibleRowCount(18);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(280, 0));

        turnLabel.setFont(turnLabel.getFont().deriveFont(Font.BOLD, 16f));
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 15f));

        add(turnLabel);
        add(Box.createRigidArea(new Dimension(0, 6)));
        add(statusLabel);
        add(Box.createRigidArea(new Dimension(0, 14)));
        add(new JLabel("Historial:"));
        add(Box.createRigidArea(new Dimension(0, 4)));
        add(new JScrollPane(historyList));
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(undoButton);
        add(Box.createRigidArea(new Dimension(0, 8)));
        add(flipCheck);

        undoButton.addActionListener(e -> onUndo());
        flipCheck.addActionListener(e -> boardPanel.setAutoFlip(flipCheck.isSelected()));

        refreshStatus();
    }

    public void recordMove(Position from, Position to) {
        historyModel.addElement(moveNotation(from) + " \u2192 " + moveNotation(to));
        historyList.ensureIndexIsVisible(historyModel.getSize() - 1);
    }

    public void refreshStatus() {
        turnLabel.setText("Turno: " + (game.currentTurn() == PieceColor.WHITE ? "Blancas" : "Negras"));
        GameStatus s = game.status();
        switch (s) {
            case CHECK -> {
                statusLabel.setText("¡JAQUE!");
                statusLabel.setForeground(Color.ORANGE.darker());
            }
            case CHECKMATE -> {
                statusLabel.setText("JAQUE MATE");
                statusLabel.setForeground(Color.RED);
                boardPanel.setEnabled(false);
            }
            case STALEMATE -> {
                statusLabel.setText("AHOGADO (TABLAS)");
                statusLabel.setForeground(Color.GRAY);
                boardPanel.setEnabled(false);
            }
            default -> statusLabel.setText("");
        }
        repaint();
    }

    private void onUndo() {
        if (game.status() == GameStatus.CHECKMATE || game.status() == GameStatus.STALEMATE) {
            boardPanel.setEnabled(true);
        }
        try {
            game.undo();
        } catch (IllegalStateException ex) {
            return;
        }
        if (!historyModel.isEmpty()) {
            historyModel.remove(historyModel.getSize() - 1);
        }
        if (boardPanel.isAutoFlip()) {
            boardPanel.toggleFlipped();
        }
        boardPanel.repaint();
        refreshStatus();
    }

    private static String moveNotation(Position p) {
        return "" + (char) ('a' + p.column()) + (8 - p.row());
    }
}

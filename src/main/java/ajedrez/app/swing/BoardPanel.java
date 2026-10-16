package ajedrez.app.swing;

import ajedrez.core.domain.Board;
import ajedrez.core.domain.BoardSize;
import ajedrez.core.domain.Piece;
import ajedrez.core.domain.PieceColor;
import ajedrez.core.domain.Position;
import ajedrez.core.game.Game;
import ajedrez.core.game.GameStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.function.BiFunction;

public class BoardPanel extends JPanel {

    private static final Color LIGHT_SQUARE = new Color(232, 235, 212);
    private static final Color DARK_SQUARE = new Color(118, 150, 86);
    private static final Color SELECTED = new Color(246, 246, 105, 190);
    private static final Color LEGAL_TARGET = new Color(90, 140, 250, 130);

    private final Game game;
    private final Board board;
    private final PieceImageLoader imageLoader;

    private boolean flipped = false;
    private boolean autoFlip = false;

    private Position selectedFrom = null;
    private List<Position> legalTargets = List.of();
    private final DragState drag = new DragState();

    private BiFunction<Position, Position, Boolean> onMoveAttempt = (from, to) -> false;

    public BoardPanel(Game game, Board board, PieceImageLoader imageLoader) {
        this.game = game;
        this.board = board;
        this.imageLoader = imageLoader;
        setPreferredSize(new Dimension(640, 640));
        setBackground(Color.WHITE);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                onMousePressed(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                onMouseReleased(e.getX(), e.getY());
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                drag.cursorX = e.getX();
                drag.cursorY = e.getY();
                repaint();
            }
        });
    }

    public void setMoveHandler(BiFunction<Position, Position, Boolean> handler) {
        this.onMoveAttempt = handler;
    }

    public void setAutoFlip(boolean autoFlip) {
        this.autoFlip = autoFlip;
    }

    public boolean isAutoFlip() {
        return autoFlip;
    }

    public void toggleFlipped() {
        flipped = !flipped;
        repaint();
    }

    public boolean isFlipped() {
        return flipped;
    }

    private void onMousePressed(int x, int y) {
        if (gameOver()) {
            return;
        }
        BoardSize size = board.size();
        int[] metrics = cellMetrics(size);
        int cellSize = metrics[0];
        int originX = metrics[1];
        int originY = metrics[2];
        Position pos = pixelToPosition(x, y, size.rows(), size.columns(), cellSize, originX, originY);
        if (pos == null) {
            clearSelection();
            return;
        }
        Piece piece = board.pieceAt(pos);
        if (piece != null && piece.color() == game.currentTurn()) {
            selectedFrom = pos;
            legalTargets = piece.reachableSquares(board, pos);
            drag.active = true;
            drag.image = imageLoader.load(piece.type(), piece.color());
            drag.cursorX = x;
            drag.cursorY = y;
        } else {
            clearSelection();
        }
        repaint();
    }

    private void onMouseReleased(int x, int y) {
        if (!drag.active) {
            clearSelection();
            repaint();
            return;
        }
        BoardSize size = board.size();
        int[] metrics = cellMetrics(size);
        int cellSize = metrics[0];
        int originX = metrics[1];
        int originY = metrics[2];
        Position to = pixelToPosition(x, y, size.rows(), size.columns(), cellSize, originX, originY);
        drag.active = false;
        drag.image = null;
        if (selectedFrom != null && to != null && !to.equals(selectedFrom)) {
            boolean applied = onMoveAttempt.apply(selectedFrom, to);
            if (applied && autoFlip) {
                flipped = !flipped;
            }
        }
        selectedFrom = null;
        legalTargets = List.of();
        repaint();
    }

    private void clearSelection() {
        selectedFrom = null;
        legalTargets = List.of();
        drag.active = false;
        drag.image = null;
    }

    private boolean gameOver() {
        GameStatus s = game.status();
        return s == GameStatus.CHECKMATE || s == GameStatus.STALEMATE;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        BoardSize size = board.size();
        int rows = size.rows();
        int cols = size.columns();
        int[] metrics = cellMetrics(size);
        int cellSize = metrics[0];
        int originX = metrics[1];
        int originY = metrics[2];

        for (int vr = 0; vr < rows; vr++) {
            for (int vc = 0; vc < cols; vc++) {
                boolean light = ((vr + vc) & 1) == 0;
                g2.setColor(light ? LIGHT_SQUARE : DARK_SQUARE);
                g2.fillRect(originX + vc * cellSize, originY + vr * cellSize, cellSize, cellSize);

                Position pos = boardPosition(vr, vc, rows, cols);

                if (selectedFrom != null && pos.equals(selectedFrom)) {
                    g2.setColor(SELECTED);
                    g2.fillRect(originX + vc * cellSize, originY + vr * cellSize, cellSize, cellSize);
                }

                if (legalTargets.contains(pos)) {
                    g2.setColor(LEGAL_TARGET);
                    int cx = originX + vc * cellSize + cellSize / 2;
                    int cy = originY + vr * cellSize + cellSize / 2;
                    int radius = Math.max(4, cellSize / 6);
                    g2.fillOval(cx - radius, cy - radius, 2 * radius, 2 * radius);
                }

                Piece piece = board.pieceAt(pos);
                if (piece != null && !(drag.active && selectedFrom != null && pos.equals(selectedFrom))) {
                    paintPiece(g2, piece, cellSize, originX, originY, vr, vc);
                }
            }
        }

        if (drag.active && drag.image != null) {
            g2.drawImage(drag.image,
                    drag.cursorX - drag.image.getWidth() / 2,
                    drag.cursorY - drag.image.getHeight() / 2,
                    null);
        }
    }

    private void paintPiece(Graphics2D g2, Piece piece, int cellSize, int originX, int originY,
                            int vr, int vc) {
        BufferedImage img = imageLoader.load(piece.type(), piece.color());
        int ix = originX + vc * cellSize + (cellSize - img.getWidth()) / 2;
        int iy = originY + vr * cellSize + (cellSize - img.getHeight()) / 2;
        g2.drawImage(img, ix, iy, null);
    }

    private int[] cellMetrics(BoardSize size) {
        int rows = size.rows();
        int cols = size.columns();
        int cellSize = Math.min(getWidth() / cols, getHeight() / rows);
        if (cellSize <= 0) {
            cellSize = 1;
        }
        int originX = (getWidth() - cellSize * cols) / 2;
        int originY = (getHeight() - cellSize * rows) / 2;
        return new int[]{cellSize, originX, originY};
    }

    private Position boardPosition(int vr, int vc, int rows, int cols) {
        int row;
        int col;
        if (flipped) {
            row = vr;
            col = cols - 1 - vc;
        } else {
            row = rows - 1 - vr;
            col = vc;
        }
        return new Position(row, col);
    }

    private Position pixelToPosition(int x, int y, int rows, int cols, int cellSize,
                                     int originX, int originY) {
        int vr = (y - originY) / cellSize;
        int vc = (x - originX) / cellSize;
        if (vr < 0 || vr >= rows || vc < 0 || vc >= cols) {
            return null;
        }
        return boardPosition(vr, vc, rows, cols);
    }
}

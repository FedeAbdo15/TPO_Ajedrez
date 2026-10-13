package ajedrez.core;

public interface CheckDetector {

    boolean isInCheck(Board board, PieceColor color);
}

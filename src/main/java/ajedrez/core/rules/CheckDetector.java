package ajedrez.core.rules;

import ajedrez.core.domain.Board;
import ajedrez.core.domain.PieceColor;

public interface CheckDetector {

    boolean isInCheck(Board board, PieceColor color);
}

package ajedrez.core.setup;

import ajedrez.core.domain.Piece;
import ajedrez.core.domain.PieceColor;
import ajedrez.core.domain.PieceType;

public interface PieceFactory {

    Piece create(PieceType type, PieceColor color);
}

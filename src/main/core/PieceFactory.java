package ajedrez.core;

public interface PieceFactory {

    Piece create(PieceType type, PieceColor color);
}

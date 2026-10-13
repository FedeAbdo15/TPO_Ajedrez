package ajedrez.core;

public enum PieceColor {
    WHITE,
    BLACK;

    public PieceColor opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}

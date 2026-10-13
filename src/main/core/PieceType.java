package ajedrez.core;

// Record y no enum: un enum es un conjunto cerrado, y sumar una pieza nueva obligaría a modificarlo.
// Así, una variante crea su propio tipo (new PieceType("CHANCELLOR")) sin tocar esta clase.
public record PieceType(String name) {

    public static final PieceType PAWN = new PieceType("PAWN");
    public static final PieceType ROOK = new PieceType("ROOK");
    public static final PieceType KNIGHT = new PieceType("KNIGHT");
    public static final PieceType BISHOP = new PieceType("BISHOP");
    public static final PieceType QUEEN = new PieceType("QUEEN");
    public static final PieceType KING = new PieceType("KING");

    public PieceType {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El tipo de pieza necesita un nombre");
        }
    }

    @Override
    public String toString() {
        return name;
    }
}

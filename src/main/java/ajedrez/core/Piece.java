package ajedrez.core;

import java.util.Objects;

public class Piece {

    private final PieceType type;
    private final PieceColor color;

    public Piece(PieceType type, PieceColor color) {
        this.type = type;
        this.color = color;
    }

    public PieceType type() {
        return type;
    }

    public PieceColor color() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Piece other)) return false;
        return type == other.type && color == other.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }

    @Override
    public String toString() {
        return color + " " + type;
    }
}

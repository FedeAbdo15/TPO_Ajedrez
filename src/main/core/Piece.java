package ajedrez.core;

import java.util.List;
import java.util.Objects;

public class Piece {

    private final PieceType type;
    private final PieceColor color;
    private final MovementStrategy movement;

    public Piece(PieceType type, PieceColor color, MovementStrategy movement) {
        this.type = type;
        this.color = color;
        this.movement = movement;
    }

    public PieceType type() {
        return type;
    }

    public PieceColor color() {
        return color;
    }

    public List<Position> reachableSquares(Board board, Position from) {
        return movement.reachableSquares(board, from, color);
    }

    public List<Position> attackedSquares(Board board, Position from) {
        return movement.attackedSquares(board, from, color);
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

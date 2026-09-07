package ajedrez.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Board {

    private final Map<Position, Piece> squares;
    private final BoardSize size;

    public Board(BoardSize size) {
        this.size = size;
        this.squares = new HashMap<>();
    }

    public Piece pieceAt(Position position) {
        validatePosition(position);
        return squares.get(position);
    }

    public boolean isEmpty(Position position) {
        validatePosition(position);
        return squares.get(position) == null;
    }

    public void placePiece(Piece piece, Position position) {
        validatePosition(position);
        squares.put(position, piece);
    }

    public Piece removePiece(Position position) {
        validatePosition(position);
        return squares.remove(position);
    }

    public void movePiece(Position from, Position to) {
        validatePosition(from);
        validatePosition(to);
        Piece piece = squares.remove(from);
        if (piece == null) {
            throw new IllegalStateException("No hay pieza en la posición de origen: " + from);
        }
        squares.put(to, piece);
    }

    public BoardSize size() {
        return size;
    }

    public Board copy() {
        Board copy = new Board(size);
        copy.squares.putAll(this.squares);
        return copy;
    }

    private void validatePosition(Position position) {
        if (!size.contains(position)) {
            throw new IllegalArgumentException("Posición fuera del tablero: " + position);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Board other)) return false;
        return size.equals(other.size) && squares.equals(other.squares);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, squares);
    }
}

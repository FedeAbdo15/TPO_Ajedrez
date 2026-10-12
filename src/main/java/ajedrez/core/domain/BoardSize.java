package ajedrez.core.domain;

public record BoardSize(int rows, int columns) {

    public boolean contains(Position position) {
        return position.row() >= 0 && position.row() < rows
                && position.column() >= 0 && position.column() < columns;
    }
}

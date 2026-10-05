package ajedrez.core;

public record Position(int row, int column) {

    public Position plus(Direction direction) {
        return new Position(row + direction.rowDelta(), column + direction.columnDelta());
    }
}

package ajedrez.core;

import java.util.List;

public final class Directions {

    public static final List<Direction> ORTHOGONAL = List.of(
            new Direction(1, 0), new Direction(-1, 0),
            new Direction(0, 1), new Direction(0, -1));

    public static final List<Direction> DIAGONAL = List.of(
            new Direction(1, 1), new Direction(1, -1),
            new Direction(-1, 1), new Direction(-1, -1));

    public static final List<Direction> KNIGHT_OFFSETS = List.of(
            new Direction(2, 1), new Direction(2, -1),
            new Direction(-2, 1), new Direction(-2, -1),
            new Direction(1, 2), new Direction(1, -2),
            new Direction(-1, 2), new Direction(-1, -2));

    private Directions() {
    }
}

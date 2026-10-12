package ajedrez.core;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class StandardPieceFactory implements PieceFactory {

    private final Map<PieceType, MovementStrategy> strategiesByType;

    // Las estrategias no tienen estado: una sola instancia por tipo sirve para todas las piezas
    // de ese tipo, de los dos colores.
    public StandardPieceFactory() {
        MovementStrategy rook = new SlidingMovement(Directions.ORTHOGONAL);
        MovementStrategy bishop = new SlidingMovement(Directions.DIAGONAL);
        List<Direction> allDirections = Stream.concat(
                Directions.ORTHOGONAL.stream(), Directions.DIAGONAL.stream()).toList();

        strategiesByType = new EnumMap<>(PieceType.class);
        strategiesByType.put(PieceType.PAWN, new PawnMovement());
        strategiesByType.put(PieceType.ROOK, rook);
        strategiesByType.put(PieceType.KNIGHT, new JumpMovement(Directions.KNIGHT_OFFSETS));
        strategiesByType.put(PieceType.BISHOP, bishop);
        strategiesByType.put(PieceType.QUEEN, new CompositeMovement(List.of(rook, bishop)));
        strategiesByType.put(PieceType.KING, new SlidingMovement(allDirections, 1));
    }

    @Override
    public Piece create(PieceType type, PieceColor color) {
        MovementStrategy movement = strategiesByType.get(type);
        if (movement == null) {
            throw new IllegalArgumentException("No hay estrategia de movimiento para " + type);
        }
        return new Piece(type, color, movement);
    }
}

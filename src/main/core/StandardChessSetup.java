package ajedrez.core;

import java.util.List;

public class StandardChessSetup implements BoardSetup {

    private static final List<PieceType> BACK_RANK = List.of(
            PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
            PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK);

    // El 8x8 es parte de la definición del ajedrez estándar y vive solo acá.
    // Otro tamaño es otra implementación de BoardSetup, no un cambio en esta.
    private static final BoardSize SIZE = new BoardSize(8, BACK_RANK.size());

    private final PieceFactory factory;

    public StandardChessSetup(PieceFactory factory) {
        this.factory = factory;
    }

    // Los peones van en la segunda fila desde cada borde: la misma convención de la que
    // PawnMovement deriva su fila inicial.
    @Override
    public Board createInitialBoard() {
        Board board = new Board(SIZE);
        int lastRow = SIZE.rows() - 1;
        placeArmy(board, PieceColor.WHITE, 0, 1);
        placeArmy(board, PieceColor.BLACK, lastRow, lastRow - 1);
        return board;
    }

    private void placeArmy(Board board, PieceColor color, int backRow, int pawnRow) {
        for (int column = 0; column < SIZE.columns(); column++) {
            board.placePiece(factory.create(BACK_RANK.get(column), color), new Position(backRow, column));
            board.placePiece(factory.create(PieceType.PAWN, color), new Position(pawnRow, column));
        }
    }
}

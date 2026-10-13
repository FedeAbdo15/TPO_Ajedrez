package ajedrez.core;

// status es el estado después del intento, para el jugador que tiene el turno. Si se aplicó, reason queda vacío.
public record MoveResult(boolean applied, String reason, GameStatus status) {
}

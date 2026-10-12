package ajedrez.core;

// Si el movimiento es válido, reason queda vacío.
public record ValidationResult(boolean valid, String reason) {
}

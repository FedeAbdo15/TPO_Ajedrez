# Etapas de implementación — núcleo del TPO

Una etapa por vez. Cada una termina con `mvn test` en verde y un commit.
Todas las clases van en `src/main/java/ajedrez/core/`, todos los tests en `src/test/java/ajedrez/core/`.

## Etapa 0 — Scaffolding

- `pom.xml`: Java 17, JUnit 5.10.2, surefire 3.2.5.
- `.gitignore`: `target/`, `.idea/`, `*.iml`.
- Repo git inicializado.
- Las dos carpetas de código (`src/main/java/ajedrez/core/` y `src/test/java/ajedrez/core/`).
- Un test trivial para verificar que compila y corre; después se borra.

## Etapa 1 — Dominio base

`Position`, `BoardSize`, `PieceColor`, `PieceType`, `Piece` (por ahora solo tipo y color), `Board` (mapa de casillas ocupadas, `copy()`, `equals`/`hashCode` por contenido).

El tablero recibe su tamaño; nada asume 8x8.

Tests: poner y sacar piezas, casilla vacía, mover pieza, copia independiente del original, posición fuera del tablero rechazada.

## Etapa 2 — Movimiento por composición

`MovementStrategy` (interfaz, con `reachableSquares` y `attackedSquares`) y las estrategias:

- `SlidingMovement(direcciones, maxSteps)` → torre, alfil, y rey con `maxSteps = 1`
- `JumpMovement(offsets)` → caballo
- `PawnMovement` → avance simple, doble desde fila inicial, captura diagonal (sin al paso ni promoción)
- `CompositeMovement(lista)` → la reina combina la estrategia del alfil y la de la torre, sin duplicar código ni heredar de ninguna
- `Directions` con las constantes de direcciones y offsets

`Piece` pasa a componer una estrategia.

Acá se resuelve la trampa 1: para casi todas las piezas `attackedSquares` coincide con `reachableSquares`, pero el peón la sobreescribe.

Tests: movimiento válido e inválido de cada una de las 6 piezas, bloqueo por pieza intermedia (torre/alfil/reina), caballo saltando por encima, avance doble del peón negado fuera de la fila inicial, captura recta del peón rechazada, captura diagonal aceptada.

## Etapa 3 — Armado del tablero

`PieceFactory` / `StandardPieceFactory` y `BoardSetup` / `StandardChessSetup`.

Test: el tablero inicial tiene 32 piezas, cada una en su casilla.

## Etapa 4 — Reglas

`MoveRule` (interfaz) con `SourceHasPieceRule`, `TurnOwnershipRule`, `PieceMovementRule`, `NoFriendlyCaptureRule`. `MoveValidator` recibe la lista por constructor y devuelve `ValidationResult(valid, reason)`.

Tests: cada regla rechazando su propio caso, el validador aceptando un movimiento legal, movimiento fuera del tablero rechazado.

## Etapa 5 — Jaque

`CheckDetector` (interfaz) y `AttackBasedCheckDetector`, que usa `attackedSquares` de las piezas rivales. `KingSafetyRule` se suma a la cadena y simula el movimiento sobre una copia del tablero.

Acá se resuelve la trampa 2: el detector no usa el validador, solo las estrategias de movimiento.

Tests: jaque detectado, no-jaque, movimiento que deja al propio rey en jaque rechazado, movimiento que saca al rey del jaque aceptado.

## Etapa 6 — Partida

`Command` / `MoveCommand` (execute/undo restaurando la pieza capturada, con `IllegalStateException` si se deshace sin haber ejecutado), `MoveHistory`, y `Game`: tablero, turno actual, aplica el movimiento solo si el validador lo aprueba, alterna turnos, expone `undo()` y el estado (`IN_PROGRESS` / `CHECK`).

Tests: alternancia de turnos, captura aplicada, mover pieza del rival rechazado, undo restaura el tablero exacto.

## Etapa 7 — Prueba de la arquitectura

Un test que agrega una pieza inventada (por ejemplo, una que se mueve como caballo y como alfil) usando **solo composición**, sin tocar una sola clase existente.

Si hay que modificar algo para que entre, la arquitectura no cumple lo que promete y se arregla antes de entregar. Este test es la evidencia verificable del criterio "agregar sin modificar", y es el ensayo de lo que puede pedir el docente en la defensa individual.

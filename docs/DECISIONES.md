# Decisiones de diseño

## `Piece.equals`/`hashCode` comparan solo `type` y `color`

**Qué:** dos instancias de `Piece` son iguales si tienen el mismo `PieceType` y `PieceColor`, sin importar ninguna otra colaboración que la pieza llegue a tener.

**Por qué:** `Board.equals` compara "por contenido" (para `copy()` y para tests), y ese contenido son las piezas ocupando casillas. Dos torres blancas son la misma pieza a los efectos de esa comparación, independientemente de cómo se implemente su comportamiento.

**Cuándo la rompería:** en la Etapa 2, `Piece` va a componer una `MovementStrategy` inyectada por constructor. Si alguna vez dos piezas del mismo tipo y color necesitaran comportarse distinto (una variante de reglas con estrategias diferentes para la "misma" pieza), esta igualdad dejaría de alcanzar y habría que decidir si `MovementStrategy` entra en la comparación.

## `reachableSquares` incluye la casilla de la pieza que bloquea, sea propia o rival

**Qué:** las estrategias calculan geometría y bloqueo. El recorrido se corta en la primera casilla ocupada y la incluye sin mirar el color. El peón captura en diagonal si la casilla está ocupada por cualquier pieza.

**Por qué:** descartar capturas propias es una regla de juego, y le corresponde a `NoFriendlyCaptureRule` (Etapa 4). Si las estrategias también lo hicieran, esa regla sería redundante y la responsabilidad estaría duplicada. Además, `attackedSquares` necesita incluir las casillas propias que la pieza defiende.

**Cuándo la rompería:** si apareciera un consumidor de `reachableSquares` que no pasa por el validador (por ejemplo, listar jugadas legales para una UI). En ese caso convendría un método que ya devuelva solo destinos legales, en lugar de filtrar en cada consumidor.

## `PawnMovement` no tiene estado: deriva dirección y fila inicial del color y del tablero

**Qué:** las blancas avanzan +1 y arrancan en la fila 1. Las negras avanzan -1 y arrancan en `rows - 2`. Se borró `-int startingRow` del UML.

**Por qué:** `StandardPieceFactory` mapea estrategias por tipo, así que una única instancia de `PawnMovement` sirve para los dos colores, y un solo `startingRow` no puede representar a ambos. Derivar la fila inicial de `board.size()` evita hardcodear el 8.

**Cuándo la rompería:** si una variante pusiera los peones en otra fila que no fuera la segunda desde cada borde, o si las blancas arrancaran arriba. Ahí la convención tendría que entrar por constructor (una instancia por color) y la fábrica tendría que usar como clave tipo + color.

## `attackedSquares` es un método default; lo sobreescriben solo `PawnMovement` y `CompositeMovement`

**Qué:** por defecto, una pieza ataca las mismas casillas a las que puede ir. `CompositeMovement` sobreescribe el método para unir los `attackedSquares` de sus partes.

**Por qué:** si `CompositeMovement` usara el default, una pieza compuesta que incluya un peón atacaría las casillas a las que avanza en vez de sus diagonales. Es la trampa 1 reapareciendo por la composición. Para eso está el test `unaComposicionConPeonAtacaConElAtaqueDelPeon`.

**Cuándo la rompería:** si aparecieran varias piezas que atacan distinto de como se mueven, el default dejaría de ser el caso común. Ahí convendría hacer el método abstracto, para que cada estrategia nueva tenga que decidirlo de forma explícita.

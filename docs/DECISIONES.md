# Decisiones de diseño

## `Piece.equals`/`hashCode` comparan solo `type` y `color`

**Qué:** dos instancias de `Piece` son iguales si tienen el mismo `PieceType` y `PieceColor`, sin importar ninguna otra colaboración que la pieza llegue a tener.

**Por qué:** `Board.equals` compara "por contenido" (para `copy()` y para tests), y ese contenido son las piezas ocupando casillas. Dos torres blancas son la misma pieza a los efectos de esa comparación, independientemente de cómo se implemente su comportamiento.

**Cuándo la rompería:** en la Etapa 2, `Piece` va a componer una `MovementStrategy` inyectada por constructor. Si alguna vez dos piezas del mismo tipo y color necesitaran comportarse distinto (una variante de reglas con estrategias diferentes para la "misma" pieza), esta igualdad dejaría de alcanzar y habría que decidir si `MovementStrategy` entra en la comparación.

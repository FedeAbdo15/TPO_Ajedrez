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

## `StandardPieceFactory` crea sus estrategias en el constructor en vez de recibirlas

**Qué:** la fábrica arma internamente el mapa `PieceType → MovementStrategy` con una instancia por tipo, compartida por todas las piezas de ese tipo y de los dos colores.

**Por qué:** la regla "toda dependencia entra por constructor" apunta a las colaboraciones de una clase de negocio; crear objetos es justamente la responsabilidad de una fábrica, y es el único lugar que sabe qué movimiento tiene cada pieza estándar. Si recibiera el mapa, ese conocimiento se mudaría a quien la construye, que hoy no existe. Compartir instancias es seguro porque las estrategias no tienen estado.

**Cuándo la rompería:** si una variante necesitara mapear los mismos tipos a otras estrategias. Ahí el mapa entraría por constructor, o sería otra implementación de `PieceFactory`.

## El 8x8 vive como constante en `StandardChessSetup`

**Qué:** `StandardChessSetup` define `SIZE = new BoardSize(8, BACK_RANK.size())`. Las filas de peones y de piezas negras se derivan de ese tamaño, no se escriben como 6 y 7.

**Por qué:** "ningún 8 hardcodeado en la lógica" se refiere a las clases que juegan (tablero, estrategias, reglas): esas siguen sin saber el tamaño. El ajedrez estándar sí es 8x8 por definición, y su fila de atrás tiene exactamente 8 piezas; recibir el tamaño por constructor permitiría armar tableros sin sentido. Cambiar el tamaño es escribir otra implementación de `BoardSetup`, sin tocar esta ni ninguna otra clase. Esa es la razón concreta para que `BoardSetup` sea una interfaz con una sola implementación hoy.

**Cuándo la rompería:** si se pidiera el mismo armado estándar centrado en tableros de distinto tamaño. Ahí `BoardSize` entraría por constructor y la fila de atrás se ubicaría según el ancho recibido.

## `MoveValidator` corta en la primera regla que falla, y el orden de la lista importa

**Qué:** el validador evalúa las reglas en el orden recibido y devuelve el motivo de la primera que no se cumple. Cada regla puede asumir que las anteriores pasaron: `TurnOwnershipRule` y `PieceMovementRule` dan por hecho que hay una pieza en el origen, y `NoFriendlyCaptureRule` da por hecho que el destino está dentro del tablero. El orden estándar es origen → turno → movimiento → captura propia.

**Por qué:** así cada regla responde una sola pregunta, sin repetir los chequeos de las otras. El movimiento fuera del tablero no necesita una regla propia (que el UML no tiene): un origen afuera no tiene pieza, así que lo rechaza `SourceHasPieceRule`, y un destino afuera no lo alcanza ninguna estrategia, así que lo rechaza `PieceMovementRule`. Ninguna de las dos lanza excepción, porque comprueban antes de leer el tablero.

**Cuándo la rompería:** si las reglas tuvieran que poder armarse en cualquier orden (por ejemplo, si las eligiera el usuario). Ahí cada regla tendría que protegerse sola de los casos que hoy cubren las anteriores.

## El detector de jaque usa `attackedSquares`, no movimientos legales

**Qué:** `AttackBasedCheckDetector` recorre las piezas rivales y se fija si la casilla del rey está entre sus `attackedSquares`. No pasa por `MoveValidator` ni por ninguna regla. `KingSafetyRule` simula el movimiento sobre `board.copy()` y le pregunta al detector.

**Por qué:** por tres razones. (1) Evita la recursión de la trampa 2: si el detector validara movimientos, llamaría a `KingSafetyRule`, que llamaría al detector, y así para siempre. (2) Es la regla real del ajedrez: una pieza clavada igual da jaque, aunque no pueda moverse legalmente. (3) "Atacar" es la pregunta correcta, y no "poder ir". Hoy, en la detección de jaque, las dos dan lo mismo incluso con el peón, porque `reachableSquares` incluye la diagonal ocupada y el rey siempre ocupa su casilla. Pero una pieza nueva que capture distinto de como se mueve (por ejemplo, una que capture saltando por encima de otra) rompería un detector basado en `reachableSquares`.

**Cuándo la rompería:** si apareciera una regla que dependa de movimientos legales del rival (por ejemplo, jaque mate o ahogado). Ahí haría falta generar movimientos legales, y eso sí tendría que usar el validador, pero desde afuera del detector para no reintroducir la recursión.

## `Game` crea un `MoveCommand` por jugada, y su estado se calcula en vez de guardarse

**Qué:** `Game.move` hace `new MoveCommand(board, move)` en cada jugada aprobada, en vez de recibirlo por constructor. `status()` le pregunta al detector en cada llamada; no hay un campo de estado.

**Por qué:** la regla "nada de `new` de una colaboración" apunta a los servicios con los que una clase trabaja (validador, detector, historial), y esos sí entran por constructor. Un `MoveCommand` no es un colaborador: es el registro de una jugada concreta, con su propia pieza capturada, y no puede existir antes de saber qué se mueve. El UML ya lo marca (`Game ..> MoveCommand : crea`). El estado calculado evita que `undo` tenga que restaurarlo: después de deshacer, el tablero manda.

**Cuándo la rompería:** si hubiera que crear comandos distintos según la jugada (por ejemplo, enroque o promoción, que mueven o cambian más de una pieza). Ahí la creación pasaría a una fábrica de comandos inyectada en `Game`. Si calcular el estado se volviera caro (jaque mate exige recorrer todas las jugadas posibles), convendría guardarlo y actualizarlo junto con el historial.

## `PieceType` es un record con constantes, no un enum

**Qué:** `PieceType` pasó de `enum` a `record PieceType(String name)`, con las seis piezas estándar como constantes (`PieceType.ROOK`, etc.). Las comparaciones usan `equals`, y `StandardPieceFactory` usa `Map.of` en vez de `EnumMap`.

**Por qué:** un enum es un conjunto cerrado: para sumar una pieza inventada había que agregarle un valor, o sea, modificar una clase existente. Eso contradecía la promesa "agregar una pieza = estrategia nueva + entrada en una fábrica, cero modificaciones", y la Etapa 7 lo dejó en evidencia. Con el record, una variante crea `new PieceType("CHANCELLOR")` desde afuera. El código que usaba las constantes no cambió. Lo que se pierde es lo que el núcleo no usaba: `values()`, `switch` exhaustivo y `EnumMap`.

**Cuándo la rompería:** si el núcleo necesitara recorrer todos los tipos posibles (por ejemplo, para validar que una fábrica los cubra todos) o hacer un `switch` exhaustivo. Ahí convendría una interfaz `PieceType` implementada por un enum de piezas estándar y otro por cada variante.

## Una pieza nueva entra por una fábrica de la variante, no por `StandardPieceFactory`

**Qué:** el canciller (caballo + torre) y el arzobispo (caballo + alfil) de la Etapa 7 se arman con un `PieceType` nuevo, un `CompositeMovement` de estrategias existentes y una `PieceFactory` de la variante, que conoce las piezas nuevas y delega el resto en `StandardPieceFactory`. El tablero Capablanca (8x10) es un `BoardSetup` propio. Las dos interfaces tienen un solo método, así que alcanza con una lambda.

**Por qué:** "una entrada en la fábrica" no puede significar editar `StandardPieceFactory`, porque eso sería modificar una clase existente. La fábrica de la variante es un decorador: suma lo nuevo y reutiliza lo estándar. Validador, detector de jaque, `Game` y undo funcionan con las piezas nuevas sin saber que existen, porque solo hablan con `MovementStrategy`. `PieceByCompositionTest` es la evidencia: agrega dos piezas y un tamaño de tablero sin una sola línea nueva en el núcleo.

**Cuándo la rompería:** si las variantes se volvieran parte del producto (elegirlas desde la consola, por ejemplo). Ahí la fábrica y el armado de cada variante pasarían de los tests a clases de producción, en el lugar donde se arma la partida.

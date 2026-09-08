# TPO Ajedrez — Ingeniería de Software (UADE)

## Qué es este proyecto

Un ajedrez en Java. El ajedrez es el vehículo: lo que se evalúa es la arquitectura — separación núcleo/adaptadores, SOLID, composición sobre herencia, y que el UML entregado sea fiel al código real.

Lo desarrolla una sola persona, que después tiene que defender cada decisión de diseño en un final oral individual, sin el código adelante. El docente puede pedir en el momento una extensión no prevista: agregar una pieza nueva con movimiento propio, o cambiar el tamaño del tablero.

## Stack

- Java 17, Maven, JUnit 5 (`scope=test`).
- Cero dependencias de terceros en código de producción.

## Estructura — mantenerla simple

Todo el código vive en **un solo paquete: `ajedrez.core`**. Cada archivo arranca con `package ajedrez.core;`.

```
tpo-ajedrez/
├── pom.xml
├── CLAUDE.md
├── docs/
│   ├── uml.mermaid
│   ├── ETAPAS.md
│   └── DECISIONES.md
└── src/
    ├── main/core/     <- todas las clases
    └── test/core/     <- todos los tests
```

No crear subpaquetes, ni carpetas por tipo de archivo (nada de `interfaces/`, `clases/`, `enums/`): una interfaz y sus implementaciones son una sola idea y viven juntas.

Cuando más adelante aparezca la consola va a ir en `ajedrez.app`, y ahí sí van a ser dos paquetes: núcleo y adaptador. Esa es la única división que importa, y no se agrega ninguna otra sin que yo la pida.

## Convenciones

- Comentarios y `@DisplayName` en español. Identificadores (clases, métodos, variables) en inglés.
- Un test por caso, Arrange-Act-Assert, nombres descriptivos.
- Records para value objects inmutables: `Position`, `Move`, `BoardSize`, `ValidationResult`, `MoveResult`.

## Reglas de arquitectura — no negociables

- `ajedrez.core` no importa nada de fuera de `ajedrez`. Solo JDK, y JUnit en tests.
- Las dependencias apuntan hacia adentro: las reglas conocen el dominio, el dominio no conoce las reglas.
- Toda dependencia entra por constructor. Nada de `new` de una colaboración adentro de una clase de negocio, nada de singletons, nada de estáticos con estado.
- Agregar una pieza nueva = una estrategia de movimiento nueva + una entrada en la fábrica. Cero modificaciones a clases existentes.
- Agregar una regla de validación nueva = una clase nueva que implementa `MoveRule` + una entrada en la lista inyectada. Cero modificaciones a `MoveValidator` ni a las otras reglas.
- El tablero recibe su tamaño. No hay ningún 8 hardcodeado en la lógica.

## El UML es el contrato

`docs/uml.mermaid` es el diagrama que se entrega. El código debe coincidir: mismos nombres de clase, mismas relaciones, misma dirección de dependencias.

Si al implementar encontrás que el diagrama está mal, **pará y avisá con la corrección concreta** antes de desviarte. La inconsistencia entre UML y código es un criterio de evaluación propio de la materia.

## Fuera de alcance — no implementar sin que se pida explícitamente

Consola, UI, renderers, parsers de notación algebraica, jaque mate, enroque, captura al paso, promoción, tablas, IA, persistencia, repositorios, logging, framework de DI, configuración externa.

La sobre-ingeniería es un criterio de corrección **en contra** en esta materia. No agregar una interfaz que hoy tiene una sola implementación y ninguna razón concreta para tener otra. Tampoco capas ni paquetes "para ordenar". Si una abstracción de más parece justificada, justificarla en una línea o no ponerla.

## Cómo trabajar en este repo

- Se avanza **por etapas** (ver `docs/ETAPAS.md`). Una etapa por vez, sin adelantarse.
- Antes del código de cada tanda: 2-3 oraciones explicando qué se va a hacer y por qué así.
- Al terminar una etapa: correr `mvn test`, mostrar la salida, hacer un commit con mensaje descriptivo en español, y **frenar** hasta que yo confirme.
- Nunca tocar clases de etapas anteriores sin avisar primero qué se cambia y por qué.
- Cerrar cada etapa con 2 o 3 preguntas de comprensión sobre lo recién escrito, del tipo que haría un docente en un final. Si respondo mal o dudo, explicarlo distinto antes de seguir.

## Documento de decisiones

`docs/DECISIONES.md` se actualiza **en el momento** en que se toma cada decisión no obvia, nunca al final. Formato: **qué / por qué / cuándo la rompería**.

Sin relleno: si una decisión es evidente, no se documenta.

## Dos trampas de diseño conocidas

1. **El peón ataca distinto de como se mueve** (avanza recto, captura en diagonal). Un detector de jaque que pregunte "a dónde puede moverse esta pieza" falla con el peón. Por eso `MovementStrategy` separa `reachableSquares` de `attackedSquares`.
2. **Recursión infinita en `KingSafetyRule`**: la regla simula el movimiento y pregunta si el rey queda en jaque; si el detector validara movimientos con el mismo `MoveValidator`, se llamaría a sí mismo para siempre. El detector trabaja con las estrategias de movimiento, nunca con el validador.

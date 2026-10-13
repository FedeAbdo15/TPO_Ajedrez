package ajedrez.core;

import java.util.ArrayDeque;
import java.util.Deque;

public class MoveHistory {

    private final Deque<Command> executed = new ArrayDeque<>();

    public void push(Command command) {
        executed.push(command);
    }

    // Deshace el último comando ejecutado (LIFO).
    public void undoLast() {
        if (executed.isEmpty()) {
            throw new IllegalStateException("No hay movimientos para deshacer");
        }
        executed.pop().undo();
    }

    public boolean isEmpty() {
        return executed.isEmpty();
    }
}

package ajedrez.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoveHistoryTest {

    // Comando que solo anota en una lista cuándo se lo deshace.
    private static Command recording(String name, List<String> undone) {
        return new Command() {
            @Override
            public void execute() {
            }

            @Override
            public void undo() {
                undone.add(name);
            }
        };
    }

    @Test
    @DisplayName("Un historial nuevo está vacío")
    void unHistorialNuevoEstaVacio() {
        assertTrue(new MoveHistory().isEmpty());
    }

    @Test
    @DisplayName("Deshace primero el último comando agregado")
    void deshacePrimeroElUltimoComandoAgregado() {
        List<String> undone = new ArrayList<>();
        MoveHistory history = new MoveHistory();
        history.push(recording("primero", undone));
        history.push(recording("segundo", undone));

        history.undoLast();
        history.undoLast();

        assertEquals(List.of("segundo", "primero"), undone);
        assertTrue(history.isEmpty());
    }

    @Test
    @DisplayName("Después de agregar un comando el historial no está vacío")
    void despuesDeAgregarUnComandoElHistorialNoEstaVacio() {
        MoveHistory history = new MoveHistory();

        history.push(recording("unico", new ArrayList<>()));

        assertFalse(history.isEmpty());
    }

    @Test
    @DisplayName("Deshacer con el historial vacío lanza IllegalStateException")
    void deshacerConElHistorialVacioLanzaExcepcion() {
        assertThrows(IllegalStateException.class, () -> new MoveHistory().undoLast());
    }
}

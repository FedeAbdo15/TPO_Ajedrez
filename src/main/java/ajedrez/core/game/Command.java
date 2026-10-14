package ajedrez.core.game;

public interface Command {

    void execute();

    void undo();
}

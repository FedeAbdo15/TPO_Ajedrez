package ajedrez.app.swing;

import ajedrez.core.domain.Position;
import java.awt.image.BufferedImage;

public class DragState {

    public Position from;
    public BufferedImage image;
    public int cursorX;
    public int cursorY;
    public boolean active;

    public DragState() {
        this.active = false;
    }
}

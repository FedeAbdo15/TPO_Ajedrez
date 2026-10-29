package ajedrez.app.swing;

import ajedrez.core.domain.PieceColor;
import ajedrez.core.domain.PieceType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PieceImageLoader {

    private static final String RESOURCE_PREFIX = "/pieces/";
    private final Map<String, BufferedImage> cache = new HashMap<>();

    public BufferedImage load(PieceType type, PieceColor color) {
        String key = (color == PieceColor.WHITE ? "w" : "b") + letter(type);
        BufferedImage img = cache.get(key);
        if (img != null) {
            return img;
        }
        img = loadResource(key);
        if (img == null) {
            img = fallbackImage(key.charAt(0), letter(type));
        }
        cache.put(key, img);
        return img;
    }

    private BufferedImage loadResource(String key) {
        try (var input = PieceImageLoader.class.getResourceAsStream(RESOURCE_PREFIX + key + ".png")) {
            if (input == null) {
                return null;
            }
            return ImageIO.read(input);
        } catch (IOException e) {
            return null;
        }
    }

    private static String letter(PieceType type) {
        return switch (type.name()) {
            case "PAWN" -> "P";
            case "KNIGHT" -> "N";
            case "BISHOP" -> "B";
            case "ROOK" -> "R";
            case "QUEEN" -> "Q";
            case "KING" -> "K";
            default -> type.name();
        };
    }

    private static BufferedImage fallbackImage(char colorInitial, String pieceLetter) {
        BufferedImage img = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color body = colorInitial == 'w' ? Color.WHITE : Color.BLACK;
            Color mark = colorInitial == 'w' ? Color.BLACK : Color.WHITE;
            g.setColor(body);
            g.fillOval(6, 6, 52, 52);
            g.setColor(mark);
            g.setStroke(new BasicStroke(3));
            g.drawOval(6, 6, 52, 52);
            g.setFont(new Font("SansSerif", Font.BOLD, 26));
            g.drawString(pieceLetter, 24, 40);
        } finally {
            g.dispose();
        }
        return img;
    }
}

package view.components.Flyweight;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Flyweight Factory for poster images.
 * Ensures each poster URL is downloaded once and shared.
 */
public class PosterFlyweightFactory {

    // Shared flyweights
    private static final Map<String, ImageIcon> cache = new ConcurrentHashMap<>();

    /**
     * Returns a shared ImageIcon for the given URL.
     * If already cached → return the existing flyweight.
     * If not cached → load asynchronously and return a temporary placeholder.
     */
    public static ImageIcon getPoster(String url, int width, int height, Runnable onLoaded) {

        // If already cached → return the flyweight
        if (cache.containsKey(url)) {
            return cache.get(url);
        }

        // Temporary placeholder icon
        ImageIcon placeholder = new ImageIcon(new byte[]{});
        cache.put(url, placeholder);

        // Load asynchronously
        new Thread(() -> {
            try {
                ImageIcon original = new ImageIcon(new URL(url));
                Image scaled = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);

                ImageIcon finalIcon = new ImageIcon(scaled);
                cache.put(url, finalIcon);

                // Callback to update UI
                if (onLoaded != null) {
                    SwingUtilities.invokeLater(onLoaded);
                }

            } catch (Exception e) {
                // On error → leave placeholder
            }
        }).start();

        return placeholder;
    }
}

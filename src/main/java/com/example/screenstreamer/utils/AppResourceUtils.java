package com.example.screenstreamer.utils;

import org.springframework.util.ResourceUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public final class AppResourceUtils {
    private AppResourceUtils() { }

    public static InputStream getStream(String resourcePath) {
        try {
            var url = ResourceUtils.getURL(resourcePath);
            return url.openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage getImage(String resourcePath) {
        try (var imageStream = AppResourceUtils.getStream(resourcePath)) {
            return ImageIO.read(imageStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

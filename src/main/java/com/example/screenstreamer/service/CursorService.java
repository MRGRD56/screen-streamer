package com.example.screenstreamer.service;

import com.example.screenstreamer.utils.AppResourceUtils;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

@Service
public class CursorService {
    private final BufferedImage cursorImage = getCursorImage();

    public PointerInfo getPointerInfo() {
        return MouseInfo.getPointerInfo();
    }

    private BufferedImage getCursorImage() {
        return AppResourceUtils.getImage("classpath:assets/cursor.png");
    }

    public void drawCursor(Graphics graphics, GraphicsDevice graphicsDevice) {
        var pointerInfo = getPointerInfo();
        if (!Objects.equals(pointerInfo.getDevice().getIDstring(), graphicsDevice.getIDstring())) {
            return;
        }

        var cursorPosition = pointerInfo.getLocation();
        graphics.drawImage(cursorImage,
                cursorPosition.x, cursorPosition.y,
                cursorImage.getWidth(), cursorImage.getHeight(),
                null);
    }

    public void drawCursor(BufferedImage originalImage, GraphicsDevice graphicsDevice) {
        drawCursor(originalImage.getGraphics(), graphicsDevice);
    }
}

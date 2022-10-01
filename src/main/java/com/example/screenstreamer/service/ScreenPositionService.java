package com.example.screenstreamer.service;

import org.springframework.stereotype.Service;

import java.awt.*;

@Service
public class ScreenPositionService {
    public Point getPositionOnScreen(GraphicsDevice graphicsDevice, Point absolutePosition) {
        var screenBounds = getScreenBounds(graphicsDevice);
        return new Point(
                absolutePosition.x - screenBounds.x, absolutePosition.y - screenBounds.y);
    }

    public Point getAbsolutePosition(GraphicsDevice graphicsDevice, Point positionOnScreen) {
        var absolutePosition = getScreenBounds(graphicsDevice);
        return new Point(
                positionOnScreen.x + absolutePosition.x, positionOnScreen.y + absolutePosition.y);
    }

    public Point getPositionOnScreenFromPercentage(GraphicsDevice graphicsDevice, double xPercentage, double yPercentage) {
        var screenBounds = getScreenBounds(graphicsDevice);
        return new Point(
                (int) Math.round(xPercentage * screenBounds.width),
                (int) Math.round(yPercentage * screenBounds.height)
        );
    }

    private Rectangle getScreenBounds(GraphicsDevice graphicsDevice) {
        return graphicsDevice.getDefaultConfiguration().getBounds();
    }
}

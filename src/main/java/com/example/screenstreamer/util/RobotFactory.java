package com.example.screenstreamer.util;

import org.springframework.lang.NonNull;

import java.awt.*;

public final class RobotFactory {
    private RobotFactory() { }

    public static Robot create() {
        try {
            return new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public static Robot create(@NonNull GraphicsDevice graphicsDevice) {
        try {
            return new Robot(graphicsDevice);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.example.screenstreamer.service;

import com.example.screenstreamer.model.MouseButton;
import com.example.screenstreamer.util.AppResourceUtils;
import com.example.screenstreamer.util.RobotFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

@Service
public class CursorService {
    private final BufferedImage cursorImage = getCursorImage();
    private final Robot robot = RobotFactory.create();
    private final ScreenPositionService screenPositionService;

    public CursorService(ScreenPositionService screenPositionService) {
        this.screenPositionService = screenPositionService;
    }

    public PointerInfo getPointerInfo() {
        return MouseInfo.getPointerInfo();
    }

    public Point getPosition() {
        return getPointerInfo().getLocation();
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
        var cursorPositionOnScreen = screenPositionService.getPositionOnScreen(graphicsDevice, cursorPosition);

        graphics.drawImage(cursorImage,
                cursorPositionOnScreen.x, cursorPositionOnScreen.y,
                cursorImage.getWidth(), cursorImage.getHeight(),
                null);
    }

    public void drawCursor(BufferedImage originalImage, GraphicsDevice graphicsDevice) {
        drawCursor(originalImage.getGraphics(), graphicsDevice);
    }

    public void moveCursor(int x, int y) {
        robot.mouseMove(x, y);
    }

    public void moveCursor(Point point) {
        robot.mouseMove(point.x, point.y);
    }

    public void moveCursor(PointerInfo info) {
        var position = screenPositionService.getAbsolutePosition(info.getDevice(), info.getLocation());
        robot.mouseMove(position.x, position.y);
    }

    public void click(@NonNull MouseButton button) {
        robot.mousePress(button.code());
        robot.mouseRelease(button.code());
    }
}

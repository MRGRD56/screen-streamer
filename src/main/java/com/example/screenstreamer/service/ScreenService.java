package com.example.screenstreamer.service;

import com.example.screenstreamer.model.config.ScreenshotSettings;
import com.example.screenstreamer.model.dto.ScreenDto;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;

@Service
public class ScreenService {
    private final Robot robot = createRobot();
    private final GraphicsDevice[] screens = getScreens();
    private final ImageService imageService;
    private final CursorService cursorService;

    public ScreenService(
            ImageService imageService,
            CursorService cursorService) {
        this.imageService = imageService;
        this.cursorService = cursorService;
    }

    private Robot createRobot() {
        try {
            return new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    private GraphicsDevice[] getScreens() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    }

    public ScreenDto[] getScreensDto() {
        var result = new ScreenDto[screens.length];

        for (var i = 0; i < screens.length; i++) {
            result[i] = ScreenDto.fromGraphicsDevice(i, screens[i]);
        }

        return result;
    }

    public GraphicsDevice getScreen(Integer index) {
        if (index == null) {
            index = 0;
        }

        if (screens.length == 0) {
            throw new RuntimeException("No screens");
        }
        if (index >= screens.length) {
            throw new RuntimeException("No such screen");
        }

        return screens[index];
    }

    private BufferedImage getOriginalScreenshot(@NonNull GraphicsDevice graphicsDevice, boolean hasCursor) {
        var screenshot = robot.createScreenCapture(
                graphicsDevice.getDefaultConfiguration().getBounds());
        if (hasCursor) {
            cursorService.drawCursor(screenshot, graphicsDevice);
        }

        return screenshot;
    }

    public BufferedImage getScreenshotAsImage(@NonNull ScreenshotSettings screenshotSettings) {
        var graphicsDevice = getScreen(screenshotSettings.getScreen());
        var screenshot = getOriginalScreenshot(graphicsDevice, true);

        if (screenshotSettings.getSizeMultiplier() != null) {
            screenshot = imageService.resizeImage(screenshot, screenshotSettings.getSizeMultiplier());
        }
        return screenshot;
    }

    public byte[] getScreenshotAsBytes(@NonNull ScreenshotSettings screenshotSettings) {
        var screenshot = getScreenshotAsImage(screenshotSettings);
        return imageService.compressJpegAsBytes(screenshot, screenshotSettings.getQuality());
    }
}

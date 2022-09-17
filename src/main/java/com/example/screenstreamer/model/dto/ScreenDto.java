package com.example.screenstreamer.model.dto;

import java.awt.*;

public class ScreenDto {
    private int index;
    private String id;
    private DisplayMode displayMode;

    public static ScreenDto fromGraphicsDevice(int index, GraphicsDevice graphicsDevice) {
        var screenDto = new ScreenDto();

        screenDto.index = index;
        screenDto.id = graphicsDevice.getIDstring();
        screenDto.displayMode = graphicsDevice.getDisplayMode();

        return screenDto;
    }

    public int getIndex() {
        return index;
    }

    public String getId() {
        return id;
    }

    public DisplayMode getDisplayMode() {
        return displayMode;
    }
}

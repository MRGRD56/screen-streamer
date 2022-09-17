package com.example.screenstreamer.model.config;

import org.springframework.lang.NonNull;

public class ScreenshotSettings {
    private Integer screen;
    private Float quality;
    private Float sizeMultiplier;

    public ScreenshotSettings() {
    }

    public ScreenshotSettings(Integer screen, Float quality, Float sizeMultiplier) {
        this.screen = screen;
        this.quality = quality;
        this.sizeMultiplier = sizeMultiplier;
    }

    @NonNull
    public Integer getScreen() {
        return screen == null ? 0 : screen;
    }

    public void setScreen(Integer screen) {
        this.screen = screen;
    }

    public Float getQuality() {
        return quality;
    }

    public void setQuality(Float quality) {
        this.quality = quality;
    }

    public Float getSizeMultiplier() {
        return sizeMultiplier;
    }

    public void setSizeMultiplier(Float sizeMultiplier) {
        this.sizeMultiplier = sizeMultiplier;
    }
}

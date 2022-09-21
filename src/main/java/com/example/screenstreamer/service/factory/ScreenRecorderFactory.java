package com.example.screenstreamer.service.factory;

import com.example.screenstreamer.model.config.ScreenCaptureSettings;
import com.example.screenstreamer.service.ImageService;
import com.example.screenstreamer.util.video.ScreenRecorder;
import com.example.screenstreamer.service.ScreenService;
import org.springframework.stereotype.Component;

@Component
public class ScreenRecorderFactory {
    private final ScreenService screenService;
    private final ImageService imageService;
    private final ScreenCaptureSettings screenCaptureSettings;

    public ScreenRecorderFactory(
            ScreenService screenService,
            ImageService imageService,
            ScreenCaptureSettings screenCaptureSettings) {
        this.screenService = screenService;
        this.imageService = imageService;
        this.screenCaptureSettings = screenCaptureSettings;
    }

    public ScreenRecorder create(String filename, ScreenCaptureSettings captureSettings) {
        return new ScreenRecorder(filename, captureSettings, screenService, imageService);
    }

    public ScreenRecorder create(String filename) {
        return create(filename, screenCaptureSettings);
    }
}

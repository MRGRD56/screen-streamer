package com.example.screenstreamer.service;

import com.example.screenstreamer.service.factory.ScreenRecorderFactory;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class ScreenStreamingService {
    private static final String CAPTURE_FILENAME = Path.of("tmp/screen_stream.mkv").toAbsolutePath().toString();

    private final ScreenRecorder screenRecorder;

    public ScreenStreamingService(
            ScreenRecorderFactory screenRecorderFactory) {
        this.screenRecorder = screenRecorderFactory.create(CAPTURE_FILENAME);
        this.screenRecorder.startRecording();
        LoggerFactory.getLogger("DEBUG").info("Screen capture started");
        try {
            Thread.sleep(5_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        screenRecorder.stopRecording();
        LoggerFactory.getLogger("DEBUG").info("Screen capture finished");
    }
}
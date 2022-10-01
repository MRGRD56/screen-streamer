package com.example.screenstreamer.service;

import com.example.screenstreamer.model.config.ScreenCaptureSettings;
import com.example.screenstreamer.util.video.AnyFrameMultiWriter;
import com.example.screenstreamer.util.video.FrameMultiWriter;
import com.example.screenstreamer.util.video.FrameWriter;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ScreenCaptureService {
    private final ScreenCaptureSettings screenCaptureSettings;
    private final ScreenService screenService;

//    private final Subject<byte[]> frameSubject = PublishSubject.create();

    private final FrameMultiWriter<FrameWriter> frameMultiWriter = new AnyFrameMultiWriter();

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public ScreenCaptureService(
            ScreenCaptureSettings screenCaptureSettings,
            ScreenService screenService) {
        this.screenCaptureSettings = screenCaptureSettings;
        this.screenService = screenService;

//        frameSubject.subscribe(frameMultiWriter::apply);
        startCapture();
    }

    public FrameMultiWriter<FrameWriter> getFrameMultiWriter() {
        return frameMultiWriter;
    }

    private void startCapture() {
        final long delay = (long) Math.ceil(1000D / screenCaptureSettings.getFps());

        executor.submit(() -> {
            while (true) {
                try {
                    if (frameMultiWriter.hasWriters()) {
                        var frame = screenService.getScreenshotAsBytes(screenCaptureSettings);
                        frameMultiWriter.apply(frame);
//                        frameSubject.onNext(frame);
                    }

                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}

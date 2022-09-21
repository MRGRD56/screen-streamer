package com.example.screenstreamer.service;

import com.example.screenstreamer.model.config.ScreenCaptureSettings;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ScreenCaptureService {
    private final ScreenCaptureSettings screenCaptureSettings;
    private final ScreenService screenService;

    private final Subject<byte[]> frameSubject = PublishSubject.create();

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public ScreenCaptureService(
            ScreenCaptureSettings screenCaptureSettings,
            ScreenService screenService) {

        this.screenCaptureSettings = screenCaptureSettings;
        this.screenService = screenService;

        startCapture();
    }

    public Observable<byte[]> getFrameObservable() {
        return frameSubject;
    }

    private void startCapture() {
        final long delay = (long) Math.ceil(1000D / screenCaptureSettings.getFps());

        executor.submit(() -> {
            while (true) {
                try {
                    var frame = screenService.getScreenshotAsBytes(screenCaptureSettings);
                    frameSubject.onNext(frame);

                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}

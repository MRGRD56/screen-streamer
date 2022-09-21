package com.example.screenstreamer.service;

import com.example.screenstreamer.util.video.mjpeg.MjpegMultiWriter;
import com.example.screenstreamer.util.video.mjpeg.MjpegWriter;
import org.springframework.stereotype.Service;

@Service
public class MjpegScreenStreamingService {
    private final MjpegMultiWriter mjpegMultiWriter = new MjpegMultiWriter();

    public MjpegScreenStreamingService(ScreenCaptureService screenCaptureService) {
        screenCaptureService.getFrameObservable().subscribe(mjpegMultiWriter::apply);
    }

    public MjpegWriter getMjpegStream() {
        var mjpegWriter = new MjpegWriter();
        mjpegMultiWriter.addWriter(mjpegWriter);
        return mjpegWriter;
    }

    public void stopMjpegStream(MjpegWriter mjpegWriter) {
        mjpegMultiWriter.removeWriter(mjpegWriter);
    }
}
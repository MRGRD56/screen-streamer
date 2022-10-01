package com.example.screenstreamer.service;

import com.example.screenstreamer.util.video.FrameMultiWriter;
import com.example.screenstreamer.util.video.FrameWriter;
import com.example.screenstreamer.util.video.mjpeg.MjpegWriter;
import org.springframework.stereotype.Service;

@Service
public class MjpegScreenStreamingService {
    private final FrameMultiWriter<FrameWriter> multiWriter;

    public MjpegScreenStreamingService(ScreenCaptureService screenCaptureService) {
        multiWriter = screenCaptureService.getFrameMultiWriter();
    }

    public MjpegWriter startMjpegStream() {
        var mjpegWriter = new MjpegWriter();
        multiWriter.addWriter(mjpegWriter);
        return mjpegWriter;
    }

    public void stopMjpegStream(MjpegWriter mjpegWriter) {
        multiWriter.removeWriter(mjpegWriter);
    }
}
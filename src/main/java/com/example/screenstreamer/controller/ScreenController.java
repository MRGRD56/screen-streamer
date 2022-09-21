package com.example.screenstreamer.controller;

import com.example.screenstreamer.model.config.ScreenshotSettings;
import com.example.screenstreamer.model.dto.ScreenDto;
import com.example.screenstreamer.service.MjpegScreenStreamingService;
import com.example.screenstreamer.service.ScreenService;
import com.example.screenstreamer.util.video.mjpeg.MjpegWriter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/screen")
public class ScreenController {
    private final ScreenService screenService;
    private final MjpegScreenStreamingService mjpegScreenStreamingService;

    public ScreenController(
            ScreenService screenService,
            MjpegScreenStreamingService mjpegScreenStreamingService) {
        this.screenService = screenService;
        this.mjpegScreenStreamingService = mjpegScreenStreamingService;
    }

    @GetMapping(
            value = "screenshot",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public byte[] getScreenshot(
            @RequestParam(name = "screen", defaultValue = "0") int screenIndex,
            @RequestParam(required = false) Float quality,
            @RequestParam(required = false) Float sizeMultiplier) {
        var settings = new ScreenshotSettings(screenIndex, quality, sizeMultiplier);
        return screenService.getScreenshotBytes(screenService.getScreenshotAsImage(settings), settings);
    }

    @GetMapping("screens")
    public ScreenDto[] getScreens() {
        return screenService.getScreensDto();
    }

    @GetMapping("stream.mjpeg")
    public void getScreenStreamMjpeg(HttpServletResponse response) throws IOException {
        var mjpegWriter = mjpegScreenStreamingService.getMjpegStream();

        response.setHeader("Cache-Control", "no-cache, private");
        response.setHeader("Content-Type", "multipart/x-mixed-replace;boundary=" + MjpegWriter.BOUNDARY);

        var outputStream = response.getOutputStream();

        while (true) {
            try {
                mjpegWriter.writeLast(outputStream);
                synchronized (mjpegWriter) {
                    mjpegWriter.wait();
                }
            } catch (IOException | InterruptedException exception) {
                mjpegScreenStreamingService.stopMjpegStream(mjpegWriter);
            }
        }
    }
}

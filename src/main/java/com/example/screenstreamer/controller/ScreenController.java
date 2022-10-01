package com.example.screenstreamer.controller;

import com.example.screenstreamer.model.config.ScreenshotSettings;
import com.example.screenstreamer.model.config.SecuritySettings;
import com.example.screenstreamer.model.dto.ScreenDto;
import com.example.screenstreamer.service.MjpegScreenStreamingService;
import com.example.screenstreamer.service.ScreenService;
import com.example.screenstreamer.service.SecurityService;
import com.example.screenstreamer.util.video.mjpeg.MjpegWriter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/api/screen")
public class ScreenController {
    private final ScreenService screenService;
    private final MjpegScreenStreamingService mjpegScreenStreamingService;
    private final SecurityService securityService;

    public ScreenController(
            ScreenService screenService,
            MjpegScreenStreamingService mjpegScreenStreamingService,
            SecurityService securityService) {
        this.screenService = screenService;
        this.mjpegScreenStreamingService = mjpegScreenStreamingService;
        this.securityService = securityService;
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
    public void getScreenStreamMjpeg(
            @RequestParam(required = false) String password,
            HttpServletResponse response) throws IOException {
        securityService.checkPassword(password, SecuritySettings::isSecureView);

        var mjpegWriter = mjpegScreenStreamingService.startMjpegStream();

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

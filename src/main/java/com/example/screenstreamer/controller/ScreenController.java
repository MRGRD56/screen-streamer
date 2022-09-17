package com.example.screenstreamer.controller;

import com.example.screenstreamer.model.config.ScreenshotSettings;
import com.example.screenstreamer.model.dto.ScreenDto;
import com.example.screenstreamer.service.ScreenService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/screen")
public class ScreenController {
    private final ScreenService screenService;

    public ScreenController(ScreenService screenService) {
        this.screenService = screenService;
    }

    @GetMapping(
            value = "screenshot",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public byte[] getScreenshot(
            @RequestParam(name = "screen", defaultValue = "0") int screenIndex,
            @RequestParam(required = false) Float quality,
            @RequestParam(required = false) Float sizeMultiplier) {
        return screenService.getScreenshotAsBytes(new ScreenshotSettings(screenIndex, quality, sizeMultiplier));
    }

    @GetMapping("screens")
    public ScreenDto[] getScreens() {
        return screenService.getScreensDto();
    }
}

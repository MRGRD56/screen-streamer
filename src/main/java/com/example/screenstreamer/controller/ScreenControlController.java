package com.example.screenstreamer.controller;

import com.example.screenstreamer.model.MouseButton;
import com.example.screenstreamer.model.config.ScreenCaptureSettings;
import com.example.screenstreamer.model.dto.MouseClickDto;
import com.example.screenstreamer.service.CursorService;
import com.example.screenstreamer.service.ScreenPositionService;
import com.example.screenstreamer.service.ScreenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Objects;

@RestController
@RequestMapping("/api/screen-control")
public class ScreenControlController {
    private static final String PASSWORD_HEADER = "X-Password";
    private final CursorService cursorService;
    private final ScreenPositionService screenPositionService;
    private final ScreenCaptureSettings screenCaptureSettings;
    private final ScreenService screenService;

    public ScreenControlController(
            CursorService cursorService,
            ScreenPositionService screenPositionService,
            ScreenCaptureSettings screenCaptureSettings,
            ScreenService screenService) {
        this.cursorService = cursorService;
        this.screenPositionService = screenPositionService;
        this.screenCaptureSettings = screenCaptureSettings;
        this.screenService = screenService;
    }

    @Value("${control-password}")
    private String validPassword;

    @PostMapping("mouse/click")
    public void mouseClick(
            @RequestHeader(PASSWORD_HEADER) String password,
            @RequestBody MouseClickDto body) {
        checkPassword(password);

        var device = screenService.getScreen(screenCaptureSettings.getScreen());
        var screenPosition = screenPositionService.getPositionOnScreenFromPercentage(device, body.getX(), body.getY());
        var absolutePosition = screenPositionService.getAbsolutePosition(device, screenPosition);

//        var previousPosition = cursorService.getPointerInfo();
        cursorService.moveCursor(absolutePosition);
        cursorService.click(body.getMouseButton());
//        cursorService.moveCursor(previousPosition);
    }

    private void checkPassword(String password) {
        if (validPassword != null && !Objects.equals(password, validPassword)) {
            throw new HttpStatusCodeException(HttpStatus.BAD_REQUEST) {
                @Override
                public HttpStatus getStatusCode() {
                    return HttpStatus.BAD_REQUEST;
                }
            };
        }
    }
}

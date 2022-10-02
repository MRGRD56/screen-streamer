package com.example.screenstreamer.controller;

import com.example.screenstreamer.model.config.ScreenCaptureSettings;
import com.example.screenstreamer.model.config.SecuritySettings;
import com.example.screenstreamer.model.dto.KeyboardKeyDto;
import com.example.screenstreamer.model.dto.MousePositionDto;
import com.example.screenstreamer.model.dto.MousePositionedClickDto;
import com.example.screenstreamer.model.dto.MouseScrollDto;
import com.example.screenstreamer.service.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/screen-control")
public class ScreenControlController {

    private final MouseService mouseService;
    private final ScreenPositionService screenPositionService;
    private final ScreenCaptureSettings screenCaptureSettings;
    private final ScreenService screenService;
    private final SecurityService securityService;
    private final KeyboardService keyboardService;

    public ScreenControlController(
            MouseService mouseService,
            ScreenPositionService screenPositionService,
            ScreenCaptureSettings screenCaptureSettings,
            ScreenService screenService,
            SecurityService securityService,
            KeyboardService keyboardService) {
        this.mouseService = mouseService;
        this.screenPositionService = screenPositionService;
        this.screenCaptureSettings = screenCaptureSettings;
        this.screenService = screenService;
        this.securityService = securityService;
        this.keyboardService = keyboardService;
    }

    @Deprecated
    @PostMapping("mouse/click")
    public void mouseClick(
            @RequestHeader(name = SecuritySettings.PASSWORD_HEADER, required = false) String password,
            @RequestBody MousePositionedClickDto body) {
        securityService.checkPassword(password, SecuritySettings::isSecureControl);

        var device = screenService.getScreen(screenCaptureSettings.getScreen());
        var absolutePosition = screenPositionService.getAbsolutePositionFromScreenPercentage(device, body.getX(), body.getY());

//        var previousPosition = cursorService.getPointerInfo();
        mouseService.moveCursor(absolutePosition);
        mouseService.click(body.getMouseButton());
//        cursorService.moveCursor(previousPosition);
    }

    @PostMapping("mouse/press")
    public void mousePress(
            @RequestHeader(name = SecuritySettings.PASSWORD_HEADER, required = false) String password,
            @RequestBody MousePositionedClickDto body) {
        securityService.checkPassword(password, SecuritySettings::isSecureControl);

        var device = screenService.getScreen(screenCaptureSettings.getScreen());
        var absolutePosition = screenPositionService.getAbsolutePositionFromScreenPercentage(device, body.getX(), body.getY());

        mouseService.moveCursor(absolutePosition);
        mouseService.press(body.getMouseButton());
    }

    @PostMapping("mouse/release")
    public void mouseRelease(
            @RequestHeader(name = SecuritySettings.PASSWORD_HEADER, required = false) String password,
            @RequestBody MousePositionedClickDto body) {
        securityService.checkPassword(password, SecuritySettings::isSecureControl);

        var device = screenService.getScreen(screenCaptureSettings.getScreen());
        var absolutePosition = screenPositionService.getAbsolutePositionFromScreenPercentage(device, body.getX(), body.getY());

        mouseService.moveCursor(absolutePosition);
        mouseService.release(body.getMouseButton());
    }

    @PostMapping("mouse/move")
    public void mouseMove(
            @RequestHeader(name = SecuritySettings.PASSWORD_HEADER, required = false) String password,
            @RequestBody MousePositionDto body) {
        securityService.checkPassword(password, SecuritySettings::isSecureControl);

        var device = screenService.getScreen(screenCaptureSettings.getScreen());
        var absolutePosition = screenPositionService.getAbsolutePositionFromScreenPercentage(device, body.getX(), body.getY());

        mouseService.moveCursor(absolutePosition);
    }

    @PostMapping("mouse/scroll")
    public void mouseScroll(
            @RequestHeader(name = SecuritySettings.PASSWORD_HEADER, required = false) String password,
            @RequestBody MouseScrollDto body) {
        securityService.checkPassword(password, SecuritySettings::isSecureControl);

        mouseService.scroll(body.getDeltaY());
    }

    @PostMapping("keyboard/key-down")
    public void keyboardKeyDown(
            @RequestHeader(name = SecuritySettings.PASSWORD_HEADER, required = false) String password,
            @RequestBody KeyboardKeyDto body) {
        securityService.checkPassword(password, SecuritySettings::isSecureControl);

        keyboardService.keyDown(body.getKeycode());
    }

    @PostMapping("keyboard/key-up")
    public void keyboardKeyUp(
            @RequestHeader(name = SecuritySettings.PASSWORD_HEADER, required = false) String password,
            @RequestBody KeyboardKeyDto body) {
        securityService.checkPassword(password, SecuritySettings::isSecureControl);

        keyboardService.keyUp(body.getKeycode());
    }
}

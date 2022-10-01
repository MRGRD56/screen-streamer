package com.example.screenstreamer.controller;

import com.example.screenstreamer.model.config.ScreenCaptureSettings;
import com.example.screenstreamer.model.config.SecuritySettings;
import com.example.screenstreamer.model.dto.MouseClickDto;
import com.example.screenstreamer.model.dto.MouseScrollDto;
import com.example.screenstreamer.service.CursorService;
import com.example.screenstreamer.service.ScreenPositionService;
import com.example.screenstreamer.service.ScreenService;
import com.example.screenstreamer.service.SecurityService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/screen-control")
public class ScreenControlController {

    private final CursorService cursorService;
    private final ScreenPositionService screenPositionService;
    private final ScreenCaptureSettings screenCaptureSettings;
    private final ScreenService screenService;
    private final SecurityService securityService;

    public ScreenControlController(
            CursorService cursorService,
            ScreenPositionService screenPositionService,
            ScreenCaptureSettings screenCaptureSettings,
            ScreenService screenService,
            SecurityService securityService) {
        this.cursorService = cursorService;
        this.screenPositionService = screenPositionService;
        this.screenCaptureSettings = screenCaptureSettings;
        this.screenService = screenService;
        this.securityService = securityService;
    }

    @PostMapping("mouse/click")
    public void mouseClick(
            @RequestHeader(name = SecuritySettings.PASSWORD_HEADER, required = false) String password,
            @RequestBody MouseClickDto body) {
        securityService.checkPassword(password, SecuritySettings::isSecureControl);

        var device = screenService.getScreen(screenCaptureSettings.getScreen());
        var screenPosition = screenPositionService.getPositionOnScreenFromPercentage(device, body.getX(), body.getY());
        var absolutePosition = screenPositionService.getAbsolutePosition(device, screenPosition);

//        var previousPosition = cursorService.getPointerInfo();
        cursorService.moveCursor(absolutePosition);
        cursorService.click(body.getMouseButton());
//        cursorService.moveCursor(previousPosition);
    }

    @PostMapping("mouse/scroll")
    public void mouseScroll(
            @RequestHeader(name = SecuritySettings.PASSWORD_HEADER, required = false) String password,
            @RequestBody MouseScrollDto body) {
        securityService.checkPassword(password, SecuritySettings::isSecureControl);

        cursorService.scroll(body.getDeltaY());
    }
}

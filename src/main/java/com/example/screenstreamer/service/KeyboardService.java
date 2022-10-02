package com.example.screenstreamer.service;

import com.example.screenstreamer.util.RobotFactory;
import org.springframework.stereotype.Service;

import java.awt.*;

@Service
public class KeyboardService {
    private final Robot robot = RobotFactory.create();

    public void keyDown(int keycode) {
        robot.keyPress(keycode);
    }

    public void keyUp(int keycode) {
        robot.keyRelease(keycode);
    }
}

package com.example.screenstreamer.model;

import java.awt.event.InputEvent;

public enum MouseButton {
    LEFT(InputEvent.BUTTON1_DOWN_MASK),
    RIGHT(InputEvent.BUTTON3_DOWN_MASK),
    MIDDLE(InputEvent.BUTTON2_DOWN_MASK);

    private final int code;

    MouseButton(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}

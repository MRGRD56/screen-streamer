package com.example.screenstreamer.model.dto;

import com.example.screenstreamer.model.MouseButton;

public class MouseButtonDto implements IMouseButtonClickDto {
    private MouseButton mouseButton;

    @Override
    public MouseButton getMouseButton() {
        return mouseButton;
    }

    @Override
    public void setMouseButton(MouseButton mouseButton) {
        this.mouseButton = mouseButton;
    }
}

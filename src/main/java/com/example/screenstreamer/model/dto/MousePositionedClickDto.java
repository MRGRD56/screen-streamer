package com.example.screenstreamer.model.dto;

import com.example.screenstreamer.model.MouseButton;

public class MousePositionedClickDto implements IMouseButtonClickDto, IMousePositionDto {
    private double x;
    private double y;
    private MouseButton mouseButton;

    @Override
    public double getX() {
        return x;
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public MouseButton getMouseButton() {
        return mouseButton;
    }

    @Override
    public void setMouseButton(MouseButton mouseButton) {
        this.mouseButton = mouseButton;
    }
}

package com.example.screenstreamer.model.dto;

import com.example.screenstreamer.model.MouseButton;

public class MouseClickDto {
    private double x;
    private double y;
    private MouseButton mouseButton;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public MouseButton getMouseButton() {
        return mouseButton;
    }

    public void setMouseButton(MouseButton mouseButton) {
        this.mouseButton = mouseButton;
    }
}

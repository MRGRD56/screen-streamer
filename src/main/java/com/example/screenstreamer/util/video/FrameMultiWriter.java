package com.example.screenstreamer.util.video;

public interface FrameMultiWriter<T extends FrameWriter> extends FrameWriter {
    void addWriter(T writer);

    void removeWriter(T writer);

    boolean hasWriters();
}

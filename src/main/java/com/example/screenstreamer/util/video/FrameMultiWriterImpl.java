package com.example.screenstreamer.util.video;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class FrameMultiWriterImpl<T extends FrameWriter> implements FrameMultiWriter<T> {
    private final List<T> writers = Collections.synchronizedList(new ArrayList<>());

    public void addWriter(T mjpegWriter) {
        writers.add(mjpegWriter);
    }

    public void removeWriter(T mjpegWriter) {
        writers.remove(mjpegWriter);
    }

    @Override
    public void apply(byte[] image) {
        writers.forEach(writer -> writer.apply(image));
    }

    @Override
    public void applyAll(Collection<byte[]> images) {
        writers.forEach(writer -> writer.applyAll(images));
    }

    @Override
    public boolean hasWriters() {
        return !writers.isEmpty();
    }
}

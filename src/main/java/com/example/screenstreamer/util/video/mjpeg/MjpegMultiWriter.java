package com.example.screenstreamer.util.video.mjpeg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MjpegMultiWriter implements MjpegAppliable {
    private final List<MjpegWriter> writers = Collections.synchronizedList(new ArrayList<>());

    public void addWriter(MjpegWriter mjpegWriter) {
        writers.add(mjpegWriter);
    }

    public void removeWriter(MjpegWriter mjpegWriter) {
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
}

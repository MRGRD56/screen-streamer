package com.example.screenstreamer.util.video.mjpeg;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedList;

public class MjpegWriter implements MjpegAppliable {
    private static final String NL = "\r\n";
    public static final String BOUNDARY = "--boundary";
    private static final String HEAD = BOUNDARY + NL +
                                       "Content-Type: image/jpeg" + NL +
                                       "Content-Length: ";

    private final LinkedList<byte[]> images = new LinkedList<>();

    public void apply(byte[] image) {
        this.images.add(image);
        synchronized (this) {
            notify();
        }
    }

    public void applyAll(Collection<byte[]> images) {
        this.images.addAll(images);
        synchronized (this) {
            notify();
        }
    }

    public void writeAll(OutputStream outputStream) throws IOException {
        byte[] image;
        while ((image = images.poll()) != null) {
            write(outputStream, image);
        }
    }

    public void writeLast(OutputStream outputStream) throws IOException {
        var lastImage = images.pollLast();
        if (lastImage != null) {
            write(outputStream, lastImage);
            images.clear();
        }
    }

    private void write(OutputStream outputStream, byte[] image) throws IOException {
        outputStream.write((HEAD + image.length + NL + NL).getBytes());
        outputStream.write(image);
        outputStream.write((NL + NL).getBytes());
        outputStream.flush();
    }
}

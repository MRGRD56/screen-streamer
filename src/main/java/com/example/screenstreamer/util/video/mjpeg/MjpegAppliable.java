package com.example.screenstreamer.util.video.mjpeg;

import java.util.Collection;

public interface MjpegAppliable {
    void apply(byte[] image);

    void applyAll(Collection<byte[]> images);
}

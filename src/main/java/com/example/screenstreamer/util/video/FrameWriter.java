package com.example.screenstreamer.util.video;

import java.util.Collection;

public interface FrameWriter {
    void apply(byte[] image);

    void applyAll(Collection<byte[]> images);
}

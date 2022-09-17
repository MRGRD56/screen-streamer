package com.example.screenstreamer.service;

import com.example.screenstreamer.model.config.ScreenCaptureSettings;
import io.humble.video.*;
import io.humble.video.awt.MediaPictureConverter;
import io.humble.video.awt.MediaPictureConverterFactory;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ScreenRecorder implements AutoCloseable {
    private final static Codec.ID CODEC = Codec.ID.CODEC_ID_H264;
    private final static PixelFormat.Type PIXEL_FORMAT = PixelFormat.Type.PIX_FMT_YUV420P;
    private final static int IMAGE_TYPE = BufferedImage.TYPE_3BYTE_BGR;

    private final String filename;
    private final ScreenCaptureSettings captureSettings;
    private final Muxer muxer;
    private final Rational rational;
    private final ScreenService screenService;
    private final ImageService imageService;
    private final Codec codec;
    private final Encoder encoder;

    private final ExecutorService mainExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService frameExecutor = new ThreadPoolExecutor(
            5, 5, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(1));

    private volatile boolean isRecording = false;

    public ScreenRecorder(
            String filename,
            ScreenCaptureSettings captureSettings,
            ScreenService screenService,
            ImageService imageService) {
        this.filename = filename;
        this.captureSettings = captureSettings;
        this.screenService = screenService;
        this.imageService = imageService;

        initializeFile();

        this.muxer = Muxer.make(filename, null, null);
        this.rational = Rational.make(1, captureSettings.getFps());
        this.codec = Codec.findEncodingCodec(CODEC);
        this.encoder = createEncoder();
    }

    private void initializeFile() {
        var file = new File(filename);
        var directory = file.getParentFile();
        if (directory != null && !directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + directory);
        }
    }

    private Encoder createEncoder() {
        var encoder = Encoder.make(codec);

        var screen = screenService.getScreen(captureSettings.getScreen());
        var screenBounds = screen.getDefaultConfiguration().getBounds();
        var screenSize = imageService.getScaledSize(screenBounds.getSize(), captureSettings.getSizeMultiplier());
        encoder.setWidth(screenSize.width);
        encoder.setHeight(screenSize.height);
        encoder.setPixelFormat(PIXEL_FORMAT);
        encoder.setTimeBase(rational);
        if (muxer.getFormat().getFlag(MuxerFormat.Flag.GLOBAL_HEADER)) {
            encoder.setFlag(Encoder.Flag.FLAG_GLOBAL_HEADER, true);
        }

        return encoder;
    }

    private BufferedImage createFrame() {
        var sourceImage = screenService.getScreenshotAsImage(captureSettings);
        return imageService.convertToType(sourceImage, IMAGE_TYPE);
    }

    public void startRecording() {
        if (isRecording) {
            return;
        }

        isRecording = true;

        var quality = Math.round(captureSettings.getQuality() * 100);

        encoder.open(null, null);
        var muxerStream = muxer.addNewStream(encoder);
        try {
            muxer.open(null, null);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        mainExecutor.submit(() -> {
            final AtomicReference<MediaPictureConverter> converter = new AtomicReference<>(null);
            final MediaPicture picture = MediaPicture.make(
                    encoder.getWidth(),
                    encoder.getHeight(),
                    PIXEL_FORMAT);
            picture.setQuality(quality);
            picture.setTimeBase(rational);

            var futures = new ArrayList<Future<?>>();

            final MediaPacket packet = MediaPacket.make();
            for (var i = 0; isRecording; i++) {
                final int frameIndex = i;
                try {
                    futures.add(frameExecutor.submit(() -> {
                        if (!isRecording) return;

                        BufferedImage frame = createFrame();

                        if (!isRecording) return;

                        if (converter.get() == null) {
                            converter.set(MediaPictureConverterFactory.createConverter(frame, picture));
                        }
                        converter.get().toPicture(picture, frame, frameIndex);

                        if (!isRecording) return;

                        do {
                            if (!isRecording) return;

                            encoder.encode(packet, picture);
                            if (packet.isComplete()) {
                                muxer.write(packet, false);
                            }
                        } while (packet.isComplete());
                    }));
                } catch (RejectedExecutionException e) {
                }

                try {
                    Thread.sleep((long) (1000 * rational.getDouble()));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            do {
                encoder.encode(packet, null);
                if (packet.isComplete()) {
                    muxer.write(packet, false);
                }
            } while (packet.isComplete());

            encoder.delete();
            muxer.close();
            muxer.delete();
            muxerStream.delete();
        });
    }

    public void stopRecording() {
        isRecording = false;
    }

    @Override
    public void close() {
        stopRecording();
    }
}
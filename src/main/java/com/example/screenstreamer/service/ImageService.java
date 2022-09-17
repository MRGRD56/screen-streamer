package com.example.screenstreamer.service;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class ImageService {
    public void compressJpeg(ImageOutputStream outputStream, BufferedImage image, Float imageQuality) {
        var jpegWriter = ImageIO.getImageWritersByFormatName("jpg").next();
        var jpegWriteParam = jpegWriter.getDefaultWriteParam();
        if (imageQuality != null) {
            jpegWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpegWriteParam.setCompressionQuality(imageQuality);
        }

        jpegWriter.setOutput(outputStream);
        var outputImage = new IIOImage(image, null, null);
        try {
            jpegWriter.write(null, outputImage, jpegWriteParam);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            jpegWriter.dispose();
        }
    }

    public byte[] compressJpegAsBytes(BufferedImage image, Float imageQuality) {
        var baOutputStream = new ByteArrayOutputStream();
        var imageOutputStream = new MemoryCacheImageOutputStream(baOutputStream);
        compressJpeg(imageOutputStream, image, imageQuality);
        return baOutputStream.toByteArray();
    }

    public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        return Scalr.resize(
                originalImage, Scalr.Method.BALANCED, Scalr.Mode.AUTOMATIC, targetWidth, targetHeight, Scalr.OP_ANTIALIAS);
    }

    public BufferedImage resizeImage(BufferedImage originalImage, float sizeMultiplier) {
        if (sizeMultiplier == 1) {
            return originalImage;
        }

        var scaledSize = getScaledSize(new Dimension(originalImage.getWidth(), originalImage.getHeight()), sizeMultiplier);

        return resizeImage(originalImage, scaledSize.width, scaledSize.height);
    }

    public Dimension getScaledSize(Dimension originalSize, Float multiplier) {
        if (multiplier == null) {
            return originalSize;
        }

        return new Dimension(
                Math.round(originalSize.width * multiplier),
                Math.round(originalSize.height * multiplier));
    }

    public BufferedImage convertToType(BufferedImage sourceImage,
                                              int targetType) {
        BufferedImage image;

        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        }

        else {
            image = new BufferedImage(sourceImage.getWidth(),
                    sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }

        return image;
    }
}

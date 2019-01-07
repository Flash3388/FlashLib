package edu.flash3388.flashlib.cam.jpeg.reader;

import edu.flash3388.flashlib.cam.jpeg.JpegImage;
import edu.flash3388.flashlib.cam.jpeg.MjpegFormat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MjpegReader implements JpegReader {

    private final InputStream mInputStream;

    public MjpegReader(InputStream inputStream) {
        mInputStream = inputStream;
    }

    @Override
    public JpegImage read() throws IOException {
        byte[] imageBytes = readNextImageData();
        return JpegImage.fromBytes(imageBytes);
    }

    private byte[] readNextImageData() throws IOException {
        int contentLength = readContentLength();

        byte[] data = new byte[contentLength];
        System.arraycopy(MjpegFormat.SOI_MARKER, 0, data, 0, MjpegFormat.SOI_MARKER.length);
        mInputStream.read(data, MjpegFormat.SOI_MARKER.length, data.length - MjpegFormat.SOI_MARKER.length);

        return data;
    }

    private int readContentLength() throws IOException {
        byte[] header = readUntil(MjpegFormat.SOI_MARKER);

        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(header));
        return Integer.parseInt(properties.getProperty(MjpegFormat.CONTENT_LENGTH));
    }

    private byte[] readUntil(byte[] sequence) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int sequencePosition = 0;

        byte current;
        while ((current = (byte) mInputStream.read()) != 0) {
            outputStream.write(current);

            if (current == sequence[sequencePosition]) {
                sequencePosition++;

                if (sequencePosition == sequence.length) {
                    break;
                }
            } else {
                sequencePosition = 0;
            }
        }

        return outputStream.toByteArray();
    }
}

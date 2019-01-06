package edu.flash3388.flashlib.cam.jpeg.reader;

import edu.flash3388.flashlib.cam.jpeg.JpegImage;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class HttpJpegReader implements JpegReader {

    private static final String CONTENT_LENGTH = "Content-Length: ";

    private final InputStream mInputStream;

    public HttpJpegReader(InputStream inputStream) {
        mInputStream = inputStream;
    }

    @Override
    public JpegImage read() throws IOException {
        byte[] imageBytes = readNextImageData();
        return JpegImage.fromBytes(imageBytes);
    }

    @Override
    public void close() throws IOException {
        mInputStream.close();
    }

    private byte[] readNextImageData() throws IOException {
        int currByte = -1;

        String header = null;
        // build headers
        // the DCS-930L stops it's headers

        boolean captureContentLength = false;
        StringWriter contentLengthStringWriter = new StringWriter(128);
        StringWriter headerWriter = new StringWriter(128);

        int contentLength = 0;

        while ((currByte = mInputStream.read()) > -1) {
            if (captureContentLength) {
                if (currByte == 10 || currByte == 13) {
                    contentLength = Integer.parseInt(contentLengthStringWriter.toString());
                    break;
                }
                contentLengthStringWriter.write(currByte);

            } else {
                headerWriter.write(currByte);
                String tempString = headerWriter.toString();
                int indexOf = tempString.indexOf(CONTENT_LENGTH);
                if (indexOf > 0) {
                    captureContentLength = true;
                }
            }
        }

        // 255 indicates the start of the jpeg image
        while ((mInputStream.read()) != 255) {
            // just skip extras
        }

        // rest is the buffer
        byte[] imageBytes = new byte[contentLength + 1];
        // since we ate the original 255 , shove it back in
        imageBytes[0] = (byte) 255;
        int offset = 1;
        int numRead = 0;
        while (offset < imageBytes.length
                && (numRead = mInputStream.read(imageBytes, offset, imageBytes.length - offset)) >= 0) {
            offset += numRead;
        }

        return imageBytes;
    }
}

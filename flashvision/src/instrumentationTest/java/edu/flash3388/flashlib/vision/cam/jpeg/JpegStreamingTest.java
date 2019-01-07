package edu.flash3388.flashlib.vision.cam.jpeg;

import edu.flash3388.flashlib.cam.jpeg.JpegCamera;
import edu.flash3388.flashlib.cam.jpeg.JpegImage;
import edu.flash3388.flashlib.cam.jpeg.client.MjpegClient;
import edu.flash3388.flashlib.cam.jpeg.server.MjpegServer;
import edu.flash3388.flashlib.io.Closer;
import edu.flash3388.flashlib.time.Clock;
import edu.flash3388.flashlib.time.JavaNanoClock;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class JpegStreamingTest {

    private static final int SERVER_PORT = 10001;
    private static final String CAMERA_NAME = "test";
    private static final String CAMERA_CLIENT_URL_FORMAT = "http://localhost:%d/%s";

    private Clock mClock;
    private Logger mLogger;

    @Before
    public void setUp() throws Exception {
        mClock = new JavaNanoClock();
        mLogger = mock(Logger.class);
    }

    @Test
    public void serverAndClientAreUp_imagesAreStreaming_imageTransferredSuccessfully() throws Exception {
        final JpegImage IMAGE = new JpegImage(new BufferedImage(255, 255, BufferedImage.TYPE_3BYTE_BGR));
        final JpegCamera CAMERA = new StaticImageJpegCamera(IMAGE);

        Closer closer = Closer.empty();
        List<JpegImage> resultImages = new ArrayList<>();

        try {
            MjpegServer mjpegServer = MjpegServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), SERVER_PORT), 1, mClock, mLogger);

            mjpegServer.setCamera(CAMERA_NAME, CAMERA);

            closer.add(mjpegServer);
            mjpegServer.start();

            MjpegClient mjpegClient = MjpegClient.create(new URL(String.format(CAMERA_CLIENT_URL_FORMAT, SERVER_PORT, CAMERA_NAME)), mLogger);

            CountDownLatch countDownLatch = new CountDownLatch(1);

            closer.add(mjpegClient);
            mjpegClient.start((image) -> {
                resultImages.add(image);
                countDownLatch.countDown();
            });

            countDownLatch.await();
        } finally {
            closer.close();
        }

        List<JpegImage> imageSnapshot = new ArrayList<>(resultImages);

        assertTrue(imageSnapshot.size() > 0);
        assertArrayEquals(IMAGE.toByteArray(), imageSnapshot.get(0).toByteArray());
    }
}

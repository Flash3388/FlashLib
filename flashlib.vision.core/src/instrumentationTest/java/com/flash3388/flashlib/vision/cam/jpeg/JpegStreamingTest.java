package com.flash3388.flashlib.vision.cam.jpeg;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.SystemNanoClock;
import com.flash3388.flashlib.vision.Camera;
import com.flash3388.flashlib.vision.Image;
import com.flash3388.flashlib.vision.jpeg.JpegImage;
import com.flash3388.flashlib.vision.jpeg.client.MjpegClient;
import com.flash3388.flashlib.vision.jpeg.server.MjpegServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.mock;

public class JpegStreamingTest {

    private static final int MIN_SERVER_PORT = 10001;
    private static final int MAX_SERVER_PORT = 10010;

    private static final String CAMERA_NAME = "test";
    private static final String CAMERA_CLIENT_URL_FORMAT = "http://localhost:%d/%s";

    private Clock mClock;
    private Logger mLogger;

    @BeforeEach
    public void setUp() throws Exception {
        mClock = new SystemNanoClock();
        mLogger = mock(Logger.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void serverAndClientAreUp_imagesAreStreaming_imageTransferredSuccessfully() throws Exception {
        final JpegImage IMAGE = new JpegImage(new BufferedImage(255, 255, BufferedImage.TYPE_3BYTE_BGR));
        final Camera CAMERA = new StaticImageCamera(IMAGE);

        List<Image> resultImages = new ArrayList<>();

        Server server = createServer();
        MjpegServer mjpegServer = server.mMjpegServer;
        try {
            mjpegServer.setCamera(CAMERA_NAME, CAMERA, CAMERA.getFps());
            mjpegServer.start();

            CountDownLatch imageLatch = new CountDownLatch(1);
            MjpegClient mjpegClient = MjpegClient.create(
                    new URL(String.format(CAMERA_CLIENT_URL_FORMAT, server.mPort, CAMERA_NAME)),
                    (image) -> {
                        resultImages.add(image);
                        imageLatch.countDown();
                    },
                    mLogger);
            try {
                mjpegClient.start();
                imageLatch.await(1, TimeUnit.MINUTES);
            } finally {
                mjpegClient.stop();
            }
        } finally {
            mjpegServer.stop();
        }

        List<Image> imageSnapshot = new ArrayList<>(resultImages);

        assertThat(imageSnapshot, not(empty()));
        assertArrayEquals(IMAGE.getRaw(), imageSnapshot.get(0).getRaw());
    }

    private Server createServer() throws IOException {
        for (int port : generatePorts()) {
            try {
                MjpegServer server = MjpegServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 1, mClock);

                Server serverWrapper = new Server();
                serverWrapper.mMjpegServer = server;
                serverWrapper.mPort = port;

                return serverWrapper;
            } catch (BindException e) {
                // let's try again
                e.printStackTrace();
            }
        }

        throw new Error("Unable to find an available port");
    }

    private Collection<Integer> generatePorts() {
        return IntStream.range(MIN_SERVER_PORT, MAX_SERVER_PORT)
                .boxed()
                .collect(Collectors.toList());
    }

    private static class Server {
        MjpegServer mMjpegServer;
        int mPort;
    }
}

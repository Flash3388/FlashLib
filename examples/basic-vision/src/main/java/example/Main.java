package example;

import com.castle.util.throwables.Throwables;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.DoubleBufferImageHolder;
import com.flash3388.flashlib.vision.Image;
import com.flash3388.flashlib.vision.ImageSource;
import com.flash3388.flashlib.vision.jpeg.JpegImage;
import com.flash3388.flashlib.vision.processing.VisionPipeline;

import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        try {
            ImageSource<Image> imageSource = ImageSource.of(

            );
            DoubleBufferImageHolder<Image> imageHolder = new DoubleBufferImageHolder<>();

            Future<?> future = imageSource.asyncPollAtFixedRate(executorService,
                    Time.milliseconds(50),
                    imageHolder.divergeTo(new VisionPipeline.Builder<>()
                                    .process((img) -> img)
                                    .analyse((img)-> Optional.empty())
                                    .analysisTo(System.out::println)
                                    .build()),
                    Throwables.silentHandler());

            Thread.sleep(1000);
            future.cancel(true);
        } finally {
            executorService.shutdownNow();
        }
    }

    private static Image loadImage(Path path) throws IOException {
        try (InputStream inputStream = new FileInputStream(path.toFile())) {
            return new JpegImage(ImageIO.read(inputStream));
        }
    }
}

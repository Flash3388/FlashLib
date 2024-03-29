package example;

import com.flash3388.flashlib.time.SystemMillisClock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.Image;
import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.Source;
import com.flash3388.flashlib.vision.SourcePollingObserver;
import com.flash3388.flashlib.vision.analysis.Analysis;
import com.flash3388.flashlib.vision.jpeg.JpegImage;
import com.flash3388.flashlib.vision.processing.VisionPipeline;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {
        if (args.length < 1) {
            System.err.println("Expected argument: path to folder with images");
            System.exit(1);
        }
        Path imageFolderPath = Paths.get(args[0]);

        // we'll use a set of premade images from some folder
        // this "static" ImageSource will provide the images one-by-one when polled.
        Source<Image> source = Source.of(loadImages(imageFolderPath));
        // we need something to run the actual polling action and vision code
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        try {
            // let's set up the window, with a single label in the center where we will set the icon.
            JFrame frame = createWindow();
            JLabel lbl = createImageHolder(frame);
            frame.setVisible(true);

            // this pipeline will be used to set the image to the gui from the vision thread
            Pipeline<Image> guiPipeline = (image)-> {
                SwingUtilities.invokeLater(()-> {
                    System.out.println(image);
                    lbl.setIcon(new ImageIcon(image.toAwt()));
                });
            };

            // lets config and start the vision code
            Future<?> future = startVisionPipeline(source, guiPipeline, executorService);

            // when the window is closed, we should stop the vision code
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    future.cancel(true);
                }
            });

            // let's block the main thread until vision finishes
            future.get();
        } finally {
            executorService.shutdownNow();
        }
    }

    private static Future<?> startVisionPipeline(Source<Image> source, Pipeline<Image> guiPipeline,
                                                 ScheduledExecutorService executorService) {
        // we'll poll images from imageSource with executeService
        return source.asyncPollAtFixedRate(
                executorService,
                Time.milliseconds(1000), // polling rate
                guiPipeline.divergeTo(new VisionPipeline.Builder<>()
                        .process((img) -> {
                            // here we can process the image, modify it, extract features, etc.
                            // return the result image to be used by the next processor or by the analyzer;
                            return img;
                        })
                        .analyse((original, postProcess) -> {
                            // analyze the image for the information we want
                            // and return an analysis, if possible
                            return Optional.of(Analysis.builder()
                                    .put("somedatakey", "some data")
                                    .build());
                        })
                        .analysisTo((analysis) -> {
                            // the analysis will be transferred here to where we want
                            // some output destination, like the robot or some log

                            //noinspection Convert2MethodRef
                            System.out.println(analysis);
                        })
                        .build()),
                // tracker for execution of image polling
                new SourcePollingObserver() {
                    @Override
                    public void onStartProcess() {

                    }

                    @Override
                    public void onEndProcess(Time runTime) {

                    }

                    @Override
                    public void onErroredProcess(Throwable t) {

                    }
                },
                new SystemMillisClock()
        );
    }

    private static Collection<Image> loadImages(Path folder) throws IOException {
        Collection<Image> images = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
            for (Path path : stream) {
                images.add(loadImage(path));
            }
        }

        return images;
    }

    private static Image loadImage(Path path) throws IOException {
        try (InputStream inputStream = new FileInputStream(path.toFile())) {
            return new JpegImage(ImageIO.read(inputStream));
        }
    }

    private static JFrame createWindow() {
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        return frame;
    }

    private static JLabel createImageHolder(JFrame frame) {
        JLabel lbl = new JLabel();
        lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        lbl.setSize(500, 500);
        lbl.setVisible(true);

        frame.add(lbl);

        return lbl;
    }
}

package example;

import com.castle.util.throwables.Throwables;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.Camera;
import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.Source;
import com.flash3388.flashlib.vision.cv.CvCamera;
import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.cv.processing.disFolderIsNasty.HsvRangeProcessor;
import com.flash3388.flashlib.vision.processing.VisionPipeline;
import com.flash3388.flashlib.vision.processing.color.ColorRange;
import com.flash3388.flashlib.vision.processing.color.HsvColorSettings;
import org.opencv.core.Core;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class Main {

    public static void main(String[] args) throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // we need something to run the actual polling action and vision code
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        // we'll use a camera, interfaced using OpenCv
        try (Camera<CvImage> camera = new CvCamera(0)) {
            // let's set up the window, with a single label in the center where we will set the icon.
            JFrame frame = createWindow();
            JLabel lbl = createImageHolder(frame);
            frame.setVisible(true);

            // this pipeline will be used to set the image to the gui from the vision thread
            Pipeline<CvImage> guiPipeline = (image)-> {
                SwingUtilities.invokeLater(()-> {
                    System.out.println(image);
                    lbl.setIcon(new ImageIcon(image.toAwt()));
                });
            };

            // let's config and start the vision code
            Future<?> future = startVisionPipeline(camera, guiPipeline, executorService);

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

    private static Future<?> startVisionPipeline(Source<CvImage> source,
                                                 Pipeline<CvImage> guiPipeline,
                                                 ScheduledExecutorService executorService) {
        CvProcessing cvProcessing = new CvProcessing();

        // we'll poll images from imageSource with executeService
        return source.asyncPollAtFixedRate(executorService,
                Time.milliseconds(1000), // polling rate
                guiPipeline.divergeTo(new VisionPipeline.Builder<CvImage, List<MatOfPoint>>()
                        // we start to define the processors
                        // we will build them in a cascading manner, each processor making a change
                        // and passing to the other

                        // we start with the color processor, which filters for pixels in a specific
                        // range. It will also convert the image to HSV format.
                        // The keepMat (true) argument specifies that the color filtering should
                        // be done in a different matrix and not the same one, thus keeping the original
                        // with the same look.
                        .process(new HsvRangeProcessor(
                                new HsvColorSettings(
                                        new ColorRange(0, 180),
                                        new ColorRange(100, 255),
                                        new ColorRange(105, 255)),
                                cvProcessing, true)
                                // Now we define another processor which will run after the color filter.
                                // This processor will detect contours on the image and return a list of them.
                                .andThen((image)-> cvProcessing.detectContours(image.getMat()))
                        )
                        // After we're finished processing the image, we can draw the contours we found on it.
                        // Usually the analyse phase is used to extract information from the result of the processors.
                        .analyse((image, contours)-> {
                            Imgproc.drawContours(image.getMat(), contours, -1, new Scalar(100, 100, 100));
                            return Optional.empty();
                        })
                        .build()),
                // if there is an error, it will be ignored. Normally not recommended, but it's okay for an example
                Throwables.silentHandler());
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

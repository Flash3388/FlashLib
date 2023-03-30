package example;

import com.castle.util.throwables.Throwables;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.Camera;
import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.Source;
import com.flash3388.flashlib.vision.analysis.Analysis;
import com.flash3388.flashlib.vision.analysis.AnalysisAlgorithms;
import com.flash3388.flashlib.vision.cv.CvCamera;
import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.cv.processing.HsvRangeProcessor;
import com.flash3388.flashlib.vision.cv.processing.RectProcessor;
import com.flash3388.flashlib.vision.cv.processing.Scorable;
import com.flash3388.flashlib.vision.processing.BestProcessor;
import com.flash3388.flashlib.vision.processing.StreamMappingProcessor;
import com.flash3388.flashlib.vision.processing.VisionPipeline;
import com.flash3388.flashlib.vision.processing.color.ColorRange;
import com.flash3388.flashlib.vision.processing.color.HsvColorSettings;
import com.jmath.vectors.Vector2;
import org.opencv.core.Core;
import org.opencv.core.Rect;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

public class Main {

    private static final double TARGET_HEIGHT_TO_WIDTH_RATIO = 42/92.0;
    private static final double REAL_TARGET_WIDTH_CM = 92;
    private static final double MIN_CONTOUR_SIZE = 1000;
    private static final double MIN_SCORE = 0.6;
    private static final double CAM_FOV_RADIANS = 1.229061;

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

    private static Future<?> startVisionPipeline(Source<CvImage> source, Pipeline<CvImage> guiPipeline,
                                                 ScheduledExecutorService executorService) {
        CvProcessing cvProcessing = new CvProcessing();

        // we'll poll images from imageSource with executeService
        return source.asyncPollAtFixedRate(executorService,
                Time.milliseconds(1000), // polling rate
                guiPipeline.divergeTo(new VisionPipeline.Builder<CvImage, Optional<TargetScorable>>()
                        // we start to define the processors
                        // we will build them in a cascading manner, each processor making a change
                        // and passing to the other

                        // we start with the color processor, which filters for pixels in a specific
                        // range. It will also convert the image to HSV format
                        .process(new HsvRangeProcessor(
                                new HsvColorSettings(
                                        new ColorRange(0, 180),
                                        new ColorRange(100, 255),
                                        new ColorRange(105, 255)),
                                cvProcessing)
                                // now we move to the rect processor, which extracts rectangles from remaining
                                // pixel groups, excluding rects which don't pass the limiting predicate (by size)
                                .andThen(new RectProcessor(cvProcessing, (rect)-> rect.area() > MIN_CONTOUR_SIZE)
                                        // next we convert the rects into TargetScorable object, which allow
                                        // scoring of results. TargetScorable is defined as an inner class in this class.
                                        // we filter out scorables with a low score
                                        .andThen(new StreamMappingProcessor<Rect, TargetScorable>(
                                                        (rect) -> new TargetScorable(rect, TARGET_HEIGHT_TO_WIDTH_RATIO),
                                                        (scorable)-> scorable.score() > MIN_SCORE)
                                                // now we find the best scorable (highest score) assuming there are any
                                                .andThen(new BestProcessor<>(Scorable::compareTo))))
                        )
                        // this is the end of the processing phase. from an image input, we will receive a
                        // scorable as an output.
                        // we know need to extract what we want from the scorable into an Analysis object,
                        // which will contain vision-result information
                        .analyse((image, best)-> {
                            if (!best.isPresent()) {
                                return Optional.empty();
                            }

                            TargetScorable scorable = best.get();

                            // take the distance and angle to the found scorable
                            double distance = AnalysisAlgorithms.measureDistance(scorable.getWidth(), image.getWidth(),
                                    REAL_TARGET_WIDTH_CM, CAM_FOV_RADIANS);
                            double angle = AnalysisAlgorithms.calculateHorizontalOffsetDegrees2(scorable.getCenter().x(),
                                    image.getWidth(), CAM_FOV_RADIANS);

                            return Optional.of(Analysis.builder()
                                    .put("distance", distance)
                                    .put("angle", angle)
                                    .build());
                        })
                        // normally we would pass the Analysis to some where important, like the robot
                        // here we simply print it out so we can see it
                        .analysisTo(System.out::println)
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

    private static class TargetScorable implements Scorable {

        private final Rect mRect;
        private final double mExpectedRatio;

        TargetScorable(Rect rect, double expectedRatio) {
            mRect = rect;
            mExpectedRatio = expectedRatio;
        }

        @Override
        public double getWidth() {
            return mRect.width;
        }

        @Override
        public double getHeight() {
            return mRect.height;
        }

        @Override
        public Vector2 getCenter() {
            return new Vector2(mRect.x + mRect.width * 0.5, mRect.y + mRect.height * 0.5);
        }

        @Override
        public double score() {
            double ratio = mRect.height / (double) mRect.width;
            return ratio > mExpectedRatio ? mExpectedRatio / ratio : ratio / mExpectedRatio;
        }
    }
}

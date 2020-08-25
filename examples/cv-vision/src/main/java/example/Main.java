package example;

import com.castle.util.throwables.Throwables;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.Camera;
import com.flash3388.flashlib.vision.ImageSource;
import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.cv.CvCamera;
import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.cv.processing.BestScoreProcessor;
import com.flash3388.flashlib.vision.cv.processing.HsvRangeProcessor;
import com.flash3388.flashlib.vision.cv.processing.RectProcessor;
import com.flash3388.flashlib.vision.cv.processing.Scorable;
import com.flash3388.flashlib.vision.cv.processing.ScoringProcessor;
import com.flash3388.flashlib.vision.processing.VisionPipeline;
import com.flash3388.flashlib.vision.processing.analysis.Analysis;
import com.flash3388.flashlib.vision.processing.analysis.AnalysisAlgorithms;
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

    private static final double TARGET_REAL_WIDTH_CM = 20;
    private static final double CAMERA_FOV_RAD = 30;
    private static final double EXPECTED_RATIO = 0.0;

    public static void main(String[] args) throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // we need something to run the actual polling action and vision code
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        // we'll use a camera, interfaced using OpenCv
        try (Camera<CvImage> camera = new CvCamera(0)) {
            // let's set up the window, with a single label in the center where we will set the icon.
            JFrame frame = new JFrame();
            frame.setLayout(new FlowLayout());
            frame.setSize(500, 500);
            JLabel lbl = new JLabel();
            lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            lbl.setSize(500, 500);
            lbl.setVisible(true);
            frame.add(lbl);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

    private static Future<?> startVisionPipeline(ImageSource<CvImage> imageSource, Pipeline<CvImage> guiPipeline,
                                                 ScheduledExecutorService executorService) {
        CvProcessing cvProcessing = new CvProcessing();

        // we'll poll images from imageSource with executeService
        return imageSource.asyncPollAtFixedRate(executorService,
                Time.milliseconds(1000), // polling rate
                guiPipeline.divergeTo(new VisionPipeline.Builder<CvImage, Optional<TargetScorable>>()
                        .process(new HsvRangeProcessor(
                                new HsvColorSettings(
                                        new ColorRange(0, 180),
                                        new ColorRange(100, 255),
                                        new ColorRange(105, 255)),
                                cvProcessing)
                                .pipeTo(new RectProcessor(cvProcessing, (rect)-> rect.area() > 1000)
                                        .pipeTo(new ScoringProcessor<Rect, TargetScorable>(
                                                        (rect) -> new TargetScorable(rect, EXPECTED_RATIO),
                                                        (scorable)-> scorable.score() > 0.6)
                                                .pipeTo(new BestScoreProcessor<>())))
                        )
                        .analyse((image, best)-> {
                            if (!best.isPresent()) {
                                return Optional.empty();
                            }

                            TargetScorable scorable = best.get();
                            double distance = AnalysisAlgorithms.measureDistance(scorable.getWidth(), image.getWidth(),
                                    TARGET_REAL_WIDTH_CM, CAMERA_FOV_RAD);
                            double angle = AnalysisAlgorithms.calculateHorizontalOffsetDegrees2(scorable.getCenter().x(),
                                    image.getWidth(), CAMERA_FOV_RAD);

                            return Optional.of(new Analysis.Builder()
                                    .put("distance", distance)
                                    .put("angle", angle)
                                    .build());
                        })
                        .analysisTo(System.out::println)
                        .build()),
                // if there is an error, it will be ignored. Normally not recommended, but it's okay for an example
                Throwables.silentHandler());
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

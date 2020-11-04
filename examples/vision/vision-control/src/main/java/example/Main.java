package example;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.SystemNanoClock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.vision.Camera;
import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.Source;
import com.flash3388.flashlib.vision.control.SingleThreadVisionControl;
import com.flash3388.flashlib.vision.control.VisionControl;
import com.flash3388.flashlib.vision.control.VisionData;
import com.flash3388.flashlib.vision.cv.CvCamera;
import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.cv.CvProcessing;
import com.flash3388.flashlib.vision.processing.VisionProcessor;
import org.opencv.core.Core;

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
            VisionControl control = createVisionControl(camera, guiPipeline, executorService);
            control.start();
            // when the window is closed, we should stop the vision code
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    control.stop();
                }
            });

            while (control.isRunning()) {
                Thread.sleep(100);
            }
        } finally {
            executorService.shutdownNow();
        }
    }

    private static VisionControl createVisionControl(Source<CvImage> source,
                                                 Pipeline<CvImage> guiPipeline,
                                                 ScheduledExecutorService executorService) {
        CvProcessing cvProcessing = new CvProcessing();
        Clock clock = new SystemNanoClock();

        VisionControl control = SingleThreadVisionControl.<CvImage>withExecutorService(executorService, Time.milliseconds(1000))
                .source(source)
                .preProcess(guiPipeline)
                .processor(new VisionProcessor.Builder<VisionData<CvImage>, CvImage>()
                        .process((data)-> data.getData())
                        .analyse((data, result)-> Optional.empty())
                        .build())
                .build();

        return control;
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

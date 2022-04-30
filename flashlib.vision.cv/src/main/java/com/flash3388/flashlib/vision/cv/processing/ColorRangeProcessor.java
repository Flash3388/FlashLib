package com.flash3388.flashlib.vision.cv.processing;

import com.flash3388.flashlib.vision.VisionException;
import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.processing.Processor;
import com.flash3388.flashlib.vision.processing.color.ColorConfig;
import com.flash3388.flashlib.vision.processing.color.ColorRange;
import com.flash3388.flashlib.vision.processing.color.ColorSpace;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.List;
import java.util.function.Supplier;

public class ColorRangeProcessor implements Processor<ImageContainer, ProcessedImageContainer> {

    private final Supplier<ColorConfig> mColorSupplier;

    public ColorRangeProcessor(Supplier<ColorConfig> colorSupplier) {
        mColorSupplier = colorSupplier;
    }

    @Override
    public ProcessedImageContainer process(ImageContainer input) throws VisionException {
        ColorConfig colorConfig = mColorSupplier.get();

        Mat src = input.getImage().getMat();
        Mat dst = new Mat();
        convertColorSpace(src, input.getColorSpace(), dst, colorConfig.getSpace());
        filterColors(dst, dst, colorConfig);

        return new ProcessedImageContainer(
                new CvImage(dst),
                colorConfig.getSpace(),
                input
        );
    }

    private void convertColorSpace(Mat src, ColorSpace srcColorSpace, Mat dst, ColorSpace dstColorSpace) {
        if (srcColorSpace != dstColorSpace) {
            int code = getColorSpaceConversionCode(srcColorSpace, dstColorSpace);
            Imgproc.cvtColor(src, dst, code);
        }
    }

    private void filterColors(Mat src, Mat dst, ColorConfig colorConfig) {
        Core.inRange(src,
                getMinColors(colorConfig),
                getMaxColors(colorConfig),
                dst);
    }

    private int getColorSpaceConversionCode(ColorSpace src, ColorSpace dst) {
        if (src == ColorSpace.BGR && dst == ColorSpace.HSV) {
            return Imgproc.COLOR_BGR2HSV;
        }
        if (src == ColorSpace.BGR && dst == ColorSpace.RGB) {
            return Imgproc.COLOR_BGR2RGB;
        }

        throw new UnsupportedOperationException("not code for color space conversion");
    }

    private static Scalar getMinColors(ColorConfig colorConfig) {
        List<ColorRange> filters = colorConfig.getDimensionFilters();
        double[] values = {0, 0, 0, 0};
        for (int i = 0; i < filters.size(); i++) {
            values[i] = filters.get(i).getMin();
        }

        return new Scalar(values);
    }

    private static Scalar getMaxColors(ColorConfig colorConfig) {
        List<ColorRange> filters = colorConfig.getDimensionFilters();
        double[] values = {0, 0, 0, 0};
        for (int i = 0; i < filters.size(); i++) {
            values[i] = filters.get(i).getMax();
        }

        return new Scalar(values);
    }
}

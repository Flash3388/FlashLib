package com.flash3388.flashlib.vision.cv.detection;

import com.flash3388.flashlib.vision.color.ColorRange;
import com.flash3388.flashlib.vision.cv.CvHelper;
import com.flash3388.flashlib.vision.cv.CvImage;
import com.flash3388.flashlib.vision.detection.ObjectDetector;
import com.flash3388.flashlib.vision.detection.Target;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.Collection;
import java.util.List;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ColoredShapeDetector implements ObjectDetector<CvImage, Target> {

    Supplier<ColorRange> colorRangeSupplier;
    IntSupplier verticesSupplier;
    DoubleSupplier accuracySupplier;


    public ColoredShapeDetector(Supplier<ColorRange> colorRangeSupplier) {
        this.colorRangeSupplier = colorRangeSupplier;
    }

    @Override
    public Collection<? extends Target> detect(CvImage image) {

        CvImage binaryImage = CvHelper.filterColors(image, colorRangeSupplier.get());
        List<MatOfPoint> contours = CvHelper.detectContours(binaryImage);
        CvHelper.filterContoursByShape(contours, verticesSupplier.getAsInt(), accuracySupplier.getAsDouble());

        return rectifyContours(contours).map(RectTarget::new).collect(Collectors.toList());
    }

    private Stream<Rect> rectifyContours(List<MatOfPoint> contours) {
        return contours.stream()
                .map(Imgproc::boundingRect);
    }
}

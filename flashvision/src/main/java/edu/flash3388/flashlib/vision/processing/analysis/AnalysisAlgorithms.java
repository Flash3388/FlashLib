package edu.flash3388.flashlib.vision.processing.analysis;

import com.jmath.vectors.Vector2;
import com.jmath.vectors.Vector3;
import edu.flash3388.flashlib.vision.Image;

public class AnalysisAlgorithms {

    public double measureDistanceByWidth(Image image, double contourWidth, double realWidth, double fovRadians) {
        return measureDistance(image.getWidth(), contourWidth, realWidth, fovRadians);
    }

    public double measureDistanceByHeight(Image image, double contourWidth, double realWidth, double fovRadians) {
        return measureDistance(image.getHeight(), contourWidth, realWidth, fovRadians);
    }

    public double measureDistance(double imageDimension, double contourDimension, double realDimension, double fovRadians) {
        return (realDimension * imageDimension / (2 * contourDimension * Math.tan(fovRadians)));
    }

    public double calculateHorizontalOffsetRadians(Image image, Vector2 targetPoint, double fovRadians){
        double centerX = image.getHeight() * 0.5;
        double centerY = image.getWidth() * 0.5;
        double focalLength = centerX / Math.tan(0.5 * fovRadians);

        Vector3 center = new Vector3(0, 0, focalLength);
        Vector3 pixel = new Vector3(targetPoint.x() - centerX, targetPoint.y() - centerY, focalLength);

        return center.angleTo(pixel);
    }

    public double calculateHorizontalOffsetDegrees(Image image, Vector2 targetPoint, double fovDegrees){
        return Math.toDegrees(calculateHorizontalOffsetRadians(image, targetPoint, Math.toRadians(fovDegrees)));
    }
}

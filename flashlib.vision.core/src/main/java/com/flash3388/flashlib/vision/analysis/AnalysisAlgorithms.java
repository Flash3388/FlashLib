package com.flash3388.flashlib.vision.analysis;

import com.flash3388.flashlib.vision.Image;
import com.jmath.vectors.Vector2;
import com.jmath.vectors.Vector3;

public final class AnalysisAlgorithms {

    private AnalysisAlgorithms() {}

    public static double measureDistanceByWidth(Image image, double contourWidth, double realWidth, double fovRadians) {
        return measureDistance(image.getWidth(), contourWidth, realWidth, fovRadians);
    }

    public static double measureDistanceByHeight(Image image, double contourWidth, double realWidth, double fovRadians) {
        return measureDistance(image.getHeight(), contourWidth, realWidth, fovRadians);
    }

    public static double measureDistance(double imageDimension, double contourDimension, double realDimension, double fovRadians) {
        return (realDimension * imageDimension / (2 * contourDimension * Math.tan(fovRadians)));
    }

    public static double calculateHorizontalOffsetRadians(Image image, Vector2 targetPoint, double fovRadians){
        double centerX = image.getHeight() * 0.5;
        double centerY = image.getWidth() * 0.5;
        double focalLength = centerX / Math.tan(0.5 * fovRadians);

        Vector3 center = new Vector3(0, 0, focalLength);
        Vector3 pixel = new Vector3(targetPoint.x() - centerX, targetPoint.y() - centerY, focalLength);

        return center.angleTo(pixel);
    }

    public static double calculateHorizontalOffsetDegrees(Image image, Vector2 targetPoint, double fovDegrees){
        return Math.toDegrees(calculateHorizontalOffsetRadians(image, targetPoint, Math.toRadians(fovDegrees)));
    }

    public static double calculateHorizontalOffsetDegrees2(double centerX, double imageWidth, double camFovRadians) {
        // based on Alon and Michaelov's calculations used in 2020 vision code:
        // https://github.com/Flash3388/flash-vision-perocessing/blob/jetson2020/src/main/java/com/flash3388/ScoreMatchingPipeline.java
        // ScoreMatchingPipeline.calcAngleOffsetDegrees

        double imageCenter = imageWidth * 0.5;
        double xOffset = centerX - imageCenter;
        double focalLengthPixel = imageCenter / Math.tan(Math.toDegrees(camFovRadians) * 0.5 * Math.PI/180);
        return Math.toDegrees(Math.atan(xOffset / focalLengthPixel));
    }
}

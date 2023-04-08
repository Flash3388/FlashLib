package com.flash3388.flashlib.vision.detection;

import com.flash3388.flashlib.vision.Image;
import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.VisionException;
import com.flash3388.flashlib.vision.analysis.Analysis;
import com.flash3388.flashlib.vision.analysis.AnalysisAlgorithms;
import com.flash3388.flashlib.vision.analysis.JsonAnalysis;

import java.util.Map;

public class TargetAnalyser implements Pipeline<TargetResultData<Image, Target>> {

    private final RealTargetConfig mRealTargetConfig;
    private final CameraConfig mCameraConfig;
    private final Pipeline<Analysis> mOutPipe;

    public TargetAnalyser(RealTargetConfig realTargetConfig, CameraConfig cameraConfig, Pipeline<Analysis> outPipe) {
        mRealTargetConfig = realTargetConfig;
        mCameraConfig = cameraConfig;
        mOutPipe = outPipe;
    }


    @Override
    public void process(TargetResultData<Image, Target> input) throws VisionException {
        Image originalImage = input.getImage();

        JsonAnalysis.Builder builder = new JsonAnalysis.Builder();
        for (Map.Entry<Integer, ? extends Target> entry : input.getTargets().entrySet()) {
            int id = entry.getKey();
            Target target = entry.getValue();

            double distance = AnalysisAlgorithms.measureDistance(
                    originalImage.getWidth(),
                    target.getWidthPixels(),
                    mRealTargetConfig.getWidthCm(),
                    mCameraConfig.getFovRadians());

            double angle = AnalysisAlgorithms.calculateHorizontalOffsetDegrees2(
                    target.getCenter().x(),
                    originalImage.getWidth(),
                    mCameraConfig.getFovRadians());

            builder.buildTarget()
                    .put("id", id)
                    .put("centerX", target.getCenter().x())
                    .put("centerY", target.getCenter().y())
                    .put("width", target.getWidthPixels())
                    .put("height", target.getHeightPixels())
                    .put("distance", distance)
                    .put("angle", angle)
                    .build();
        }

        mOutPipe.process(builder.build());
    }
}

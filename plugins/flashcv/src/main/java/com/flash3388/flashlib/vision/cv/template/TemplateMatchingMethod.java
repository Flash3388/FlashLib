package com.flash3388.flashlib.vision.cv.template;

import org.opencv.imgproc.Imgproc;

public enum TemplateMatchingMethod {
    SQDIFF(Imgproc.TM_SQDIFF),
    SQDIFF_NORMED(Imgproc.TM_SQDIFF_NORMED),
    TM_CCORR(Imgproc.TM_CCOEFF),
    TM_CCORR_NORMED(Imgproc.TM_CCORR_NORMED),
    TM_COEFF(Imgproc.TM_CCOEFF),
    TM_COEFF_NORMED(Imgproc.TM_CCOEFF_NORMED);

    private final int mValue;

    TemplateMatchingMethod(int value) {
        mValue = value;
    }

    public int value() {
        return mValue;
    }
}

package com.flash3388.flashlib.vision.analysis;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class AnalysisSerializer {

    public void serializeTo(DataOutput dataOutput, Analysis analysis) throws IOException {
        dataOutput.writeUTF(analysis.getClass().getName());
        analysis.serializeTo(dataOutput);
    }

    public Analysis deserializeFrom(DataInput dataInput) throws IOException {
        try {
            String analysisClass = dataInput.readUTF();
            Class<?> cls = Class.forName(analysisClass);
            if (!Analysis.class.isAssignableFrom(cls)) {
                throw new IOException("not analysis class " + cls.getName());
            }

            @SuppressWarnings("unchecked")
            Class<? extends Analysis> analysisCls = (Class<? extends Analysis>) cls;
            Constructor<? extends Analysis> constructor = analysisCls.getConstructor(DataInput.class);
            return constructor.newInstance(dataInput);
        } catch (ClassNotFoundException | NoSuchMethodException |
                InstantiationException | IllegalAccessException |
                InvocationTargetException e) {
            throw new IOException(e);
        }
    }
}

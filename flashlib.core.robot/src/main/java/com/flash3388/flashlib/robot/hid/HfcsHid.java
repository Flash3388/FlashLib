package com.flash3388.flashlib.robot.hid;

import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.hid.generic.weak.WeakHidInterface;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.time.Time;

public class HfcsHid {

    private HfcsHid() {}

    public static HidInterface createReceiver(HfcsRegistry registry) {
        HidData data = new HidData();
        DataStore dataStore = new DataStore();

        registry.registerIncoming(new HidDataInType(dataStore))
                .addListener(new NewDataListener(data, dataStore));

        return new WeakHidInterface(new HfcsHidInterface(data));
    }

    public static HidData createProvider(HfcsRegistry registry, Time sendPeriod) {
        HidData data = new HidData();
        DataStore dataStore = new DataStore();
        HidOutData outData = new HidOutData(data, dataStore);

        registry.registerOutgoing(new HidDataOutType(), sendPeriod, ()-> outData);

        return data;
    }
}

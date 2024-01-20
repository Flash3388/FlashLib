package com.flash3388.flashlib.robot.hfcs.hid;

import com.flash3388.flashlib.hid.HidChannel;
import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.hid.data.HidData;
import com.flash3388.flashlib.hid.data.RawHidData;
import com.flash3388.flashlib.hid.generic.GenericHidChannel;
import com.flash3388.flashlib.hid.generic.weak.WeakHidInterface;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.net.hfcs.HfcsRegisteredIncoming;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.FlashLibMainThread;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.resources.CircularResourceHolder;
import org.slf4j.Logger;

public class HfcsHid {

    private HfcsHid() {}

    private static final Logger LOGGER = Logging.getMainLogger();
    private static final Time RECEIVE_TIMEOUT = Time.seconds(2);

    public static HidInterface createReceiver(HfcsRegistry registry, FlashLibMainThread mainThread) {
        HidData data = new HidData();
        CircularResourceHolder<RawHidData> dataStore = new CircularResourceHolder<>(3, RawHidData::new);

        HfcsRegisteredIncoming<RawHidData> incoming = registry.registerIncoming(new HidDataInType(dataStore), RECEIVE_TIMEOUT);
        incoming.addListener(new NewDataListener(data, dataStore, LOGGER));

        return new WeakHidInterface(new HfcsHidInterface(data), mainThread);
    }

    public static HidData createProvider(HfcsRegistry registry, Time sendPeriod) {
        HidData data = new HidData();
        CircularResourceHolder<RawHidData> dataStore = new CircularResourceHolder<>(3, RawHidData::new);
        HidOutData outData = new HidOutData(data, dataStore);

        registry.registerOutgoing(new HidDataType(), sendPeriod, ()-> outData);

        return data;
    }

    public static HidChannel newChannel(int channel) {
        return new GenericHidChannel(channel);
    }
}

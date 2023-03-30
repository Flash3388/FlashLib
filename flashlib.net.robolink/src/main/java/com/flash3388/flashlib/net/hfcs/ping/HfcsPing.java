package com.flash3388.flashlib.net.hfcs.ping;

import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.util.function.BiConsumer;

public class HfcsPing {

    private HfcsPing() {}

    public static void registerReceiver(HfcsRegistry registry, BiConsumer<InstanceId, PingData> consumer) {
        registry.registerIncoming(new PingDataInType())
                .addListener((event)-> {
                    consumer.accept(event.getSender(), event.getData());
                });
    }

    public static void registerSender(HfcsRegistry registry, Clock clock, Time period) {
        registry.registerOutgoing(new PingDataType(), period, ()->new PingData(clock.currentTime()));
    }
}

package com.flash3388.flashlib.app.net;

import com.flash3388.flashlib.net.obsr.impl.ObsrNodeServiceBase;
import com.flash3388.flashlib.net.obsr.impl.ObsrPrimaryNodeService;
import com.flash3388.flashlib.net.obsr.impl.ObsrSecondaryNodeService;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;

public class ObsrConfiguration {

    interface Creator {
        ObsrNodeServiceBase create(InstanceId ourId, Clock clock) throws Exception;
    }

    final Creator creator;

    public ObsrConfiguration(Creator creator) {
        this.creator = creator;
    }

    public static ObsrConfiguration disabled() {
        return new ObsrConfiguration(null);
    }

    public static ObsrConfiguration primaryNode() {
        return new ObsrConfiguration(ObsrPrimaryNodeService::new);
    }

    public static ObsrConfiguration primaryNode(String bindAddress) {
        return new ObsrConfiguration((ourId, clock)-> new ObsrPrimaryNodeService(ourId, clock, bindAddress));
    }

    public static ObsrConfiguration secondaryNode(String primaryNodeAddress) {
        return new ObsrConfiguration((ourId, clock)-> new ObsrSecondaryNodeService(ourId, clock, primaryNodeAddress));
    }
}

package com.flash3388.flashlib.net.hfcs;

import com.flash3388.flashlib.io.Serializable;
import com.flash3388.flashlib.time.Time;

import java.util.function.Supplier;

/**
 * Registry for High-Frequency Control/Status packets. This protocol transmits and receives periodic packets from
 * and to all instances of FlashLib in the network.
 * Incoming and outgoing packets may be registered through here to be sent or received.
 *
 * @since FlashLib 3.2.0
 */
public interface HfcsRegistry {

    /**
     * Registers an outgoing packet sending. Every period of time a packet will be sent with
     * data given by a supplier. this data will be sent under the defined type to all instances in the network.
     *
     * @param type type of packet
     * @param period period for sending the data
     * @param supplier supplier of the data. Should be updated in real-time with changes.
     */
    void registerOutgoing(HfcsType type,
                          Time period,
                          Supplier<? extends Serializable> supplier);

    /**
     * Registers an incoming packet receiver. This receiver will listen for packets of the given type
     * sent by instances in the network.
     *
     * @param type type of incoming data.
     * @param receiveTimeout time after which, if no data was received, a timeout is declared
     *                       and can be handled.
     * @return a control class for incoming data management.
     * @param <T> type of incoming data.
     */
    <T> HfcsRegisteredIncoming<T> registerIncoming(HfcsInType<T> type, Time receiveTimeout);


    /**
     * Registers an incoming packet receiver. This receiver will listen for packets of the given type
     * sent by instances in the network.
     * The packet can never be timed out.
     *
     * @param type type of incoming data.
     * @return a control class for incoming data management.
     * @param <T> type of incoming data.
     */
    default <T> HfcsRegisteredIncoming<T> registerIncoming(HfcsInType<T> type) {
        return registerIncoming(type, Time.INVALID);
    }
}

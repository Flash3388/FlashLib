package com.flash3388.flashlib.app.net;

/**
 * Describes the operating features of the networking part of FlashLib applications.
 *
 * @since FlashLib 3.2.0
 */
public interface NetworkingMode {

    /**
     * Returns whether networking features are enabled for the application.
     *
     * @return <b>true</b> if enabled <b>false</b> otherwise
     */
    boolean isNetworkingEnabled();

    /**
     * Returns whether the Object Storage protocol is enabled.
     *
     * @return <b>true</b> if enabled <b>false</b> otherwise
     */
    boolean isObjectStorageEnabled();


    /**
     * Returns whether the HFCS protocol is enabled.
     *
     * @return <b>true</b> if enabled <b>false</b> otherwise
     */
    boolean isHfcsEnabled();
}

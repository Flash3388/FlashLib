package com.flash3388.flashlib.net.robolink;

import java.util.Optional;

public interface RemotesStorage {

    Optional<Remote> getById(String id);

}

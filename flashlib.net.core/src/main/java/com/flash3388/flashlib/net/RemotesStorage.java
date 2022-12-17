package com.flash3388.flashlib.net;

import java.util.Optional;

public interface RemotesStorage {

    Optional<Remote> getById(String id);
}

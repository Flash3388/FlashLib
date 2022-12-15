package com.flash3388.flashlib.hmi.impl;

import com.castle.io.TypedSerializer;
import com.flash3388.flashlib.hmi.HmiContainer;
import com.flash3388.flashlib.hmi.HmiObject;
import com.flash3388.flashlib.hmi.Namespace;
import com.flash3388.flashlib.hmi.comm.BasicMessage;
import com.flash3388.flashlib.hmi.comm.MessageListener;
import com.flash3388.flashlib.hmi.comm.NewMessageEvent;
import com.flash3388.flashlib.hmi.comm.impl.MessagingService;
import com.flash3388.flashlib.hmi.comm.messages.SetPrimitiveValueMessage;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HmiContainerImpl implements HmiContainer, MessageListener {

    private final MessagingService mMessagingService;
    private final Map<String, RegisteredObject> mRootObjects;

    public HmiContainerImpl(MessagingService messagingService) {
        mMessagingService = messagingService;
        mRootObjects = new ConcurrentHashMap<>();

        mMessagingService.addListener(this);
    }

    @Override
    public void put(String name, HmiObject object) {
        RegisteredObject registeredObject = new RegisteredObject(object);
        mRootObjects.put(name, registeredObject);
    }

    private void setValue(Namespace namespace, Object value) {
        Iterator<String> names = namespace.iterator();
        if (!names.hasNext()) {
            throw new UnsupportedOperationException();
        }

        String first = names.next();
        if (names.hasNext() && mRootObjects.containsKey(first)) {
            // set value in object
            RegisteredObject object = mRootObjects.get(first);
            object.set(names, value);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void onNewMessage(NewMessageEvent event) {
        try {
            BasicMessage basicMessage = event.getMessage();
            switch (basicMessage.getType()) {
                case SET_PRIMITIVE_VALUE: {
                    SetPrimitiveValueMessage message = convertMessage(basicMessage, SetPrimitiveValueMessage.class);
                    setValue(new Namespace(message.getNamespace()), message.getValue());
                    break;
                }
                default:
                    throw new UnsupportedOperationException();
            }
        } catch (IOException e) {

        }
    }

    private <T> T convertMessage(BasicMessage basicMessage, Class<T> type) throws IOException {
        byte[] content = basicMessage.getContent();
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
             DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            Constructor<T> ctor = type.getConstructor(DataInput.class, TypedSerializer.class);
            return ctor.newInstance(dataInputStream, new TypedSerializer());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IOException(e);
        }
    }
}

package com.flash3388.flashlib.net.messaging.impl;

import com.castle.net.Connector;
import com.castle.net.StreamConnection;
import com.castle.net.tcp.TcpServerConnector;
import com.castle.time.Time;
import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.data.KnownMessageTypes;
import com.flash3388.flashlib.net.messaging.io.MessageSerializer;
import com.flash3388.flashlib.net.messaging.io.MessagingChannel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerChannel implements MessagingChannel {

    private final Connector<StreamConnection> mConnector;
    private final Time mConnectionTimeout;
    private final MessageSerializer mSerializer;

    private final List<StreamConnection> mConnections;

    ServerChannel(Connector<StreamConnection> connector, Time connectionTimeout, MessageSerializer serializer, List<StreamConnection> connections) {
        mConnector = connector;
        mConnectionTimeout = connectionTimeout;
        mSerializer = serializer;
        mConnections = connections;
    }

    public ServerChannel(int serverPort, KnownMessageTypes messageTypes) {
        this(new TcpServerConnector(serverPort, 100),
                Time.milliseconds(100),
                new MessageSerializer(messageTypes),
                new CopyOnWriteArrayList<>());
    }

    public void receiveNewConnections() throws IOException, TimeoutException {
        StreamConnection connection = mConnector.connect(mConnectionTimeout);
        mConnections.add(connection);
    }

    @Override
    public boolean establishConnection() throws IOException, TimeoutException {
        return true;
    }

    @Override
    public void write(Message message) throws IOException, TimeoutException {
        for (Iterator<StreamConnection> it = mConnections.iterator(); it.hasNext();) {
            StreamConnection connection = it.next();

            try {
                DataOutputStream outputStream = new DataOutputStream(connection.outputStream());
                mSerializer.write(outputStream, message);
            } catch (IOException e) {
                Closeables.silentClose(connection);
                it.remove();
            }
        }
    }

    @Override
    public Message read() throws IOException, TimeoutException, InterruptedException {
        for (Iterator<StreamConnection> it = mConnections.iterator(); it.hasNext();) {
            StreamConnection connection = it.next();

            try {
                DataInputStream inputStream = new DataInputStream(connection.inputStream());
                if (inputStream.available() < 1) {
                    continue;
                }

                return mSerializer.read(inputStream);
            } catch (IOException e) {
                Closeables.silentClose(connection);
                it.remove();
            }
        }

        throw new TimeoutException();
    }
}

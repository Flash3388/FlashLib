package com.flash3388.flashlib.net.messaging.impl;

import com.castle.net.Connector;
import com.castle.net.StreamConnection;
import com.castle.net.tcp.TcpClientConnector;
import com.castle.time.Time;
import com.castle.time.exceptions.TimeoutException;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.data.KnownMessageTypes;
import com.flash3388.flashlib.net.messaging.io.MessageSerializer;
import com.flash3388.flashlib.net.messaging.io.MessagingChannel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

public class ClientChannel implements MessagingChannel {

    private final Connector<StreamConnection> mConnector;
    private final Time mConnectionTimeout;
    private final MessageSerializer mSerializer;

    private StreamConnection mConnection;
    private DataOutputStream mOutputStream;
    private DataInputStream mInputStream;

    ClientChannel(Connector<StreamConnection> connector, Time connectionTimeout, MessageSerializer serializer) {
        mConnector = connector;
        mConnectionTimeout = connectionTimeout;
        mSerializer = serializer;

        mConnection = null;
        mOutputStream = null;
        mInputStream = null;
    }

    public ClientChannel(String serverAddress, int serverPort, KnownMessageTypes messageTypes) {
        this(new TcpClientConnector(
                new InetSocketAddress(serverAddress, serverPort),
                100),
                Time.milliseconds(100),
                new MessageSerializer(messageTypes));
    }

    @Override
    public boolean establishConnection() throws IOException, TimeoutException {
        return refreshConnection();
    }

    @Override
    public void write(Message message) throws IOException, TimeoutException {
        try {
            refreshConnection();
            mSerializer.write(mOutputStream, message);
        } catch (SocketTimeoutException e) {
            throw new TimeoutException(e);
        } catch (IOException e) {
            closeConnection();
            throw e;
        }
    }

    @Override
    public Message read() throws IOException, TimeoutException, InterruptedException {
        try {
            refreshConnection();
            return mSerializer.read(mInputStream);
        } catch (SocketTimeoutException e) {
            throw new TimeoutException(e);
        } catch (IOException e) {
            closeConnection();
            throw e;
        }
    }

    private synchronized boolean refreshConnection() throws IOException, TimeoutException {
        if (mConnection == null) {
            mConnection = mConnector.connect(mConnectionTimeout);
            mInputStream = new DataInputStream(mConnection.inputStream());
            mOutputStream = new DataOutputStream(mConnection.outputStream());

            return false;
        }

        return true;
    }

    private synchronized void closeConnection() {
        if (mConnection == null) {
            return;
        }

        try {
            mConnection.close();
            mConnection = null;
        } catch (IOException e) {
            // TODO: HANDLE
        }
    }
}

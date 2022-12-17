package com.flash3388.flashlib.net.robolink.io;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.castle.util.throwables.ThrowableChain;
import com.castle.util.throwables.Throwables;
import com.flash3388.flashlib.net.robolink.InboundPacket;
import com.flash3388.flashlib.net.robolink.RemotesStorage;
import com.flash3388.flashlib.net.udp.MultiTargetUdpChannel;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Optional;

public class RoboLinkChannel implements Closeable {

    public interface UpdateHandler {
        void onPacketReceived(InboundPacket packet, boolean isFirstPacketFromSender);
    }

    private static final int[] PORTS = {5005, 5006, 5007, 5008, 5009};

    private final String mOurId;
    private final MultiTargetUdpChannel mChannel;
    private final Clock mClock;
    private final Logger mLogger;

    private final RemotesStorageImpl<RemoteImpl> mRemotes;
    private final ByteBuffer mReadBuffer;
    private final PacketSerializer mSerializer;

    RoboLinkChannel(String id, MultiTargetUdpChannel channel, Clock clock, Logger logger) {
        mOurId = id;
        mChannel = channel;
        mClock = clock;
        mLogger = logger;

        mRemotes = new RemotesStorageImpl<>();
        mReadBuffer = ByteBuffer.allocateDirect(1024);
        mSerializer = new PacketSerializer();
    }

    public RoboLinkChannel(String id, Clock clock, Logger logger) {
        this(id, new MultiTargetUdpChannel(PORTS, logger), clock, logger);
    }

    public RemotesStorage getRemotesStorage() {
        return mRemotes;
    }

    public void handleUpdates(UpdateHandler handler) throws IOException, InterruptedException {
        while (true) {
            try {
                mReadBuffer.rewind();
                SocketAddress remoteAddress = mChannel.read(mReadBuffer);

                // parse packet
                int size = mReadBuffer.position();
                mReadBuffer.flip();
                PacketSerializer.Data data = mSerializer.read(mReadBuffer, size);

                if (data.header.getSenderId().equals(mOurId)) {
                    // this is us
                    continue;
                }

                // TODO: HANDLE REMOTES WITH THE SAME ID

                boolean isFirstPacked = false;
                RemoteImpl remote;

                // see if we have this id already
                // update address for the remote if needed
                Optional<RemoteImpl> remoteOptional = mRemotes.updateRemoteByAddressAndId(
                        remoteAddress,
                        data.header.getSenderId());
                if (remoteOptional.isPresent()) {
                    remote = remoteOptional.get();
                } else {
                    // new remote
                    remote = new RemoteImpl(data.header.getSenderId());
                    mRemotes.putNewRemote(remoteAddress, remote);
                    isFirstPacked = true;
                }

                Time now = mClock.currentTime();
                remote.updateLastSeen(now);

                mLogger.debug("Received packet from {} at address {}", remote.getId(), remoteAddress);

                handler.onPacketReceived(
                        new InboundPacketImpl(remote, now, data.header.getContentType(), data.content),
                        isFirstPacked);
            } catch (TimeoutException e) {
                // oh, well try again
            }
        }
    }

    public void send(String remoteId, int contentType, byte[] content) throws IOException {
        Optional<SocketAddress> addressOptional = mRemotes.getAddressForId(remoteId);
        if (!addressOptional.isPresent()) {
            throw new UnknownRemoteException();
        }

        SocketAddress address = addressOptional.get();
        mLogger.debug("Writing packet to remote {} at address {}", remoteId, address);

        PacketHeader header = new PacketHeader(mOurId, contentType, content.length);
        ByteBuffer buffer = mSerializer.write(header, content);
        mChannel.writeTo(buffer, address);
    }

    public void broadcast(int contentType, byte[] content) throws IOException {
        PacketHeader header = new PacketHeader(mOurId, contentType, content.length);
        ByteBuffer buffer = mSerializer.write(header, content);


        mLogger.debug("Writing BROADCAST packet");
        ThrowableChain chain = Throwables.newChain();
        for (int port : PORTS) {
            try {
                mChannel.writeTo(buffer, new InetSocketAddress("255.255.255.255", port));
            } catch (IOException e) {
                chain.chain(e);
            }
        }

        chain.throwIfType(IOException.class);
    }

    @Override
    public void close() throws IOException {
        Closeables.silentClose(mChannel);
        mRemotes.clear();
    }
}

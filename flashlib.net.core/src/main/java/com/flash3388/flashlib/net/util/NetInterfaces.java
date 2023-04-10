package com.flash3388.flashlib.net.util;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.NoSuchElementException;

public class NetInterfaces {

    private NetInterfaces() {}

    public static NetworkInterface getInterface(String name) throws IOException {
        NetworkInterface networkInterface = NetworkInterface.getByName(name);
        if (networkInterface == null) {
            throw new NoSuchElementException("no such interface");
        }

        return networkInterface;
    }

    public static InterfaceAddress getIpv4AddressByInterfaceName(String name) throws IOException {
        NetworkInterface networkInterface = getInterface(name);
        return getIpv4AddressByInterface(networkInterface);
    }

    public static InterfaceAddress getIpv4AddressByInterface(NetworkInterface networkInterface) throws IOException {
        for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
            InetAddress currentAddress = interfaceAddress.getAddress();
            if (currentAddress instanceof Inet4Address) {
                return interfaceAddress;
            }
        }

        throw new NoSuchElementException("address not found");
    }
}

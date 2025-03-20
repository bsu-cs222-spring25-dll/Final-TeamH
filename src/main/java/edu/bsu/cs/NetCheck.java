package edu.bsu.cs;

import java.io.IOException;
import java.net.InetAddress;

public class NetCheck {

    public static boolean networkAvailable() {
        try {
            InetAddress address = InetAddress.getByName("8.8.8.8");
            boolean isReachable = address.isReachable(3000);
            if (isReachable) {
                return true;
            } else {
                System.err.println("Network error: No internet connection.");
                return false;
            }
        } catch (IOException e) {
            System.err.println("Network error: Connection interrupted");
            return false;
        }
    }

}

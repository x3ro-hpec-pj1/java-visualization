package de.x3ro;

import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Opens a TCP server and stores all frames received in the FrameBuffer
 */
public class ReceiverThread implements Runnable {
    @Override
    public void run() {
        try {

            ServerSocket welcomeSocket = new ServerSocket();
            welcomeSocket.setReuseAddress(true);
            welcomeSocket.bind(new InetSocketAddress(6871));

            Socket connectionSocket = welcomeSocket.accept();
            InputStreamReader in = new InputStreamReader(connectionSocket.getInputStream());

            char[] lengthBuffer = new char[5];

            while (connectionSocket.isBound()) {
                // The first five bytes contain the length of the packet (in ASCII :D)
                in.read(lengthBuffer, 0, 5);

                int length = Integer.valueOf(String.valueOf(lengthBuffer));
                char[] buffer = new char[length];

                // Read until we've received the entire frame as specified in the packet length indicator
                int lengthRead = 0;
                while (lengthRead < length) {
                    lengthRead += in.read(buffer, lengthRead, length - lengthRead);
                }

                FrameBuffer.get().put(String.valueOf(buffer));
            }

            welcomeSocket.close();

        } catch(Exception e) {
            System.err.println("Exception in receiver thread");
            System.err.println(e);
        }
    }
}

package de.x3ro;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Convenience wrapper around a blocking queue. Stores the frames received
 * by the ReceiverThread, to be read by the Main thread.
 */
public class FrameBuffer {

    private static FrameBuffer singleton;
    public static final int QUEUE_SIZE = 1000;

    private BlockingQueue<String> queue;

    public FrameBuffer() {
        queue = new ArrayBlockingQueue<String>(QUEUE_SIZE);
    }

    public static FrameBuffer get() {
        if(singleton == null) {
            singleton = new FrameBuffer();
        }
        return singleton;
    }

    public String take() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void put(String element) {
        try {
            queue.put(element);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

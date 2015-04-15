package de.x3ro;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;

/**
 * The Obstacle Detection Visualization component has two main components.
 *
 *  1. This very class, which performs the decoding of the received messages
 *     as well as the rendering of the visualization.
 *  2. The ReceiverThread, which creates a TCP server on port 6871 and stores
 *     all received frames in the FrameBuffer.
 *
 */
public class Main  extends JComponent {

    public static final int WIDTH = 650;
    public static final int HEIGHT = 1300;

    // Maximum distance the Hokuyo laser scanner is capable of reliably determine.
    // Everything beyond that point is not usable data, as per the Hokuyo specs
    // In millimeters
    private static final double MAX_DISTANCE = 5600.0;

    // We adjust the zoom such that we exactly see the 5600mm range supported by
    // the laser scanner.
    private double zoomRatio = WIDTH / MAX_DISTANCE;

    // The current frame to be rendered
    private JSONArray currentFrame = null;



    public static void main(String[] args) throws Exception {
        new Main();
    }


    public Main() {
        new Thread(new ReceiverThread()).start();

        setUpCanvas();

        while(true) {
            decodeAndDraw();

            try {
                Thread.sleep(100); // Draw with 10fps
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void setUpCanvas() {
        JFrame testFrame = new JFrame();
        testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        testFrame.getContentPane().add(this, BorderLayout.CENTER);
        testFrame.pack();
        testFrame.setVisible(true);
    }


    public void decodeAndDraw() {
        String frame = FrameBuffer.get().take();
        try {
            currentFrame = new JSONArray(frame);
            repaint();
        } catch(JSONException e) {
            System.err.println("Could not parse frame: " + frame);
            throw e;
        }
    }



    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.clearRect(0, 0, WIDTH, HEIGHT);

        if(currentFrame == null) {
            return;
        }

        for(int i=0; i<currentFrame.length(); i++) {
            JSONObject el = currentFrame.getJSONObject(i);
            paintElement(el, (Graphics2D) g);
        }
    }

    protected void paintElement(JSONObject el, Graphics2D gr) {
        if(el.getString("type").equals("line")) {

            // Decode color representation (RRGGBBAA, where every two letters represent one byte)
            int color = el.getInt("color");

            double r = ((color >> 24) & 0xFF);
            double g = ((color >> 16) & 0xFF);
            double b = ((color >> 8) & 0xFF);
            // We don't really need the alpha value, so it's not decoded
            // double a = ((color) & 0xFF);

            Color c = new Color((int) r, (int) g, (int) b);

            int x1 = (int) (el.getDouble("x1") * zoomRatio);
            int y1 = (int) (el.getDouble("y1") * zoomRatio + HEIGHT/2);
            int x2 = (int) (el.getDouble("x2") * zoomRatio);
            int y2 = (int) (el.getDouble("y2") * zoomRatio + HEIGHT/2);

            gr.setColor(c);
            gr.drawLine(x1, y1, x2, y2);


        } else if(el.getString("type").equals("text")) {

            gr.drawString(
                    el.getString("text"),
                    (float) (el.getDouble("x") * zoomRatio),
                    (float) (el.getDouble("y") * zoomRatio) + HEIGHT/2
            );
        }
    }
}

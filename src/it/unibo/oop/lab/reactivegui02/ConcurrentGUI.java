package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public final class ConcurrentGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");

    /**
     * Builds a new CGUI.
     */
    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(stop);
        panel.add(up);
        panel.add(down);
        this.getContentPane().add(panel);
        this.setVisible(true);
        final Agent agent = new Agent();
        new Thread(agent).start();
        stop.addActionListener(e -> agent.stopCounting());
        up.addActionListener(e -> agent.upCounting());
        down.addActionListener(e -> agent.downCounting());
    }

    private class Agent implements Runnable {
        private volatile boolean stop;
        private volatile boolean goUp;
        private int counter;

        Agent() {
            this.stop = false;
            this.goUp = true;
            this.counter = 0;
        }

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    if (goUp) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(Integer.toString(counter)));
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
        }

        /**
         * External command to up counting.
         */
        public void upCounting() {
            this.goUp = true;
        }

        /**
         * External command to down counting.
         */
        public void downCounting() {
            this.goUp = false;
        }
    }
}

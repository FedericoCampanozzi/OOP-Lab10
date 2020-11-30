package it.unibo.oop.lab.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public final class AnotherConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final Agent agent = new Agent();

    /**
     * Builds a new CGUI.
     */
    public AnotherConcurrentGUI() {
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
        new Thread(agent).start();
        new Thread(new AgentStoppator()).start();

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
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(Integer.toString(counter)));
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

    private class AgentStoppator implements Runnable {
        private static final long MS_TO_WAIT = 10_000L;
        @Override
        public void run() {
            try {

                Thread.sleep(MS_TO_WAIT);

                SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.stop.setEnabled(false));
                SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.up.setEnabled(false));
                SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.down.setEnabled(false));
                agent.stopCounting();

            } catch (InterruptedException | InvocationTargetException ex) {
                ex.printStackTrace();
            }
        }

    }
}

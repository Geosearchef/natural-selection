package rendering;

import javax.swing.*;

import static rendering.RendererKt.FRAME_TITLE;

public class RendererJava extends JPanel {

    private final JFrame frame;

    public RendererJava() {
        frame = new JFrame(FRAME_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setVisible(true);
    }
}

package components;
import java.awt.*;
import javax.swing.*;
public class GradientPanel extends JPanel {
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint gp = new GradientPaint(0,0,new Color(58,123,213),
                                             getWidth(),getHeight(),new Color(0,210,255));
        g2.setPaint(gp);
        g2.fillRect(0,0,getWidth(),getHeight());
    }
}

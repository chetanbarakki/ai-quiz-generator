package components;
import java.awt.*;
import javax.swing.*;
public class RoundedButton extends JButton {

    public RoundedButton(String text){
        super(text);
        setFocusPainted(false);
        setFont(new Font("Segoe UI",Font.BOLD,16));
        setForeground(Color.WHITE);
        setBackground(new Color(58,123,213));
        setBorder(BorderFactory.createEmptyBorder());
    }

    protected void paintComponent(Graphics g){
        Graphics2D g2=(Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0,0,getWidth(),getHeight(),40,40);
        super.paintComponent(g);
    }

    protected void paintBorder(Graphics g){}
}

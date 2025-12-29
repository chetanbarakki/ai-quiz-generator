import components.GradientPanel;
import components.RoundedButton;
import java.awt.*;
import javax.swing.*;
public class QuizStartFrame extends JFrame {

    JTextArea promptArea;
    JSpinner countSpinner;

    public QuizStartFrame() {

        setTitle("AI Quiz Generator");
        setSize(600,500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel bg = new GradientPanel();
        bg.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setBackground(Color.white);
        card.setPreferredSize(new Dimension(420,360));
        card.setLayout(null);

        JLabel title = new JLabel("Create Your Quiz");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setBounds(110,20,250,40);
        card.add(title);

        JLabel pLabel = new JLabel("Enter Topic / Prompt");
        pLabel.setBounds(40,80,200,25);
        card.add(pLabel);

        promptArea = new JTextArea();
        promptArea.setLineWrap(true);
        promptArea.setWrapStyleWord(true);
        promptArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scroll = new JScrollPane(promptArea);
        scroll.setBounds(40,110,340,120);
        card.add(scroll);

        JLabel qLabel = new JLabel("Number of Questions");
        qLabel.setBounds(40,250,200,25);
        card.add(qLabel);

        countSpinner = new JSpinner(new SpinnerNumberModel(10,5,20,5));
        countSpinner.setBounds(200,250,70,30);
        card.add(countSpinner);

        JButton startBtn = new RoundedButton("Start Quiz");
        startBtn.setBounds(120,300,180,45);
        card.add(startBtn);

        startBtn.addActionListener(e -> {
            new QuizFrame(promptArea.getText(), (int)countSpinner.getValue());
            dispose();
        });

        bg.add(card);
        setContentPane(bg);
        setVisible(true);
    }

    public static void main(String[] args) {
        new QuizStartFrame();
    }
}

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class QuizFrame extends JFrame {

    JLabel qLabel, timerLabel, scoreLabel;
    JButton[] options = new JButton[4];

    List<Question> questions;
    int index = 0, score = 0;
    int timeLeft = 60;
    Timer countdownTimer;
    
    // Timer to handle the delay between questions
    Timer transitionTimer; 
    
    boolean answered = false;

    public QuizFrame(String topic, int count) {
        // Load data
        questions = QuizDataStub.getQuestions(topic, count);

        setTitle("Quiz Application");
        setSize(600, 500); // Increased height slightly
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);

        // Score Label
        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        scoreLabel.setBounds(20, 10, 150, 30);
        add(scoreLabel);

        // Timer Label
        timerLabel = new JLabel("Time: 60");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setForeground(Color.RED);
        timerLabel.setBounds(480, 10, 100, 30);
        add(timerLabel);

        // Question Label
        qLabel = new JLabel();
        qLabel.setFont(new Font("Arial", Font.BOLD, 14));
        // HTML allows text to wrap if the question is long
        qLabel.setBounds(50, 50, 500, 60); 
        qLabel.setVerticalAlignment(SwingConstants.CENTER);
        add(qLabel);

        // Options Buttons
        for (int i = 0; i < 4; i++) {
            options[i] = new JButton();
            options[i].setBounds(100, 130 + (i * 60), 400, 40);
            options[i].setFont(new Font("Arial", Font.PLAIN, 14));
            int idx = i;
            options[i].addActionListener(e -> submitAnswer(idx));
            add(options[i]);
        }

        loadQuestion();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    void loadQuestion() {
        if (index >= questions.size()) {
            // Stop any running timers before showing results
            if (countdownTimer != null) countdownTimer.stop();
            JOptionPane.showMessageDialog(this, "Quiz Over!\nFinal Score: " + score + "/" + questions.size());
            System.exit(0);
            return;
        }

        answered = false;
        timeLeft = 60; // Reset time
        
        Question q = questions.get(index);
        
        // FIXED: Use HTML to wrap text, otherwise long questions get cut off
        qLabel.setText("<html><center>" + q.question + "</center></html>");

        // FIXED: You were not updating the button text with new options!
        for (int i = 0; i < 4; i++) {
            options[i].setText("<html><center>" + q.options[i] + "</center></html>");
            options[i].setEnabled(true);
            options[i].setBackground(null);
        }

        startCountdown();
    }

    void startCountdown() {
        if (countdownTimer != null) countdownTimer.stop();

        timerLabel.setText("Time: 60");

        countdownTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time: " + timeLeft);

            if (timeLeft <= 0 && !answered) {
                revealCorrect();
            }
        });
        countdownTimer.start();
    }

    void submitAnswer(int choice) {
        if (answered) return; 
        answered = true;
        
        // Stop the countdown immediately
        if (countdownTimer != null) countdownTimer.stop();

        Question q = questions.get(index);

        if (choice == q.correct) {
            options[choice].setBackground(Color.GREEN);
            score++;
            scoreLabel.setText("Score: " + score);
        } else {
            options[choice].setBackground(Color.RED);
            options[q.correct].setBackground(Color.GREEN);
        }

        for (JButton b : options) b.setEnabled(false);

        goNextAfterDelay();
    }

    void revealCorrect() {
        answered = true;
        if (countdownTimer != null) countdownTimer.stop();

        Question q = questions.get(index);
        // Highlight the correct answer since time ran out
        options[q.correct].setBackground(Color.GREEN);
        
        for (JButton b : options) b.setEnabled(false);

        goNextAfterDelay();
    }

    void goNextAfterDelay() {
        // FIXED: The major timer bug was here. 
        // 1. We create a specific timer object.
        // 2. We setRepeats(false) so it only runs ONCE.
        
        transitionTimer = new Timer(1500, e -> {
            index++;
            loadQuestion();
        });
        transitionTimer.setRepeats(false); // <--- CRITICAL FIX
        transitionTimer.start();
    }
}
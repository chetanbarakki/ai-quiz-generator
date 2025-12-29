public class Question {
    String question;
    String[] options;
    int correct;

    public Question(String q, String[] op, int c) {
        question = q;
        options = op;
        correct = c;
    }
}

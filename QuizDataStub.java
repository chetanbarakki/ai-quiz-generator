import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

class QuizDataStub {

    // 1. The main method called by your App
    public static List<Question> getQuestions(String topic, int count) {
        List<Question> aiQuestions = fetchFromAI(topic, count);
        
        // If AI works, return its questions
        if (aiQuestions != null && !aiQuestions.isEmpty()) {
            return aiQuestions;
        }

        // FALLBACK: If Python server is down, return dummy data
        System.out.println("⚠️ AI Service unavailable. Using offline backup.");
        List<Question> list = new ArrayList<>();
        list.add(new Question("Offline Mode: What is the capital of France?", 
            new String[]{"Berlin", "London", "Paris", "Madrid"}, 2));
        list.add(new Question("Offline Mode: 2 + 2 = ?", 
            new String[]{"3", "4", "5", "6"}, 1));
        return list;
    }

    // 2. Client to call Python
    private static List<Question> fetchFromAI(String topic, int count) {
        try {
            // Replace spaces with %20 for URL safety
            String safeTopic = topic.replace(" ", "%20");
            String url = "http://localhost:5000/generate-quiz?topic=" + safeTopic + "&count=" + count;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            List<Question> list = mapper.readValue(
                jsonResponse, 
                new TypeReference<List<Question>>(){}
            );
            return list;
        } catch (Exception e) {
            // Silently fail so the app falls back to offline mode
            // e.printStackTrace(); // Uncomment to debug
        }
        return null;
    }
}
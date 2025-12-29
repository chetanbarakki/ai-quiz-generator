import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            if (response.statusCode() == 200) {
                return parseJsonToQuestions(response.body());
            }
        } catch (Exception e) {
            // Silently fail so the app falls back to offline mode
            // e.printStackTrace(); // Uncomment to debug
        }
        return null;
    }

    // 3. Simple Manual JSON Parser (No external libraries like Gson/Jackson needed)
    private static List<Question> parseJsonToQuestions(String json) {
        List<Question> list = new ArrayList<>();
        
        // Regex to find objects like { ... }
        Pattern objectPattern = Pattern.compile("\\{.*?\\}", Pattern.DOTALL);
        Matcher objectMatcher = objectPattern.matcher(json);

        while (objectMatcher.find()) {
            String qBlock = objectMatcher.group();

            // Extract Question
            String qText = extractValue(qBlock, "question");
            
            // Extract Correct Index
            String correctStr = extractValue(qBlock, "correctIndex");
            int correctIdx = 0;
            try { correctIdx = Integer.parseInt(correctStr); } catch (Exception e) {}

            // Extract Choices (Manual Array Parsing)
            List<String> choicesList = new ArrayList<>();
            Pattern choicePattern = Pattern.compile("\"([^\"]+)\""); // Finds strings inside quotes
            // We need to look specifically inside the "choices" part, 
            // but for a simple hack, scanning the whole block usually works 
            // if we skip the first match (which is the question key).
            
            // Better approach for choices:
            int choicesStart = qBlock.indexOf("[");
            int choicesEnd = qBlock.indexOf("]");
            if(choicesStart != -1 && choicesEnd != -1) {
                String choicesArray = qBlock.substring(choicesStart, choicesEnd);
                Matcher m = choicePattern.matcher(choicesArray);
                while(m.find()) {
                    choicesList.add(m.group(1));
                }
            }

            if (choicesList.size() >= 4) {
                list.add(new Question(qText, choicesList.toArray(new String[0]), correctIdx));
            }
        }
        return list;
    }

    // Helper to extract "key": "value" or "key": 123
    private static String extractValue(String source, String key) {
        // Look for "key" : "value"
        Pattern pString = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher mString = pString.matcher(source);
        if (mString.find()) return mString.group(1);

        // Look for "key" : 123
        Pattern pInt = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+)");
        Matcher mInt = pInt.matcher(source);
        if (mInt.find()) return mInt.group(1);
        
        return "Unknown";
    }
}
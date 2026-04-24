import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

public class QuizLeaderboard {

    private static final String BASE_URL = "https://devapigw.vidalhealthtpa.com/srm-quiz-task";
    private static final String REG_NO = "RA2311003020070"; 

    public static void main(String[] args) throws Exception {

        HttpClient client = HttpClient.newHttpClient();

        Set<String> seenEvents = new HashSet<>();
        Map<String, Integer> scores = new HashMap<>();

        for (int poll = 0; poll < 10; poll++) {

            String url = BASE_URL + "/quiz/messages?regNo=" + REG_NO + "&poll=" + poll;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();

            if (body == null || !body.trim().startsWith("{")) {
                Thread.sleep(5000);
                continue;
            }

            JSONObject json = new JSONObject(body);
            JSONArray events = json.getJSONArray("events");

            for (int i = 0; i < events.length(); i++) {

                JSONObject event = events.getJSONObject(i);

                String key = event.getString("roundId") + "_" + event.getString("participant");

                if (seenEvents.contains(key)) continue;

                seenEvents.add(key);

                scores.put(
                        event.getString("participant"),
                        scores.getOrDefault(event.getString("participant"), 0) + event.getInt("score")
                );
            }

            Thread.sleep(5000);
        }

        List<Map.Entry<String, Integer>> leaderboard = new ArrayList<>(scores.entrySet());

        leaderboard.sort((a, b) -> {
            int cmp = b.getValue().compareTo(a.getValue());
            if (cmp != 0) return cmp;
            return a.getKey().compareTo(b.getKey());
        });

        JSONArray leaderboardArray = new JSONArray();
        int totalScore = 0;

        for (Map.Entry<String, Integer> entry : leaderboard) {
            JSONObject obj = new JSONObject();
            obj.put("participant", entry.getKey());
            obj.put("totalScore", entry.getValue());
            leaderboardArray.put(obj);
            totalScore += entry.getValue();
        }

        JSONObject submitBody = new JSONObject();
        submitBody.put("regNo", REG_NO);
        submitBody.put("leaderboard", leaderboardArray);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/quiz/submit"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(submitBody.toString()))
                .build();

        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println(postResponse.body());
        System.out.println("Total Score: " + totalScore);
    }
}
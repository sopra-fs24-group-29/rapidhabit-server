package ch.uzh.ifi.hase.soprafs24.service;

import com.google.gson.JsonParser;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.io.IOException;

@Service
public class OpenAIService {
    private OkHttpClient client = new OkHttpClient();
    @Value("${openai.api.key}")
    private String apiKey;

    public String sendPrompt(String prompt) throws IOException {
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions") // Corrected endpoint for chat models
                .post(RequestBody.create(MediaType.get("application/json; charset=utf-8"),
                        "{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"system\", \"content\": \"" + prompt + "\"}]}"))
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    public String generatePulseCheckContentChatCompletion(int maxTokenSize, double temperature) throws IOException {
        JsonObject messageContent = new JsonObject();
        messageContent.addProperty("role", "user");
        messageContent.addProperty("content", pulseCheckTemplate);

        JsonArray messagesArray = new JsonArray();
        messagesArray.add(messageContent);

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("model", "gpt-3.5-turbo");
        jsonBody.add("messages", messagesArray);
        jsonBody.addProperty("max_tokens", maxTokenSize);
        jsonBody.addProperty("temperature", temperature);

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(RequestBody.create(MediaType.get("application/json; charset=utf-8"), jsonBody.toString()))
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            JsonObject responseJson = JsonParser.parseString(responseBody).getAsJsonObject();
            String content = responseJson.getAsJsonArray("choices").get(0).getAsJsonObject()
                    .getAsJsonObject("message").get("content").getAsString();

            // Truncate content after the first newline character, if present
            if (content.contains("\n")) {
                content = content.substring(0, content.indexOf("\n"));
            }

            return content;
        }
    }

    public String generatePulseCheckContentCompletions(int maxTokenSize, double temperature) throws IOException {
        JsonObject messageContent = new JsonObject();
        messageContent.addProperty("role", "user");
        messageContent.addProperty("content", pulseCheckTemplate);

        JsonArray messagesArray = new JsonArray();
        messagesArray.add(messageContent);

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("model", "gpt-3.5-turbo");
        jsonBody.add("messages", messagesArray);
        jsonBody.addProperty("max_tokens", maxTokenSize);
        jsonBody.addProperty("temperature", temperature);

        String jsonPayload = jsonBody.toString();

        System.out.println("JSON Payload: " + jsonPayload);

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/completions")
                .post(RequestBody.create(MediaType.get("application/json; charset=utf-8"), jsonPayload))
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return response.body().string();
        }
    }

    String template = "In a collaborative habit tracking app, AI Coach Alan conducts regular Pulse Checks with group members. These are small surveys that are fed into the stream, where each group member is asked to give a personal rating. The questions in these surveys should be psychologically meaningful and engaging, encouraging each user to reflect on their behavior. For technical reasons, it is particularly important that each question can be answered with a magnitude from 0 to 1 (this is done using a slider).";

    String pulseCheckTemplate = """
In a collaborative habit tracking app, AI Coach Alan administers regular "Pulse Checks," which are quick surveys. These surveys contain questions that change each time and cover a wide range of topics. They are designed to be psychologically engaging and prompt users to reflect on their current progress. IMPORTANT! – Make sure to generate novel questions after each round and only pose questions which can be answered on a magnitude from 1 to 10!

LIBRARY OF DISTINCTIVE, ENGAGING AND MEANINGFUL PULSE CHECK QUESTIONS:

QUESTION:
A new day brings new opportunities! How motivated are you today to complete all the habits in the group and boost the group's streak?
QUESTION:
Let's talk about health. How well do you fell today?
QUESTION:
It's time to reflect on our communication. How effective do you find the communication within the group?
QUESTION:
How well do you think you're managing the stress that comes with maintaining your habits?
QUESTION:
Looking at our shared goals, how aligned do you feel with the group's objectives today?
QUESTION:
""";

    String pulseCheckTemplateV2 = """
In a collaborative habit tracking app, AI Coach Alan conducts regular Pulse Checks with group members. These small surveys are streamed to each group member, asking for personal ratings on various topics, ensuring each question:
- Is creative and personalized
- Varies in topic, covering broad fields such as health, motivation, communication, and personal growth, etc. ...
- Is psychologically meaningful and engaging
- Encourages reflection on behavior

The responses should be given on a scale of 0 to 1, facilitated by a slider, suitable for quantifiable self-assessment.

PROMPT: requested type: Pulse Check Entry
RESPONSE: Considering your recent sleep quality and energy levels, how well do you feel today?

PROMPT: requested type: Pulse Check Entry
RESPONSE: Reflecting on your long-term goals, how motivated are you this week to pursue these objectives?

PROMPT: requested type: Pulse Check Entry
RESPONSE: Evaluate the effectiveness of our group's communication this week. How well are we connecting and understanding each other?

PROMPT: requested type: Pulse Check Entry
RESPONSE: With the ongoing efforts in balancing work and personal life, how effectively do you think you are managing your stress levels?

PROMPT: requested type: Pulse Check Entry
RESPONSE: Looking at our shared goals for this month, rate how aligned you feel with the group’s objectives.

PROMPT: requested type: Pulse Check Entry
""";

}

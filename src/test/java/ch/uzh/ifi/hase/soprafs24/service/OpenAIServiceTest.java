package ch.uzh.ifi.hase.soprafs24.service;

import okhttp3.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OpenAIServiceTest {

    @Mock
    private OkHttpClient client;

    @InjectMocks
    private OpenAIService openAIService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(openAIService, "apiKey", "test-api-key");
    }

    @Test
    void testSendPrompt() throws IOException {
        // Arrange
        String prompt = "test prompt";
        String responseBodyContent = "{\"choices\":[{\"message\":{\"content\":\"test response\"}}]}";
        ResponseBody responseBody = ResponseBody.create(responseBodyContent, MediaType.get("application/json; charset=utf-8"));
        Response response = new Response.Builder()
                .code(200)
                .message("OK")
                .request(new Request.Builder().url("https://api.openai.com/v1/chat/completions").build())
                .protocol(Protocol.HTTP_1_1)
                .body(responseBody)
                .build();

        Call mockCall = mock(Call.class);
        when(client.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(response);

        // Act
        String result = openAIService.sendPrompt(prompt);

        // Assert
        assertEquals(responseBodyContent, result);
    }

    @Test
    void testSendPrompt_withError() throws IOException {
        // Arrange
        String prompt = "test prompt";
        Response response = new Response.Builder()
                .code(500)
                .message("Internal Server Error")
                .request(new Request.Builder().url("https://api.openai.com/v1/chat/completions").build())
                .protocol(Protocol.HTTP_1_1)
                .body(ResponseBody.create("", MediaType.get("application/json; charset=utf-8")))
                .build();

        Call mockCall = mock(Call.class);
        when(client.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(response);

        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> openAIService.sendPrompt(prompt));
        assertEquals("Unexpected code Response{protocol=http/1.1, code=500, message=Internal Server Error, url=https://api.openai.com/v1/chat/completions}", exception.getMessage());
    }
}

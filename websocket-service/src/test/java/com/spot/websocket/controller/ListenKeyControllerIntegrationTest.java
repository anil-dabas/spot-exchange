package com.spot.websocket.controller;


import com.spot.websocket.service.ListenKeyService;
import com.spot.websocket.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class ListenKeyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private ListenKeyService listenKeyService;

    private String jwtToken;

    @BeforeEach
    public void setup() {
        // Reset any mocks or setup needed before each test method
        Mockito.reset(listenKeyService);
        jwtToken = jwtUtil.generateToken("user-test");
    }

    @Test
    void testCreateListenKey() throws Exception {
        // Prepare data
        String generatedKey = "generatedListenKey";
        Mockito.when(listenKeyService.createListKey()).thenReturn(generatedKey);

        // Perform POST request to "/api/v1/listenKey"
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/listenKey")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("Listen key has created successfully"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.listenKey").value(generatedKey));

        // Verify that listenKeyService.createListKey() was called once
        Mockito.verify(listenKeyService, Mockito.times(1)).createListKey();
    }

    @Test
    void testValidateListenKey() throws Exception {
        String listenKey = "invalid token";

        // Mock behavior of listenKeyService.isValidListKey(listenKey) method
        Mockito.when(listenKeyService.isValidListKey(listenKey)).thenReturn(true);

        // Perform GET request to "/api/v1/listenKey/validate/{listenKey}"
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/listenKey/validate/{listenKey}", listenKey)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("Listen Key validation is true"));

        // Verify that listenKeyService.isValidListKey(listenKey) was called once
        Mockito.verify(listenKeyService, Mockito.times(1)).isValidListKey(listenKey);
    }

    @Test
    void testKeepAliveListenKey() throws Exception {
        String listenKey = "testListenKey";
        // Perform GET request to "/api/v1/listenKey/ping/{listenKey}"
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/listenKey/ping/{listenKey}", listenKey)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("Listen Key was pinged succeed"));

        // Verify that listenKeyService.keepAliveListKey(listenKey) was called once
        Mockito.verify(listenKeyService, Mockito.times(1)).keepAliveListKey(listenKey);
    }
}


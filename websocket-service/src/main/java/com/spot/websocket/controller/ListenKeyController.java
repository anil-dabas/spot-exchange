package com.spot.websocket.controller;


import com.spot.websocket.dto.user.ListenKeyDto;
import com.spot.websocket.model.response.ApiResponse;
import com.spot.websocket.service.ListenKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/listenKey")
@Slf4j
public class ListenKeyController {

    private final ListenKeyService listenKeyService;

    public ListenKeyController(ListenKeyService listenKeyService) {
        this.listenKeyService = listenKeyService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ListenKeyDto>> createlistenKey() {
        log.info("Request to create the listen key");
        String key = listenKeyService.createListKey();
        ListenKeyDto dto = ListenKeyDto.builder()
                .listenKey(key)
                .build();
        return ResponseEntity.ok(ApiResponse.<ListenKeyDto>builder()
                .success(true)
                .msg("Listen key has created successfully")
                .data(dto)
                .build());
    }

    @GetMapping("/validate/{listenKey}")
    public ResponseEntity<ApiResponse<Boolean>> validateListenKey(@PathVariable String listenKey) {
        log.info("Request to validate the api key {}", listenKey);
        boolean isValid = listenKeyService.isValidListKey(listenKey);
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .success(true)
                .msg(String.format("Listen Key validation is %s", isValid))
                .data(isValid)
                .build());
    }

    @GetMapping("/ping/{listenKey}")
    public ResponseEntity<ApiResponse<Boolean>> keepAlive(@PathVariable String listenKey) {
        log.info("Request to keep alive the api key {}", listenKey);
        listenKeyService.keepAliveListKey(listenKey);
        return ResponseEntity.ok(ApiResponse.<Boolean>builder()
                .success(true)
                .msg("Listen Key was pinged succeed")
                .data(true)
                .build());
    }

}

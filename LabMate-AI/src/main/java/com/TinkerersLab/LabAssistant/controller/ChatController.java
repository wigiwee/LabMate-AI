package com.TinkerersLab.LabAssistant.controller;

import org.springframework.web.bind.annotation.RestController;

import com.TinkerersLab.LabAssistant.model.ChatRequest;
import com.TinkerersLab.LabAssistant.service.RagAiService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/llm")
public class ChatController {

    private final RagAiService ragAiService;

    @PostMapping("/ask")
    public String chat(@RequestBody ChatRequest chatRequest) {
        log.info("received request at /api/v1/llm/ask with prompt " + chatRequest.question());
        return new String(ragAiService.chat(chatRequest));
    }
}

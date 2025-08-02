//package org.dows.gpt.bo;
//
//import com.fasterxml.jackson.annotation.JsonProperty;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.dows.gpt.request.ChatMessage;
//import org.springframework.ai.openai.api.ResponseFormat;
//
//import java.util.List;
//
//@NoArgsConstructor
//@AllArgsConstructor
//@Data
//public class ChatCompletion {
//    private String model;
//    private List<ChatMessage> messages;
//    private boolean stream;
//    @JsonProperty("response_format")
//    private ResponseFormat responseFormat;
//}
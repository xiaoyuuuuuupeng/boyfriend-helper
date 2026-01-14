package com.boyfriend.helper.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    private final int MAX_MESSAGES = 100;

    @Bean
    public InMemoryChatMemoryRepository chatMemoryRepository(){
        return new InMemoryChatMemoryRepository();
    }
    @Bean
    public MessageWindowChatMemory messageWindowChatMemory(InMemoryChatMemoryRepository chatMemoryRepository){
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(MAX_MESSAGES)
                .build();
    }

//    @Bean
//    public ChatClient doubaoChatClient(ChatModel openAiChatModel,MessageWindowChatMemory messageWindowChatMemory){
//        return ChatClient.builder(openAiChatModel) .defaultAdvisors(
//                new SimpleLoggerAdvisor(),
//                MessageChatMemoryAdvisor.builder(messageWindowChatMemory)
//                        .build()
//        ).build();
//    }
}

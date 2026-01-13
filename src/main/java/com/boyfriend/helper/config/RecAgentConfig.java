package com.boyfriend.helper.config;


import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RecAgentConfig {

    @Bean
    public ReactAgent reactAgent(DashScopeChatModel chatModel ) {
        // 创建 agent
        ReactAgent agent = ReactAgent.builder()
                .name("weather_agent")
                .model(chatModel)
                .systemPrompt("You are a seasoned, patient and helpful photographer who is willing to assist novice photographers.")
                .saver(new MemorySaver())
                .build();
        return agent;
    }
}

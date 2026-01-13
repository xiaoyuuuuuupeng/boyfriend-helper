package com.boyfriend.helper.config;


import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeConnectionProperties;
import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeImageProperties;
import com.alibaba.cloud.ai.autoconfigure.dashscope.ResolvedConnectionProperties;
import com.alibaba.cloud.ai.dashscope.api.DashScopeImageApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.image.observation.ImageModelObservationConvention;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;

import static com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeConnectionUtils.resolveConnectionProperties;

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

//    @Bean
//    public DashScopeImageModel analyzeImageModel(DashScopeConnectionProperties commonProperties,
//                                                 DashScopeImageProperties imageProperties, ObjectProvider<RestClient.Builder> restClientBuilderProvider,
//                                                 RetryTemplate retryTemplate, ResponseErrorHandler responseErrorHandler,
//                                                 ObjectProvider<ObservationRegistry> observationRegistry,
//                                                 ObjectProvider<ImageModelObservationConvention> observationConvention) {
//
//        ResolvedConnectionProperties resolved = resolveConnectionProperties(commonProperties, imageProperties, "image");
//
//        var dashScopeImageApi = DashScopeImageApi.builder()
//
//                .apiKey(resolved.apiKey())
//                .baseUrl(resolved.baseUrl())
//                .workSpaceId(resolved.workspaceId())
//                .restClientBuilder(restClientBuilderProvider.getIfAvailable(RestClient::builder))
//                .responseErrorHandler(responseErrorHandler)
//                .build();
//
//        imageProperties.setOptions(DashScopeImageOptions.builder().model("qwen-vl-plus").n(1).build());
//        DashScopeImageModel dashScopeImageModel = new DashScopeImageModel(dashScopeImageApi,
//                imageProperties.getOptions(), retryTemplate,
//                observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP));
//
//        observationConvention.ifAvailable(dashScopeImageModel::setObservationConvention);
//
//        return dashScopeImageModel;
//    }
}

package com.boyfriend.helper.agents;

import com.boyfriend.helper.common.PromptConstants;
import com.boyfriend.helper.utils.ArkServiceUtil;
import com.volcengine.ark.runtime.model.images.generation.GenerateImagesRequest;
import com.volcengine.ark.runtime.model.images.generation.ImagesResponse;
import com.volcengine.ark.runtime.model.images.generation.ResponseFormat;
import com.volcengine.ark.runtime.model.responses.constant.ResponsesConstants;
import com.volcengine.ark.runtime.model.responses.content.InputContentItemImage;
import com.volcengine.ark.runtime.model.responses.content.InputContentItemText;
import com.volcengine.ark.runtime.model.responses.item.ItemEasyMessage;
import com.volcengine.ark.runtime.model.responses.item.MessageContent;
import com.volcengine.ark.runtime.model.responses.request.CreateResponsesRequest;
import com.volcengine.ark.runtime.model.responses.request.ResponsesInput;
import com.volcengine.ark.runtime.model.responses.response.ResponseObject;
import com.volcengine.ark.runtime.service.ArkService;
import dev.langchain4j.agentic.Agent;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

public class PhotoEditAgent {


    private final ArkService service;


    public PhotoEditAgent(ArkService service) {
        this.service = service;
    }

    @Agent(outputKey = "afterUrl", description = "根据用户上传的照片，教导摄影新手如何调整从而拍出好看的照片")
    public String edit(@V("url") String url) {
        GenerateImagesRequest generateRequest = GenerateImagesRequest.builder()
                .model("doubao-seedream-4-5-251128")
                .prompt(PromptConstants.PHOTO_EDIT_PROMPT)
                .image(url)
                .sequentialImageGeneration("disabled")
                .responseFormat(ResponseFormat.Url)
                .stream(false)
                .watermark(false)
                .build();
        System.out.println(generateRequest);
        ImagesResponse imagesResponse = service.generateImages(generateRequest);
        System.out.println(imagesResponse.getData().get(0).getUrl());
        return imagesResponse.getData().get(0).getUrl();
    }

    @PreDestroy
    public void destroy() {
        service.shutdownExecutor();
    }
}

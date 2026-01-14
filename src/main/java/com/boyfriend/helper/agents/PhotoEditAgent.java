package com.boyfriend.helper.agents;

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

public class PhotoEditAgent {


    @Agent(outputKey = "edit",description = "根据用户上传的照片，教导摄影新手如何调整从而拍出好看的照片")
    public String analysis(@V("url") String url){
        String apiKey = System.getenv("ARK_API_KEY");
        // 创建ArkService实例
        ArkService arkService = ArkService.builder().apiKey(apiKey).baseUrl("https://ark.cn-beijing.volces.com/api/v3").build();
        CreateResponsesRequest request = CreateResponsesRequest.builder()
                .model("doubao-seed-1-8-251228")
                .input(ResponsesInput.builder().addListItem(
                        ItemEasyMessage.builder().role(ResponsesConstants.MESSAGE_ROLE_USER).content(
                                MessageContent.builder()
                                        .addListItem(InputContentItemImage.builder().imageUrl("https://ark-project.tos-cn-beijing.volces.com/doc_image/ark_demo_img_1.png").build())
                                        .addListItem(InputContentItemText.builder().text("""
                                                你是一个专业的摄影师助手，你正在帮助那些"不会拍照的男朋友"提高摄影技术。
                                                            请分析这张用户上传的照片：
                                                                1. 构图 (Composition)
                                                                2. 光线 (Lighting)
                                                                3. 角度 (Angle)
                                                                一次只给出1个具体、可以立刻执行的动作行为，不要额外的任何解释，只告诉新手摄影师如何调整即可
                                                                例如:
                                                                错误的回答：拍好看点；正确的回答：试试调整一下曝光，亮屏幕亮起来；
                                                                错误的回答：拍正脸；正确的回答：让人物转过身来；
                                                                错误的回答：要拍全身；正确的回答：让模特往中间来来；试试从下往上拍；
                                                                错误的回答：人物侧身望景，构图更有故事感！正确的回答：让人物左转一点试试～
                                                                字数要短，不要超过 15个字
                                                                语气要专业、鼓励、热情。""").build())
                                        .build()
                        ).build()
                ).build())
                .build();
        ResponseObject resp = arkService.createResponse(request);
        System.out.println(resp);
        arkService.shutdownExecutor();
        return resp.getText().toString();
    }



}

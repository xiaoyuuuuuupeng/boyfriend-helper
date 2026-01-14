package com.boyfriend.helper.agents;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.agentic.Agent;

public interface PhotoPoseAdjustAgent {

    @UserMessage("""
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
                语气要专业、鼓励、热情。
        """)
    @Agent(outputKey = "adjust",description = "根据用户上传的照片，教导摄影新手如何调整从而拍出好看的照片")
    public String adjust(@V("url") String url);


}

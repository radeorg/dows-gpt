package org.dows.gpt.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.gpt.client.LLMClient;
import org.dows.gpt.client.LLMClientFactory;
import org.dows.gpt.entity.GptLockEntity;
import org.dows.gpt.prompt.PromptTemplate;
import org.dows.gpt.request.ChatMessage;
import org.dows.gpt.request.ChatRequest;
import org.dows.gpt.request.ChatSession;
import org.dows.gpt.response.GptLockResponse;
import org.dows.gpt.utils.TikTokenUtils;
import org.dows.oss.api.FileUploaderApi;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatService {

    //    private final List<LLMClient> llmClients;
    private final LLMClientFactory llmClientFactory;

    private final ChatSessionService chatSessionService;

    private final ChatMessageService chatMessageService;

    private final FileUploaderApi fileUploaderApi;

    private final AsyncGptRecorder asyncGptRecorder;

    private final GptLockService gptLockService;


    public String chat(ChatRequest request) {
        String userPrompt = request.getContent();
        if (StrUtil.equals(request.getProtocol(), "oss")) {
            try {
                request.setContent(fileUploaderApi.downloadFile(userPrompt));
            } catch (Exception e) {
                throw new RuntimeException("oss协议下文件下载失败", e);
            }
        } else if (StrUtil.equals(request.getProtocol(), "http")) {
            try {
                request.setContent(fileUploaderApi.downloadFile(userPrompt));
            } catch (Exception e) {
                throw new RuntimeException("http协议下文件下载失败", e);
            }
        }

//        if (StrUtil.isBlank(request.getContent())) {
//            request.setContent("""
//                    李四
//                    男 | 年龄：25岁 | 籍贯：北京 | 共产党员 | 18794434244
//                    求职意向：算法工程师 | 期望城市：北京
//                    个人优势
//                    擅长领域：深度学习、CV 、图像识别、语义分割、目标检测、自动驾驶感知算法、JavaWeb开发
//                    专业技能：熟悉 Python 、PyTorch 框架、熟悉 Java 、MySQL、SpringMVC、SpringBoot、Office
//                    教育经历
//                    北京工业大学 硕士 软件工程 2012-2015
//                    担任职务：党支部书记；主修课程：深度学习、图像识别、语义分割、遥感图像处理、软件工程、时空大数据
//                    河南工业大学 本科 计算机科学与技术 2008-2012
//                    担任职务：班长、党支部书记；主修课程：Java、数据库、操作系统、计算机网络、数据结构
//                    实习经历
//                    大模型自然语言处理科技（北京）有限公司 算法工程师 2023.12-2024.03
//                    ● 负责数据采集、清洗并标注2D、3D驾驶数据，确保数据质量和多样性
//                    ● 负责自动驾驶感知模块的算法开发与优化，利用行车影像数据进行模型迭代优化
//                    项目经历
//                    图像识别 总负责人 2022.09-至今
//                    ● 设计了一种高性能深度学习网络 MT-AENet ，用于遥感图像中建筑物提取、建筑垃圾分割、道路提取等任务，并开发出建筑物
//                    总览可视化系统，实现对城市建筑物变化动态监测
//                    信息管理系统（SSM框架） 项目设计师 2024.02-2024.03
//                    负责高校党务信息管理系统的整体架构设计，确保系统功能模块化、高效稳定
//                    技术栈：Java、MySQL、MyBatis、HTML、CSS、JavaScript、Vue.js、AJAX、Spring MVC、Maven、Git
//                    ● 使用 MySQL 数据库设计，MyBatis 框架实现数据持久层的开发，提高 JDBC 开发效率
//                    ● 使用 HTML、CSS 和 JavaScript 技术， 结合 Element 组件库，快速构建响应式前端网页界面
//                    """);
//        }


        String modelType = request.getModelType();
        // 会话历史
        //ChatSession session = getOrCreateSession(request);
        //List<ChatMessage> history = chatMessageService.findLastMessages(session.getId(), 10);
        userPrompt = buildPrompt(null, request.getContent());

        LLMClient client = llmClientFactory.getClient(modelType);
        if (client == null) {
            return "不支持的模型类型：" + modelType;
        }

        String answer = client.chat(request.getPrompt(), userPrompt);
        // todo 计量计费
        Long inputToken = TikTokenUtils.tokens(modelType, userPrompt);
        Long outputToken = TikTokenUtils.tokens(modelType,answer);

        asyncGptRecorder.recordAndMaybeLock(
                request.getAppId(),
                request.getOperatorId(),
                request.getJdName(),
                request.getFileName(),
                request.getOrgRootId(),
                request.getOrgTreeId(),
                inputToken,
                outputToken
        );


        //todo 保留QA
        //chatMessageService.insert(new ChatMessageEntity("", "", prompt, "user", inputTokens, modelType));
        //chatMessageService.insert(new ChatMessageEntity("", "", answer, "assistant", outputTokens, modelType));

        return answer;
    }

    public Integer getLocked(String appId){
        return asyncGptRecorder.getLocked(appId);
    }

    public GptLockResponse statistics( String appId){
        GptLockEntity entity = gptLockService.getOne(QueryWrapper.create()
                .eq(GptLockEntity::getAppId, appId)
                .eq(GptLockEntity::getDeleted, 0)
        );
        return BeanUtil.copyProperties(entity, GptLockResponse.class);
    }


    /**
     * 构建提示词
     *
     * @param content       内容
     * @param structureJson 结构描述
     * @param example       示例数据
     * @return
     */
    private String buildExtractPrompt(String content, String structureJson, String example) {
        String resumeContext = "";
        Map<String, String> values = new HashMap<>();
        values.put("content", content);
        values.put("structureJson", structureJson);
        String prompContext = PromptTemplate.getSystemPrompt(values, PromptTemplate.CN);

        return resumeContext + prompContext;
    }


    private String buildPrompt(List<ChatMessage> history, String newPrompt) {
        StringBuilder sb = new StringBuilder();
        if (history != null) {
            for (ChatMessage msg : history) {
                sb.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
            }
        }
        // TODO 历史会话拼接
        sb.append("user: ").append(newPrompt);
        return sb.toString();
    }

    private ChatSession getOrCreateSession(ChatRequest request) {
        if (request.getSessionId() != null) {
            return chatSessionService.findById(request.getSessionId());
        }
        ChatSession session = new ChatSession();
        session.setUserId(request.getUserId());
        session.setModelType(request.getModelType());
        chatSessionService.insert(session);
        return session;
    }
}

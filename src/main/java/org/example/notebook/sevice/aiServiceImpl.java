package org.example.notebook.sevice;

import org.example.notebook.pojo.OcrResult;
import org.example.notebook.pojo.mistake;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionContentPart;
import com.volcengine.ark.runtime.model.completion.chat.ChatCompletionRequest;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessage;
import com.volcengine.ark.runtime.model.completion.chat.ChatMessageRole;
import com.volcengine.ark.runtime.service.ArkService;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

@Service
public class aiServiceImpl implements aiService{

    static ResourceBundle bundle = ResourceBundle.getBundle("application");
    private static String apiKey = bundle.getString("ark.api.key");

    // 此为默认路径，您可根据业务所在地域进行配置
    static String baseUrl = "https://ark.cn-beijing.volces.com/api/v3";
    static ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
    Dispatcher dispatcher = new Dispatcher();
    ArkService service = ArkService.builder().
            dispatcher(dispatcher).
            connectionPool(connectionPool).
            baseUrl(baseUrl).apiKey(apiKey).
            build();

    @Override
    public OcrResult recognizeImage(MultipartFile imageFile) {
        try {
            final List<ChatMessage> messages = new ArrayList<>();
            final List<ChatCompletionContentPart> multiParts = new ArrayList<>();

            // 读取图片文件并转换为base64
            byte[] imageBytes = imageFile.getBytes();
            String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
            String imageDataUrl = "data:image/jpeg;base64," + base64Image;

            // 正确设置图片URL为base64格式
            multiParts.add(ChatCompletionContentPart.builder().type("image_url").imageUrl(
                    new ChatCompletionContentPart.ChatCompletionContentPartImageURL(
                            imageDataUrl)
            ).build() );

            multiParts.add(ChatCompletionContentPart.builder().type("text").text(
                    "请提取图片中的题目信息，并严格按照以下JSON格式返回结果：\n" +
                            "{\n" +
                            "  \"subject\": \"必须从这个列表中选择：数学,语文,英语,物理,化学,生物,历史,地理,政治\",\n" +
                            "  \"questionType\": \"必须从这个列表中选择：选择题,填空题,解答题,判断题,计算题,应用题,实验题,其他\",\n" +
                            "  \"chapter\": \"章节知识点\",\n" +
                            "  \"question\": \"题目内容\",\n" +
                            "  \"correctAnswer\": \"正确答案\",\n" +
                            "  \"explanation\": \"解析说明\"\n" +
                            "}\n" +
                            "严格遵守上述格式，只返回JSON，不要包含其他文字。如果某些字段无法识别，请留空字符串。"
            ).build());

            final ChatMessage userMessage = ChatMessage.builder().role(ChatMessageRole.USER)
                    .multiContent(multiParts).build();
            messages.add(userMessage);

            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model("doubao-seed-1-6-251015")
                    .messages(messages)
                    .thinking(new ChatCompletionRequest.ChatCompletionRequestThinking("disabled"))
                    .reasoningEffort("minimal")
                    .build();

            String result = (String) service.createChatCompletion(chatCompletionRequest)
                    .getChoices().get(0).getMessage().getContent();

            // 解析返回的JSON字符串
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(result);

            OcrResult ocrResult = new OcrResult();
            ocrResult.setQuestion(jsonNode.has("question") ? jsonNode.get("question").asText() : "");
            ocrResult.setSubject(jsonNode.has("subject") ? jsonNode.get("subject").asText() : "");
            ocrResult.setQuestionType(jsonNode.has("questionType") ? jsonNode.get("questionType").asText() : "");
            ocrResult.setChapter(jsonNode.has("chapter") ? jsonNode.get("chapter").asText() : "");
            ocrResult.setCorrectAnswer(jsonNode.has("correctAnswer") ? jsonNode.get("correctAnswer").asText() : "");
            ocrResult.setExplanation(jsonNode.has("explanation") ? jsonNode.get("explanation").asText() : "");

            return ocrResult;
        } catch (Exception e) {
            System.err.println("OCR识别失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("OCR识别失败", e);
        }
    }

    @Override
    public OcrResult askQuestion(String question) {
        try {
            final List<ChatMessage> messages = new ArrayList<>();

            final ChatMessage userMessage = ChatMessage.builder()
                    .role(ChatMessageRole.USER)
                    .content("请用纯文本回答问题，不要使用任何Markdown格式、HTML标签或特殊符号。保持回答简洁明了。"+ question)
                    .build();
            messages.add(userMessage);

            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model("doubao-seed-1-6-251015")
                    .messages(messages)
                    .reasoningEffort("minimal")
                    .build();

            String result = (String) service.createChatCompletion(chatCompletionRequest)
                    .getChoices().get(0).getMessage().getContent();

            OcrResult ocrResult = new OcrResult();
            ocrResult.setQuestion(result);
            return ocrResult;
        } catch (Exception e) {
            System.err.println("AI问答失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("AI问答失败", e);
        }
    }
    @Override
    public List<mistake> generateSimilarQuestions(List<mistake> originalQuestions, int count) throws Exception {
        // 构建提示词
        StringBuilder prompt = new StringBuilder();
        prompt.append("基于以下题目生成").append(count).append("道相似题目，要求知识点相同但问法不同（支持题型不同）:\n\n");

        for(int i = 0; i < originalQuestions.size(); i++) {
            mistake q = originalQuestions.get(i);
            prompt.append("题目").append(i+1).append(":\n");
            prompt.append("科目: ").append(q.getSubject()).append("\n");
            prompt.append("题型: ").append(q.getQuestionType()).append("\n");
            prompt.append("章节知识点: ").append(q.getChapter()).append("\n");
            prompt.append("题目内容: ").append(q.getQuestion()).append("\n");
            prompt.append("正确答案: ").append(q.getCorrectAnswer()).append("\n");
            prompt.append("解析: ").append(q.getExplanation()).append("\n\n");
        }

        // 修改提示词，使其更明确地指导AI输出格式
        prompt.append("请严格按照以下格式返回生成的题目，不要包含任何其他文字:\n");
        prompt.append("题目1:\n");
        prompt.append("科目: 数学\n");
        prompt.append("题型: 选择题\n");
        prompt.append("章节知识点: 这里是章节知识点（用6字以内简短中文描述）\n");
        prompt.append("题目内容: 这里是题目内容\n");
        prompt.append("正确答案: A\n");
        prompt.append("解析: 这里是解析内容\n\n");
        prompt.append("题目2:\n");
        prompt.append("科目: 物理\n");
        prompt.append("题型: 解答题\n");
        prompt.append("章节知识点: 这里是章节知识点\n");
        prompt.append("题目内容: 这里是题目内容\n");
        prompt.append("正确答案: 这里是正确答案\n");
        prompt.append("解析: 这里是解析内容\n\n");
        prompt.append("以此类推，总共生成").append(count).append("道题目。");

        // 调用AI API
        OcrResult result = askQuestion(prompt.toString());

        System.out.println(result.getQuestion());
        // 解析返回的结果为题目列表
        // 这里需要根据实际返回格式进行解析
        return parseGeneratedQuestions(result.getQuestion(), count);
    }



    private List<mistake> parseGeneratedQuestions(String generatedText, int expectedCount) {
        List<mistake> questions = new ArrayList<>();

        try {
            // 如果返回的是JSON格式，尝试直接解析
            if (generatedText.trim().startsWith("[") && generatedText.trim().endsWith("]")) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonArray = mapper.readTree(generatedText);

                for (JsonNode questionNode : jsonArray) {
                    mistake m = new mistake();
                    m.setSubject(questionNode.has("subject") ? questionNode.get("subject").asText() : "");
                    m.setQuestionType(questionNode.has("questionType") ? questionNode.get("questionType").asText() : "");
                    m.setQuestion(questionNode.has("question") ? questionNode.get("question").asText() : "");
                    m.setCorrectAnswer(questionNode.has("correctAnswer") ? questionNode.get("correctAnswer").asText() : "");
                    m.setExplanation(questionNode.has("explanation") ? questionNode.get("explanation").asText() : "");
                    m.setChapter(questionNode.has("chapter") ? questionNode.get("chapter").asText() : ""); // 添加这一行
                    questions.add(m);
                }
            } else {
                // 文本格式解析 - 简化版本
                questions = parseSimpleTextFormatQuestions(generatedText);
            }
        } catch (Exception e) {
            System.err.println("解析AI生成题目失败: " + e.getMessage());
            // 返回空列表而不是抛出异常
            return new ArrayList<>();
        }

        return questions;
    }

    private List<mistake> parseSimpleTextFormatQuestions(String generatedText) {
        List<mistake> questions = new ArrayList<>();

        if (generatedText == null || generatedText.trim().isEmpty()) {
            return questions;
        }

        // 按题目分割
        String[] questionBlocks = generatedText.split("(?=题目\\d+:)");

        for (String block : questionBlocks) {
            if (block.trim().isEmpty()) continue;

            try {
                mistake m = new mistake();

                // 设置默认值避免null
                m.setQuestion("题目内容未识别");
                m.setSubject("未知");
                m.setQuestionType("未知");
                m.setChapter(""); // 添加默认章节知识点

                // 提取题目内容 - 改进逻辑以包含选择题选项
                // 先检查是否有选项信息，如果有则包含选项
                java.util.regex.Pattern typePattern = java.util.regex.Pattern.compile("题型:\\s*(.+?)(?=\\n|$)");
                java.util.regex.Matcher typeMatcher = typePattern.matcher(block);
                String questionType = "";
                if (typeMatcher.find()) {
                    questionType = typeMatcher.group(1).trim();
                }

                // 根据题型使用不同的提取策略
                if ("选择题".equals(questionType)) {
                    // 对于选择题，提取从"题目内容:"到"正确答案:"之间的所有内容（包含选项）
                    java.util.regex.Pattern choiceQuestionPattern = java.util.regex.Pattern.compile(
                            "题目内容:\\s*(.+?)(?=\\n正确答案:|\\n答案:)",
                            java.util.regex.Pattern.DOTALL);
                    java.util.regex.Matcher choiceQuestionMatcher = choiceQuestionPattern.matcher(block);
                    if (choiceQuestionMatcher.find()) {
                        m.setQuestion(choiceQuestionMatcher.group(1).trim());
                    }
                } else {
                    // 对于非选择题，使用原来的提取方式
                    java.util.regex.Pattern questionPattern = java.util.regex.Pattern.compile(
                            "题目内容:\\s*(.+?)(?=\\n科目:|\\n题型:|\\n正确答案:|\\n题目\\d+:|\\n\\s*$)",
                            java.util.regex.Pattern.DOTALL);
                    java.util.regex.Matcher questionMatcher = questionPattern.matcher(block);
                    if (questionMatcher.find()) {
                        m.setQuestion(questionMatcher.group(1).trim());
                    }
                }

                // 提取科目
                java.util.regex.Pattern subjectPattern = java.util.regex.Pattern.compile("科目:\\s*(.+?)(?=\\n|$)");
                java.util.regex.Matcher subjectMatcher = subjectPattern.matcher(block);
                if (subjectMatcher.find()) {
                    m.setSubject(subjectMatcher.group(1).trim());
                }

                // 提取题型
                if (!typeMatcher.find()) { // 如果之前已经find过，需要重新创建matcher
                    typeMatcher = typePattern.matcher(block);
                    if (typeMatcher.find()) {
                        m.setQuestionType(typeMatcher.group(1).trim());
                    }
                } else {
                    m.setQuestionType(questionType);
                }

                // 提取章节知识点
                java.util.regex.Pattern chapterPattern = java.util.regex.Pattern.compile("章节知识点:\\s*(.+?)(?=\\n|$)");
                java.util.regex.Matcher chapterMatcher = chapterPattern.matcher(block);
                if (chapterMatcher.find()) {
                    m.setChapter(chapterMatcher.group(1).trim());
                }

                // 提取正确答案
                java.util.regex.Pattern answerPattern = java.util.regex.Pattern.compile("正确答案:\\s*(.+?)(?=\\n|$)");
                java.util.regex.Matcher answerMatcher = answerPattern.matcher(block);
                if (answerMatcher.find()) {
                    m.setCorrectAnswer(answerMatcher.group(1).trim());
                }

                // 提取解析
                java.util.regex.Pattern explanationPattern = java.util.regex.Pattern.compile("解析:\\s*(.+?)(?=\\n题目\\d+:|$)");
                java.util.regex.Matcher explanationMatcher = explanationPattern.matcher(block);
                if (explanationMatcher.find()) {
                    m.setExplanation(explanationMatcher.group(1).trim());
                }

                questions.add(m);
            } catch (Exception e) {
                System.err.println("解析单个题目块失败: " + e.getMessage());
            }
        }

        return questions;
    }
}

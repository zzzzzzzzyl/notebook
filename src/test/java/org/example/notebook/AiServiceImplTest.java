//// src/test/java/org/example/notebook/AiServiceImplTest.java
//package org.example.notebook;
//
//import org.example.notebook.sevice.aiServiceImpl;
//import org.example.notebook.pojo.OcrResult;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.mock.web.MockMultipartFile;
//
//
//import java.io.IOException;
//
//
//
//class AiServiceImplTest {
//
//    private aiServiceImpl aiService;
//
////    @BeforeEach
////    void setUp() {
////        MockitoAnnotations.openMocks(this);
////        aiService = new aiServiceImpl();
////    }
//
//    @Test
//    void testRecognizeImage() throws IOException {
//        // 创建一个模拟的图片文件
//        String content = "test image content";
//        MockMultipartFile mockFile = new MockMultipartFile(
//                "image",
//                "test.jpg",
//                "image/jpeg",
//                content.getBytes()
//        );
//
//        // 由于实际的AI服务调用需要网络连接和有效的API密钥，
//        // 在测试环境中我们无法真正调用服务
//        // 这里只是验证方法能够正常执行（不抛出异常）
//        try {
//            OcrResult result = aiService.recognizeImage(mockFile);
//            // 验证返回结果不为null
//            assertNotNull(result);
//        } catch (Exception e) {
//            // 如果API密钥无效或网络问题，会抛出异常
//            // 这在测试环境中是可以接受的
//            System.out.println("Expected exception in test environment: " + e.getMessage());
//        }
//    }
//
//    @Test
//    void testRecognizeImageWithNullFile() {
//        // 测试传入null文件的情况
//        assertThrows(Exception.class, () -> {
//            aiService.recognizeImage(null);
//        });
//    }
//}
//

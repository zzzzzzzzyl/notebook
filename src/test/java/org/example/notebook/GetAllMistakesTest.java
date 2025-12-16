//package org.example.notebook;
//
//import org.example.notebook.pojo.mistake;
//import org.example.notebook.sevice.mistakeService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//public class GetAllMistakesTest {
//
//    @Autowired
//    private mistakeService mistakeService;
//
//    @Test
//    public void testGetAllMistakes() {
//        // 调用服务方法获取所有错题
//        List<mistake> mistakes = mistakeService.getalllist();
//
//        // 验证返回结果不为null
//        assertNotNull(mistakes, "返回的错题列表不应为null");
//
//        // 打印结果以便查看（可选）
//        System.out.println("共找到 " + mistakes.size() + " 条错题记录");
//        for (mistake m : mistakes) {
//            System.out.println("ID: " + m.getId() + ", Subject: " + m.getSubject() +
//                               ", Question: " + m.getQuestion());
//        }
//    }
//}

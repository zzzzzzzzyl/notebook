//package org.example.notebook.Exception;
//
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(Exception.class)
//    @ResponseBody
//    public Map<String, Object> handleException(Exception e) {
//        Map<String, Object> result = new HashMap<>();
//        result.put("success", false);
//        result.put("message", "服务器内部错误");
//        return result;
//    }
//}
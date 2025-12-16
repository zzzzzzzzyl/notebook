package org.example.notebook.controller;

import jakarta.servlet.http.HttpSession;
import org.example.notebook.pojo.OcrResult;
import org.example.notebook.pojo.mistake;
import org.example.notebook.pojo.user;
import org.example.notebook.sevice.mistakeService;
import org.example.notebook.sevice.notebookService;
import org.example.notebook.sevice.aiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/mistake")
public class mistakeController {
    @Autowired
    private mistakeService mistakeService;
    @Autowired
    private aiService aiService;

    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addMistake(@ModelAttribute mistake mistake,
                                                          @RequestParam("image") MultipartFile imageFile,
                                                          HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 获取当前登录用户
            user currentUser = (user) session.getAttribute("currentUser");
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.ok(response);
            }

            // 设置用户ID
            mistake.setUserId(currentUser.getId());

            // 如果有上传图片文件，则保存到static/images目录
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    // 使用相对路径保存图片到static目录
                    String uploadDir = uploadPath;
                    // 创建目录
                    File dir = new File(uploadDir);
                    if (!dir.exists()) {
                        boolean created = dir.mkdirs();
                        if (!created) {
                            throw new RuntimeException("无法创建目录: " + uploadDir);
                        }
                    }

                    // 生成唯一文件名
                    String originalFilename = imageFile.getOriginalFilename();
                    String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    String uniqueFilename = System.currentTimeMillis() + "_" +
                            Math.round(Math.random() * 1000) + extension;

                    // 保存文件
                    File dest = new File(dir, uniqueFilename);
                    imageFile.transferTo(dest);

                    // 设置图片路径到mistake对象（Web可访问路径）
                    mistake.setQuestionImage("/images/" + uniqueFilename);
                } catch (Exception e) {
                    response.put("success", false);
                    response.put("message", "文件上传失败: " + e.getMessage());
                    return ResponseEntity.ok(response);
                }
            }

            // 保存错题信息

            int result = mistakeService.addMistake(mistake);
            if (result > 0) {



                response.put("success", true);
                response.put("message", "错题添加成功");
            } else {
                response.put("success", false);
                response.put("message", "错题添加失败");
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "错题添加失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    // OCR识别接口
    @PostMapping("/ocr")
    @ResponseBody
    public Map<String, Object> ocrImage(@RequestParam("image") MultipartFile imageFile) {

        Map<String, Object> result = new HashMap<>();

        try {
            // 调用AI服务进行OCR识别
            OcrResult ocrResult = aiService.recognizeImage(imageFile);


            result.put("success", true);
            result.put("question", ocrResult.getQuestion());
            result.put("subject", ocrResult.getSubject());
            result.put("questionType", ocrResult.getQuestionType());
            result.put("chapter", ocrResult.getChapter());
            result.put("correctAnswer", ocrResult.getCorrectAnswer());
            result.put("explanation", ocrResult.getExplanation());
            System.out.println("OCR识别结果: " + ocrResult);
        } catch (Exception e) {
            System.err.println("OCR识别异常: " + e.getMessage());
            e.printStackTrace();
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }



    @GetMapping("/get/{id}")
    @ResponseBody
    public mistake getMistakeById(@PathVariable Integer id, HttpSession session) {
        // 获取当前登录用户
        user currentUser = (user) session.getAttribute("currentUser");
        if (currentUser == null) {
            return null; // 如果未登录，返回null
        }

        // 验证该错题是否属于当前用户
        mistake mistake = mistakeService.getMistakeByIdAndUserId(id, currentUser.getId());
        System.out.println(mistake.getQuestionImage());
        return mistake;
    }

    //获取所有错题
    @GetMapping("/list")
    @ResponseBody
    public List<mistake> getMistakesWithFilters(
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer notebookId,
            @RequestParam(required = false) Boolean isMastered,
            HttpSession session) {
        try {
            // 获取当前登录用户
            user currentUser = (user) session.getAttribute("currentUser");
            if (currentUser == null) {
                return new ArrayList<>(); // 如果未登录，返回空列表
            }

            // 调用服务层方法，传入用户ID
            return mistakeService.getMistakesWithFilters(subject, difficulty, search, notebookId, isMastered, currentUser.getId());
        } catch (Exception e) {
            System.err.println("获取所有错题异常: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 软删除错题
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> softDeleteMistake(@PathVariable Integer id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取当前登录用户
            user currentUser = (user) session.getAttribute("currentUser");
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.ok(response);
            }

            // 验证该错题是否属于当前用户
            mistake mistake = mistakeService.getMistakeByIdAndUserId(id, currentUser.getId());
            if (mistake == null) {
                response.put("success", false);
                response.put("message", "无权操作此错题");
                return ResponseEntity.ok(response);
            }

            // 执行删除操作
            mistakeService.softDeleteMistake(id);
            response.put("success", true);
            response.put("message", "错题删除成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "错题删除失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateMistake(
            @ModelAttribute mistake mistake,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 获取当前登录用户
            user currentUser = (user) session.getAttribute("currentUser");
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.ok(response);
            }

            // 验证该错题是否属于当前用户
            mistake existingMistake = mistakeService.getMistakeByIdAndUserId(mistake.getId(), currentUser.getId());
            if (existingMistake == null) {
                response.put("success", false);
                response.put("message", "无权操作此错题");
                return ResponseEntity.ok(response);
            }
            // 如果有上传图片文件，则保存到static/images目录
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    // 使用相对路径保存图片到static目录
                    String uploadDir = uploadPath;
                    // 创建目录
                    File dir = new File(uploadDir);
                    if (!dir.exists()) {
                        boolean created = dir.mkdirs();
                        if (!created) {
                            throw new RuntimeException("无法创建上传目录");
                        }
                    }

                    // 生成唯一文件名
                    String originalFilename = imageFile.getOriginalFilename();
                    String extension = "";
                    if (originalFilename != null && originalFilename.contains(".")) {
                        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    }
                    String fileName = UUID.randomUUID().toString() + extension;

                    // 保存文件
                    String filePath = uploadDir + fileName;
                    imageFile.transferTo(new File(filePath));

                    // 设置图片URL到错题对象中
                    mistake.setQuestionImage("/images/" + fileName);
                } catch (IOException e) {
                    response.put("success", false);
                    response.put("message", "图片上传失败: " + e.getMessage());
                    return ResponseEntity.ok(response);
                }
            }

            // 更新错题信息
            int result = mistakeService.updateMistake(mistake);
            if (result > 0) {
                response.put("success", true);
                response.put("message", "错题更新成功");
            } else {
                response.put("success", false);
                response.put("message", "错题更新失败");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "服务器内部错误: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/ask-ai")
    @ResponseBody
    public Map<String, Object> askAi(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long mistakeId = Long.valueOf(request.get("mistakeId").toString());
            String question = request.get("question").toString();

            // 获取错题信息
            mistake mistake = mistakeService.getMistakeById(Math.toIntExact(mistakeId));

            // 构造发送给AI的完整问题
            String fullQuestion = "关于这道题目: \n" +
                    mistake.getQuestion() +
                    "\n用户的问题: " + question;

            // 调用AI服务
            OcrResult aiResponse = null;
            try {
                aiResponse = aiService.askQuestion(fullQuestion);
            } catch (Exception e) {
                System.err.println("AI服务暂时不可用: " + e.getMessage());
                e.printStackTrace();
            }

            response.put("success", true);
            response.put("answer", aiResponse.getQuestion()); // 这里复用OcrResult对象
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "AI服务暂时不可用");
            e.printStackTrace();
        }

        return response;
    }


    //标记错题为已掌握
    @PostMapping("/markAsMastered/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markAsMastered(@PathVariable Integer id,
                                                              @RequestParam Boolean isMastered,
                                                              HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取当前登录用户
            user currentUser = (user) session.getAttribute("currentUser");
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return ResponseEntity.ok(response);
            }

            // 验证该错题是否属于当前用户
            mistake mistake = mistakeService.getMistakeByIdAndUserId(id, currentUser.getId());
            if (mistake == null) {
                response.put("success", false);
                response.put("message", "无权操作此错题");
                return ResponseEntity.ok(response);
            }

            // 标记为已掌握
            mistakeService.markAsMastered(id, isMastered);
            response.put("success", true);
            response.put("message", "标记成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "标记失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    //增加错题错误次数

    @PostMapping("/incrementMistakeCount/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> incrementMistakeCount(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            mistakeService.
                    incrementMistakeCount(id);
            response.put("success", true);
            response.put("message", "错误次数增加成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "操作失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    //批量增加次数
    @PostMapping("/save-practice-result")
    @ResponseBody
    public Map<String, Object> savePracticeResult(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 获取请求参数
            String mode = (String) request.get("mode");

            // 修复类型转换问题：正确处理 wrongMistakeIds
            List<Integer> wrongMistakeIds = new ArrayList<>();
            Object wrongMistakeIdsObj = request.get("wrongMistakeIds");

            if (wrongMistakeIdsObj instanceof List) {
                List<?> wrongIdsList = (List<?>) wrongMistakeIdsObj;
                for (Object idObj : wrongIdsList) {
                    if (idObj instanceof Integer) {
                        wrongMistakeIds.add((Integer) idObj);
                    } else if (idObj instanceof String) {
                        try {
                            wrongMistakeIds.add(Integer.parseInt((String) idObj));
                        } catch (NumberFormatException e) {
                            System.err.println("无法解析ID: " + idObj);
                        }
                    } else if (idObj instanceof Number) {
                        wrongMistakeIds.add(((Number) idObj).intValue());
                    }
                }
            }

            // 处理错题次数增加逻辑 - 只对做错的题目增加错误次数
            if (!wrongMistakeIds.isEmpty()) {
                for (Integer mistakeId : wrongMistakeIds) {
                    try {
                        mistakeService.incrementMistakeCount(mistakeId);
                    } catch (Exception e) {
                        System.err.println("增加错题ID " + mistakeId + " 的错误次数失败: " + e.getMessage());
                    }
                }
            }

            // 处理错题详细信息（知识点和用户答案）
            Object wrongQuestionDetailsObj = request.get("wrongQuestionDetails");
            if (wrongQuestionDetailsObj instanceof List) {
                List<Map<String, Object>> wrongQuestionDetails = (List<Map<String, Object>>) wrongQuestionDetailsObj;
                // 这里可以将错题详细信息存储到数据库或其他处理
                for (Map<String, Object> detail : wrongQuestionDetails) {
                    System.out.println("错题详情 - 知识点: " + detail.get("chapter") +
                            ", 用户答案: " + detail.get("userAnswer") +
                            ", 正确答案: " + detail.get("correctAnswer"));
                    // 可以在这里添加将错题详情保存到数据库的逻辑
                }
            }

            // 如果是举一反三模式，且有新错题需要添加到错题本
            if ("enhanced".equals(mode) && request.containsKey("newMistakes")) {
                List<Map<String, Object>> newMistakes = (List<Map<String, Object>>) request.get("newMistakes");

                for (Map<String, Object> newMistakeData : newMistakes) {
                    try {
                        // 创建新的错题对象
                        mistake newMistake = new mistake();

                        // 设置基本属性
                        newMistake.setSubject((String) newMistakeData.get("subject"));
                        newMistake.setQuestionType((String) newMistakeData.get("questionType"));
                        newMistake.setQuestion((String) newMistakeData.get("question"));
                        newMistake.setCorrectAnswer((String) newMistakeData.get("correctAnswer"));
                        newMistake.setMyAnswer((String) newMistakeData.get("myAnswer"));
                        newMistake.setExplanation((String) newMistakeData.get("explanation"));
                        newMistake.setChapter((String) newMistakeData.get("chapter"));

                        // 设置默认值
                        newMistake.setMistakeCount(1); // 新错题默认错误次数为1
                        newMistake.setIsMastered(false); // 默认未掌握

                        // 设置难度级别，默认为中等(2)
                        Object difficultyObj = newMistakeData.get("difficulty");
                        if (difficultyObj instanceof Number) {
                            newMistake.setDifficulty(((Number) difficultyObj).intValue());
                        } else if (difficultyObj instanceof String) {
                            try {
                                newMistake.setDifficulty(Integer.parseInt((String) difficultyObj));
                            } catch (NumberFormatException e) {
                                newMistake.setDifficulty(2); // 默认中等难度
                            }
                        } else {
                            newMistake.setDifficulty(2); // 默认中等难度
                        }

                        // 保存到数据库
                        mistakeService.addMistake(newMistake);
                    } catch (Exception e) {
                        System.err.println("添加新错题失败: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }

            response.put("success", true);
            response.put("message", "练习结果保存成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "保存练习结果失败: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }


    @GetMapping("/batch")
    @ResponseBody
    public Map<String, Object> getMistakesByIds(@RequestParam("ids") String ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 解析ID列表
            String[] idArray = ids.split(",");
            List<Integer> idList = new ArrayList<>();
            for (String idStr : idArray) {
                idList.add(Integer.parseInt(idStr.trim()));
            }

            // 批量获取错题
            List<mistake> mistakes = mistakeService.getMistakesByIds(idList);

            response.put("success", true);
            response.put("data", mistakes);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取错题失败: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }
    @PostMapping("/generate-similar-questions")
    @ResponseBody
    public Map<String, Object> generateSimilarQuestions(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Map<String, Object>> questionList = (List<Map<String, Object>>) request.get("questions");
            Integer count = (Integer) request.get("count");

            // 转换为mistake对象
            List<mistake> originalQuestions = questionList.stream().map(map -> {
                mistake m = new mistake();
                m.setSubject((String) map.get("subject"));
                m.setQuestionType((String) map.get("questionType"));
                m.setQuestion((String) map.get("question"));
                m.setCorrectAnswer((String) map.get("correctAnswer"));
                m.setExplanation((String) map.get("explanation"));
                // 设置其他属性...
                return m;
            }).collect(Collectors.toList());

            // 调用AI服务生成相似题目
            List<mistake> similarQuestions = aiService.generateSimilarQuestions(originalQuestions, count != null ? count : originalQuestions.size());

            response.put("success", true);
            response.put("data", similarQuestions);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "生成相似题目失败: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }
}

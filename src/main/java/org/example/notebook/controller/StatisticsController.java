package org.example.notebook.controller;

import jakarta.servlet.http.HttpSession;
import org.example.notebook.pojo.mistake;
import org.example.notebook.pojo.user;
import org.example.notebook.sevice.mistakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private mistakeService mistakeService;

    /**
     * 获取仪表板统计数据
     */
    @GetMapping("/dashboard")
    public Map<String, Object> getLearningStats(HttpSession session) {
        Map<String, Object> stats = new HashMap<>();
        Map<String, Object> response = new HashMap<>();

        try {
            // 获取当前登录用户
            user currentUser = (user) session.getAttribute("currentUser");
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return response;
            }

            // 获取总错题数
            int totalMistakes = mistakeService.getTotalMistakes(currentUser.getId());

            // 获取上周错题数
            int lastWeekMistakes = mistakeService.getLastWeekMistakes(currentUser.getId());

            // 计算较上周变化
            int weeklyChange = totalMistakes - lastWeekMistakes;

            // 获取待复习数量（今天需要复习的）
            int pendingReview = mistakeService.getPendingReviewCount(currentUser.getId());

            // 获取已掌握数量
            int mastered = mistakeService.getMasteredCount(currentUser.getId());

            // 计算正确率
            double accuracyRate = totalMistakes > 0 ? (double) mastered / totalMistakes * 100 : 0;

            // 获取薄弱科目（错误最多的科目）
            String weakSubject = mistakeService.getWeakSubject(currentUser.getId());

            // 处理空值情况
            if (weakSubject == null) {
                weakSubject = "暂无";
            }

            // 获取薄弱科目未掌握题目数
            int weakSubjectUnmastered = 0;
            if (!"暂无".equals(weakSubject)) {
                weakSubjectUnmastered = mistakeService.getUnmasteredBySubject(weakSubject, currentUser.getId());
            }

            stats.put("totalMistakes", totalMistakes);
            stats.put("weeklyChange", weeklyChange);
            stats.put("pendingReview", pendingReview);
            stats.put("mastered", mastered);
            stats.put("accuracyRate", String.format("%.1f", accuracyRate));
            stats.put("weakSubject", weakSubject);
            stats.put("weakSubjectUnmastered", weakSubjectUnmastered);

            response.put("success", true);
            response.put("data", stats);
        } catch (Exception e) {
            e.printStackTrace(); // 实际项目中应使用日志记录
            response.put("success", false);
            response.put("message", "获取统计数据失败: " + e.getMessage());
        }

        return response;
    }

    /**
     * 获取错题趋势数据
     */
    @GetMapping("/trend")
    public Map<String, Object> getMistakeTrend(HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取当前登录用户
            user currentUser = (user) session.getAttribute("currentUser");
            if (currentUser == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "用户未登录");
                return errorResponse;
            }

            // 获取一周内每天的新增错题数和已掌握错题数
            List<Integer> newMistakes = mistakeService.getWeeklyNewMistakes(currentUser.getId());
            List<Integer> masteredMistakes = mistakeService.getWeeklyMasteredMistakes(currentUser.getId());

            if (newMistakes.size() != 7) {
                // 填充默认值
                while (newMistakes.size() < 7) {
                    newMistakes.add(0);
                }
            }

            if (masteredMistakes.size() != 7) {
                // 填充默认值
                while (masteredMistakes.size() < 7) {
                    masteredMistakes.add(0);
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("newMistakes", newMistakes);
            data.put("masteredMistakes", masteredMistakes);
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "获取数据失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取热门科目数据
     */
    @GetMapping("/popular-subjects")
    public Map<String, Object> getPopularSubjects(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 获取当前登录用户
            user currentUser = (user) session.getAttribute("currentUser");
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return response;
            }

            // 获取所有错题并统计各科目数量
            List<mistake> allMistakes = mistakeService.getalllist(currentUser.getId());

            // 统计各科目错题数量
            Map<String, Integer> subjectCountMap = new HashMap<>();
            for (mistake m : allMistakes) {
                if (m.getSubject() != null && !m.getSubject().trim().isEmpty()) {
                    subjectCountMap.put(m.getSubject(), subjectCountMap.getOrDefault(m.getSubject(), 0) + 1);
                }
            }

            // 转换为列表并排序
            List<Map.Entry<String, Integer>> subjectList = new ArrayList<>(subjectCountMap.entrySet());
            subjectList.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue())); // 按数量降序排序

            // 取前3个科目
            List<Map<String, Object>> topSubjects = new ArrayList<>();
            for (int i = 0; i < Math.min(3, subjectList.size()); i++) {
                Map.Entry<String, Integer> entry = subjectList.get(i);
                Map<String, Object> subjectData = new HashMap<>();
                subjectData.put("subject", entry.getKey());
                subjectData.put("count", entry.getValue());
                topSubjects.add(subjectData);
            }

            response.put("success", true);
            response.put("data", topSubjects);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "获取热门科目失败: " + e.getMessage());
        }

        return response;
    }

    /**
     * 获取最近错题数据
     */
    @GetMapping("/recent")
    @ResponseBody
    public Map<String, Object> getRecentMistakes(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 获取当前登录用户
            user currentUser = (user) session.getAttribute("currentUser");
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return response;
            }

            // 获取最近添加的错题（限制为5条）
            List<mistake> allMistakes = mistakeService.getalllist(currentUser.getId());

            // 按ID降序排序（假设ID越大越新）
            allMistakes.sort((m1, m2) -> m2.getId().compareTo(m1.getId()));

            // 取前5条
            List<mistake> recentMistakes = allMistakes.stream()
                    .limit(5)
                    .collect(Collectors.toList());

            response.put("success", true);
            response.put("data", recentMistakes);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "获取最近错题失败: " + e.getMessage());
        }

        return response;
    }

    /**
     * 生成最近7天的日期标签
     */
    private List<String> generateLast7DaysLabels() {
        // 这里应该根据实际需求生成日期标签
        // 示例返回固定值
        return List.of("周一", "周二", "周三", "周四", "周五", "周六", "周日");
    }
}
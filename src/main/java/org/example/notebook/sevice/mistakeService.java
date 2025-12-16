// src/main/java/org/example/notebook/sevice/mistakeService.java
package org.example.notebook.sevice;

import org.example.notebook.pojo.mistake;

import java.util.List;

public interface mistakeService {
    List<mistake> getMistakesWithFilters(String subject, Integer difficulty, String search,
                                         Integer notebookId, Boolean isMastered, Integer userId); // 添加userId参数

    int addMistake(mistake mistake);

    mistake getMistakeById(Integer id);

    int softDeleteMistake(Integer id);

    int updateMistake(mistake mistake);

    int markAsMastered(Integer id, Boolean isMastered);

    int getTotalMistakes();

    int getUnmasteredMistakes();

    List<Integer> getWeeklyMasteredMistakes();

    mistake getMistakeByIdAndUserId(Integer id, Integer userId);

    void incrementMistakeCount(Integer id);
    List<mistake> getMistakesByIds(List<Integer> ids);

    int getTotalMistakes(Integer userId);
    int getLastWeekMistakes(Integer userId);
    int getPendingReviewCount(Integer userId);
    int getMasteredCount(Integer userId);
    String getWeakSubject(Integer userId);
    int getUnmasteredBySubject(String subject, Integer userId);
    List<Integer> getWeeklyNewMistakes(Integer userId);
    List<Integer> getWeeklyMasteredMistakes(Integer userId);
    List<mistake> getalllist(Integer userId);

}

package org.example.notebook.sevice;

import org.example.notebook.pojo.mistake;
import org.example.notebook.dao.mistakeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class mistakeServiceImpl implements mistakeService {

    @Autowired
    private mistakeMapper mistakeMapper;

    @Override
    public List<mistake> getMistakesWithFilters(String subject, Integer difficulty, String search,
                                                Integer notebookId, Boolean isMastered, Integer userId) {
        return mistakeMapper.getMistakesWithFilters(subject, difficulty, search, notebookId, isMastered, userId);
    }

    @Override
    public int addMistake(mistake mistake) {
        // 插入错题
        int result = mistakeMapper.addMistake(mistake);

        // 如果插入成功，且有用户ID，则建立用户与错题的关联
        if (result > 0 && mistake.getUserId() != null) {
            mistakeMapper.insertUserMistake(mistake.getUserId(), mistake.getId());
        }

        return result;
    }
    @Override
    public mistake getMistakeById(Integer id) {
        return mistakeMapper.getMistakeById(id);
    }

    @Override
    public int softDeleteMistake(Integer id) {
        return mistakeMapper.softDeleteMistake(id);
    }

    @Override
    public int updateMistake(mistake mistake) {
        return mistakeMapper.updateMistake(mistake);
    }

    @Override
    public int markAsMastered(Integer id, Boolean isMastered) {
        return mistakeMapper.markAsMastered(id, isMastered);
    }

    @Override
    public int getTotalMistakes() {
        return mistakeMapper.getTotalMistakes();
    }

    @Override
    public int getUnmasteredMistakes() {
        return mistakeMapper.getUnmasteredMistakes();
    }

    @Override
    public List<Integer> getWeeklyMasteredMistakes() {
        return mistakeMapper.getWeeklyMasteredMistakes();
    }

    @Override
    public mistake getMistakeByIdAndUserId(Integer id, Integer userId) {
        return mistakeMapper.getMistakeByIdAndUserId(id, userId);
    }
    @Override
    public void incrementMistakeCount(Integer id) {
        mistakeMapper.incrementMistakeCount(id);
    }

    @Override
    public List<mistake> getMistakesByIds(List<Integer> ids) {
        return mistakeMapper.getMistakesByIds(ids);
    }

    @Override
    public int getTotalMistakes(Integer userId) {
        return mistakeMapper.getTotalMistakes(userId);
    }

    @Override
    public int getLastWeekMistakes(Integer userId) {
        return mistakeMapper.getLastWeekMistakes(userId);
    }

    @Override
    public int getPendingReviewCount(Integer userId) {
        return mistakeMapper.getPendingReviewCount(userId);
    }

    @Override
    public int getMasteredCount(Integer userId) {
        return mistakeMapper.getMasteredCount(userId);
    }

    @Override
    public String getWeakSubject(Integer userId) {
        return mistakeMapper.getWeakSubject(userId);
    }

    @Override
    public int getUnmasteredBySubject(String subject, Integer userId) {
        return mistakeMapper.getUnmasteredBySubject(subject, userId);
    }

    @Override
    public List<Integer> getWeeklyNewMistakes(Integer userId) {
        return mistakeMapper.getWeeklyNewMistakes(userId);
    }

    @Override
    public List<Integer> getWeeklyMasteredMistakes(Integer userId) {
        return mistakeMapper.getWeeklyMasteredMistakes(userId);
    }

    @Override
    public List<mistake> getalllist(Integer userId) {
        return mistakeMapper.getalllist(userId);
    }
}

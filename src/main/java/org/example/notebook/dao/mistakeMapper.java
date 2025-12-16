package org.example.notebook.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.notebook.pojo.mistake;

import java.util.List;

@Mapper
public interface mistakeMapper {
    // 获取所有错题（带筛选条件）
    List<mistake> getMistakesWithFilters(@Param("subject") String subject,
                                         @Param("difficulty") Integer difficulty,
                                         @Param("search") String search,
                                         @Param("notebookId") Integer notebookId,
                                         @Param("isMastered") Boolean isMastered,
                                         @Param("userId") Integer userId); // 添加userId参数

    // 添加错题
    int addMistake(mistake mistake);
    void insertUserMistake(@Param("userId") Integer userId, @Param("mistakeId") Integer mistakeId);

    // 获取错题详情
    @Select("SELECT m.*, n.name as notebookName FROM mistakes m LEFT JOIN notebooks n ON m.notebook_id = n.id WHERE m.id = #{id}")
    mistake getMistakeById(Integer id);

    // 软删除错题
    int softDeleteMistake(Integer id);

    // 更新错题
    int updateMistake(mistake mistake);

    // 标记为已掌握
    int markAsMastered(@Param("id") Integer id, @Param("isMastered") Boolean isMastered);

    // 获取所有错题数量
    int getTotalMistakes();

    // 获取未掌握错题数量
    int getUnmasteredMistakes();

    // 获取本周每天掌握的错题数
    List<Integer> getWeeklyMasteredMistakes();

    // 根据ID获取错题（用于验证用户权限）
    mistake getMistakeByIdAndUserId(@Param("id") Integer id, @Param("userId") Integer userId);

    int incrementMistakeCount(Integer id);
    List<mistake> getMistakesByIds(@Param("ids") List<Integer> ids);
    int getTotalMistakes(@Param("userId") Integer userId);
    int getLastWeekMistakes(@Param("userId") Integer userId);
    int getPendingReviewCount(@Param("userId") Integer userId);
    int getMasteredCount(@Param("userId") Integer userId);
    String getWeakSubject(@Param("userId") Integer userId);
    int getUnmasteredBySubject(@Param("subject") String subject, @Param("userId") Integer userId);
    List<Integer> getWeeklyNewMistakes(@Param("userId") Integer userId);
    List<Integer> getWeeklyMasteredMistakes(@Param("userId") Integer userId);
    List<mistake> getalllist(@Param("userId") Integer userId);


}
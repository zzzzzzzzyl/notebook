package org.example.notebook.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.notebook.pojo.notebook;
import org.example.notebook.pojo.mistake;

import java.util.List;

@Mapper
public interface notebookMapper {
    List<notebook> getAllNotebooks();
    List<notebook> getUserNotebooks(@Param("userId") Integer userId);
    void addUserNotebook(@Param("userId") Integer userId, @Param("notebookId") Integer notebookId);
    int createNotebook(notebook notebook);
    void addToNotebook(@Param("mistakeId") Integer mistakeId, @Param("notebookId") Integer notebookId);
    void removeFromNotebook(@Param("mistakeId") Integer mistakeId, @Param("notebookId") Integer notebookId);
    List<Integer> getNotebookIdsByMistakeId(Integer mistakeId);}

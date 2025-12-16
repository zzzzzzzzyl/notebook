package org.example.notebook.sevice;

import org.example.notebook.pojo.mistake;
import org.example.notebook.pojo.notebook;

import java.util.List;

public interface notebookService {
    List<notebook> getAllNotebooks();
    List<notebook> getUserNotebooks(Integer userId);
    int createNotebook(notebook notebook, Integer userId);
    void addToNotebook(Integer mistakeId, Integer notebookId);
    void removeFromNotebook(Integer mistakeId, Integer notebookId);
    List<Integer> getNotebookIdsByMistakeId(Integer mistakeId);
}

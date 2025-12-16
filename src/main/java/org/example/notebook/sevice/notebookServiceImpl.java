package org.example.notebook.sevice;

import org.example.notebook.dao.mistakeMapper;
import org.example.notebook.dao.notebookMapper;
import org.example.notebook.pojo.notebook;
import org.example.notebook.pojo.mistake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class notebookServiceImpl implements notebookService {

    @Autowired
    private notebookMapper notebookMapper;


    @Override
    public List<notebook> getAllNotebooks() {
        return notebookMapper.getAllNotebooks();
    }
    @Override
    public List<notebook> getUserNotebooks(Integer userId) {
        return notebookMapper.getUserNotebooks(userId);
    }
    @Override
    public int createNotebook(notebook notebook, Integer userId) {
        // 创建错题集
        int result = notebookMapper.createNotebook(notebook);

        // 如果创建成功，建立用户与错题集的关联
        if (result > 0 && userId != null) {
            notebookMapper.addUserNotebook(userId, notebook.getId());
        }

        return result;
    }
    @Override
    public void addToNotebook(Integer mistakeId, Integer notebookId) {
        notebookMapper.addToNotebook(mistakeId, notebookId);
    }

    @Override
    public void removeFromNotebook(Integer mistakeId, Integer notebookId) {
        notebookMapper.removeFromNotebook(mistakeId, notebookId);
    }

    @Override
    public List<Integer> getNotebookIdsByMistakeId(Integer mistakeId) {
        return notebookMapper.getNotebookIdsByMistakeId(mistakeId);
    }
}

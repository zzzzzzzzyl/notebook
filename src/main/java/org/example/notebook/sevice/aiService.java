package org.example.notebook.sevice;

import org.example.notebook.pojo.OcrResult;
import org.example.notebook.pojo.mistake;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface aiService {
    OcrResult recognizeImage(MultipartFile imageFile);
    OcrResult askQuestion(String question);
    List<mistake> generateSimilarQuestions(List<mistake> originalQuestions, int count) throws Exception;

}

package org.example.notebook.pojo;

import lombok.Data;

@Data
public class OcrResult {
    private String question;
    private String subject;
    private String questionType;
    private String chapter;
    private String correctAnswer;
    private String explanation;
}

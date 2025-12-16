package org.example.notebook.pojo;

import lombok.Data;

import java.util.Date;

@Data
    public class mistake {
    //错题id
    private Integer id;
    //错题所属科目
    private String subject;
    //错题所属章节
    private String chapter;
    //题型
    private String questionType;
    //错题
    private String question;
    //错题图片URL
    private String questionImage;
    //错题难度
    private Integer difficulty;
    //我的答案
    private String myAnswer;
    //正确答案
    private String correctAnswer;
    //答案解释
    private String explanation;
    //笔记
    private String note;
    //是否掌握
    private Boolean isMastered = false;
    //错误次数
    private Integer mistakeCount = 1;
    //错题集id
    private Integer notebookId;
    //软删除
    private Boolean isDeleted = false;
    // 添加错题集名称（用于显示）
    private String notebookName;
    //创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;
    //用户id
    private Integer userId;


    public mistake() {}
}

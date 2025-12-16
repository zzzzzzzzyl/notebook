# -- 删除旧数据库（如果存在）
DROP DATABASE IF EXISTS mistakenote;
#
# -- 创建新数据库
CREATE DATABASE mistakenote DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- 用户表

USE mistakenote;
CREATE TABLE users (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
                       password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
                       avatar VARCHAR(255) COMMENT '头像URL'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


USE mistakeNote;
-- 错题表（存储所有错题的核心信息）
CREATE TABLE mistakes (
                          id INT PRIMARY KEY AUTO_INCREMENT,

                          subject VARCHAR(50) NOT NULL COMMENT '所属科目，如：数学、英语',
                          questiontype VARCHAR(50) DEFAULT NULL COMMENT '题型',
                          chapter VARCHAR(100) COMMENT '所属章节，如：函数、时态',
                          notebook_id INT NULL COMMENT '所属错题集ID',
                          question TEXT NOT NULL COMMENT '题目内容',
                          questionimage VARCHAR(255) COMMENT '题目图片URL',
                          difficulty TINYINT DEFAULT 2 COMMENT '难度等级：1-简单，2-中等，3-困难',
                          myanswer TEXT COMMENT '自己的错误答案',
                          correctanswer TEXT NOT NULL COMMENT '正确答案',
                          explanation TEXT COMMENT '答案解析',
                          note TEXT COMMENT '个人笔记',
                          ismastered BOOLEAN DEFAULT FALSE COMMENT '是否已掌握：0-未掌握，1-已掌握',
                          mistakecount INT DEFAULT 1 COMMENT '错误次数',
                          isdeleted BOOLEAN DEFAULT FALSE COMMENT '软删除标记：0-未删除，1-已删除',
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          last_reviewed_at TIMESTAMP NULL COMMENT '最后复习时间',
                          next_review_at TIMESTAMP NULL COMMENT '下次复习时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 错题集表（用户自定义的错题集合）
CREATE TABLE notebooks (
                           id INT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(100) NOT NULL COMMENT '错题集名称，如：期末复习重点',
                           description TEXT COMMENT '错题集描述',
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户错题关联表（用于多用户支持）
CREATE TABLE user_mistakes (
                               id INT PRIMARY KEY AUTO_INCREMENT,
                               user_id INT NOT NULL COMMENT '用户ID',
                               mistake_id INT NOT NULL COMMENT '错题ID',
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                               FOREIGN KEY (mistake_id) REFERENCES mistakes(id) ON DELETE CASCADE,
                               UNIQUE KEY unique_user_mistake (user_id, mistake_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户错题集关联表（用于多用户支持）
CREATE TABLE user_notebooks (
                                id INT PRIMARY KEY AUTO_INCREMENT,
                                user_id INT NOT NULL COMMENT '用户ID',
                                notebook_id INT NOT NULL COMMENT '错题集ID',
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                FOREIGN KEY (notebook_id) REFERENCES notebooks(id) ON DELETE CASCADE,
                                UNIQUE KEY unique_user_notebook (user_id, notebook_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 1. 第一次阶段考错题集
INSERT INTO notebooks (name, description)
VALUES ('第一次阶段考错题集', '收录本次阶段考错题，针对性补弱');

-- 2. 期中考错题集
INSERT INTO notebooks (name, description)
VALUES ('期中考错题集', '整理期中考各类错题，助力复盘提升');

-- 1. 数学-函数定义域错题（关联错题集1：id=1）
INSERT INTO mistakes (
    subject, questiontype, chapter, notebook_id,
    question, questionimage, difficulty,
    myanswer, correctanswer, explanation, note,
    ismastered, mistakecount, last_reviewed_at, next_review_at
) VALUES (
             '数学', '选择题', '函数定义域', 1,
             '函数 f(x) = √(2x-1) + 1/(x-2) 的定义域是（  ）
             A. x ≥ 1/2  B. x ≥ 1/2 且 x ≠ 2  C. x > 1/2 且 x ≠ 2  D. x > 2',
             '/images/1763458981930.png', 2,
             'A',  -- 错误答案：只考虑根号，忽略分母不为0
             'B',
             '解析：定义域需满足两个条件：① 根号下非负：2x-1 ≥ 0 → x ≥ 1/2；② 分母不为0：x-2 ≠ 0 → x ≠ 2。综上，定义域为 x ≥ 1/2 且 x ≠ 2，选B',
             '注意：分式+根号的定义域需同时满足两个约束条件，下次先列所有限制条件再求解',
             0, 2, '2025-11-10 19:30:00', '2025-11-13 19:30:00'  -- 复习间隔3天
         );

-- 2. 英语-过去完成时错题（关联错题集1：id=1）
INSERT INTO mistakes (
    subject, questiontype, chapter, notebook_id,
    question, questionimage, difficulty,
    myanswer, correctanswer, explanation, note,
    ismastered, mistakecount, last_reviewed_at, next_review_at
) VALUES (
             '英语', '单选题', '过去完成时', 1,
             'By the time we arrived at the station, the train ______ already ______.
             A. has; left  B. had; left  C. will; leave  D. is; leaving',
             '/images/1763459004220.png', 2,
             'A',  -- 错误答案：混淆现在完成时与过去完成时
             'B',
             '解析：by the time + 过去时从句（arrived），主句需用过去完成时（had + 过去分词），表示“过去的过去”发生的动作（火车在我们到达前已离开）。A选项是现在完成时，用于“现在之前”，不符合语境',
             '关键词：by the time + 过去时 → 主句用过去完成时',
             0, 1, '2025-11-15 16:40:00', '2025-11-16 16:40:00'  -- 复习间隔1天
         );

-- 3. 物理-受力分析错题（关联错题集2：id=2）
INSERT INTO mistakes (
    subject, questiontype, chapter, notebook_id,
    question, questionimage, difficulty,
    myanswer, correctanswer, explanation, note,
    ismastered, mistakecount, last_reviewed_at, next_review_at
) VALUES (
             '物理', '填空题', '受力分析', 2,
             '如图所示，一个重5N的木块静止在倾角为30°的斜面上，木块受到的静摩擦力大小为______N（g取10N/kg，忽略空气阻力）',
             '/images/1763459083808.png', 3,
             '5',  -- 错误答案：误将重力直接当作静摩擦力
             '2.5',
             '解析：木块静止时受力平衡，沿斜面方向：静摩擦力 f = G·sinθ（G为重力，θ为斜面倾角）。代入数据：f = 5N × sin30° = 5×0.5 = 2.5N。注意：静摩擦力需根据平衡条件计算，而非等于重力',
             '受力分析步骤：1. 重力；2. 支持力；3. 静摩擦力（沿斜面向上）；4. 沿斜面和垂直斜面分解力',
             0, 2, '2025-11-08 14:20:00', '2025-11-11 14:20:00'  -- 复习间隔3天
         );

-- 4. 化学-有机官能团识别错题（关联错题集2：id=2）
INSERT INTO mistakes (
    subject, questiontype, chapter, notebook_id,
    question, questionimage, difficulty,
    myanswer, correctanswer, explanation, note,
    ismastered, mistakecount, last_reviewed_at, next_review_at
) VALUES (
             '化学', '选择题', '有机化合物', 2,
             '下列有机物的结构简式为 CH₃CH₂COOH，其含有的官能团是（  ）
             A. 羟基（-OH）  B. 羧基（-COOH）  C. 醛基（-CHO）  D. 酯基（-COOR）',
             '/images/1763459214927.png', 1,
             'A',  -- 错误答案：混淆羟基与羧基
             'B',
             '解析：CH₃CH₂COOH 是丙酸，结构中含有的官能团是羧基（-COOH），羧基由羰基（C=O）和羟基（-OH）组成，但整体为羧基，不能单独当作羟基。A选项羟基常见于醇类（如乙醇 CH₃CH₂OH）',
             '记忆：羧基是 -COOH，醇羟基是 -OH，醛基是 -CHO，酯基是 -COO- 连接烃基',
             1, 1, '2025-11-05 10:10:00', '2025-11-20 10:10:00'  -- 已掌握，复习间隔15天
         );

-- 5. 语文-文言文虚词错题（关联错题集2：id=2）
INSERT INTO mistakes (
    subject, questiontype, chapter, notebook_id,
    question, questionimage, difficulty,
    myanswer, correctanswer, explanation, note,
    ismastered, mistakecount, last_reviewed_at, next_review_at
) VALUES (
             '语文', '翻译题', '文言文虚词', 2,
             '下列句子中“之”的用法与其他三项不同的是（  ）
             A. 学而时习之（《论语》）  B. 友人惭，下车引之（《陈太丘与友期行》）
             C. 久之，目似瞑（《狼》）  D. 知之者不如好之者（《论语》）',
             '/images/1763459303655.png', 2,
             'B',  -- 错误答案：未区分代词与语气助词
             'C',
             '解析：A、B、D三项中“之”均为代词，代指前文提到的事物（A代“学过的知识”，B代“元方”，D代“学问”）；C项中“之”是语气助词，无实义，用于补充音节（“久之”表示“过了很久”）',
             '虚词“之”的常见用法：1. 代词（代人/事/物）；2. 结构助词（的）；3. 语气助词（无实义）；4. 宾语前置标志（无实义）',
             0, 1, '2025-11-17 09:50:00', '2025-11-18 09:50:00'  -- 复习间隔1天
         );

-- 6. 数学-导数极值错题（关联错题集2：id=2）
INSERT INTO mistakes (
    subject, questiontype, chapter, notebook_id,
    question, questionimage, difficulty,
    myanswer, correctanswer, explanation, note,
    ismastered, mistakecount, last_reviewed_at, next_review_at
) VALUES (
             '数学', '计算题', '导数应用', 2,
             '求函数 f(x) = x³ - 3x² + 2 的极值（要求写出极大值和极小值）',
             '/images/1763459340034.png', 3,
             '极大值：2，极小值：-2',  -- 错误答案：未验证导数为0的点是否为极值点
             '极大值：2（x=0时），极小值：-2（x=2时）',
             '解析：1. 求导：f’(x) = 3x² - 6x；2. 令f’(x)=0，解得x=0或x=2；3. 验证单调性：x<0时f’(x)>0（递增），0<x<2时f’(x)<0（递减），x>2时f’(x)>0（递增）；4. 结论：x=0是极大值点（f(0)=2），x=2是极小值点（f(2)=-2）',
             '易错点：导数为0的点不一定是极值点，必须通过单调性判断增减性变化',
             0, 3, '2025-11-03 18:10:00', '2025-11-06 18:10:00'  -- 复习间隔3天
         );

-- 默认用户
INSERT INTO users (username, password, avatar) VALUES (
            'admin',
            '123456',
            '/images/avatar/user.png'

    );
INSERT INTO user_notebooks (user_id, notebook_id)
SELECT 1, id FROM notebooks;

INSERT INTO user_mistakes (user_id, mistake_id)
SELECT 1, id FROM mistakes;
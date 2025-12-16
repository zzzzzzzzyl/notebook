package org.example.notebook.pojo;

import lombok.Data;

@Data
public class notebook {
    private Integer id;
    private String name;
    private String description;
    private Integer userId;
}
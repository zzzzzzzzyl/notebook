package org.example.notebook.pojo;

import lombok.Data;

@Data
public class user {
    private Integer id;
    private String username;
    private String password;
    private String avatar;


    // Constructors
    public user() {}

    public user(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

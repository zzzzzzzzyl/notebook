package org.example.notebook;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("org.example.notebook.dao")
public class NotebookApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotebookApplication.class, args);
    }

}

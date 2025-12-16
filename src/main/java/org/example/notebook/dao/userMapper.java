package org.example.notebook.dao;

import org.apache.ibatis.annotations.*;
import org.example.notebook.pojo.user;

public interface userMapper {

    user findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    user findByUsername(String username);

    int insert(user user);

    user findById(Integer id);

    void updateUserAvatar(@Param("userId") Integer userId, @Param("avatarUrl") String avatarUrl);

    void updateUserPassword(@Param("userId") Integer userId, @Param("password") String password);
}

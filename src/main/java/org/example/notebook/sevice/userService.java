package org.example.notebook.sevice;

import org.example.notebook.pojo.user;

public interface userService {
    user authenticate(String username, String password);
    boolean register(String username, String password);
    user findById(Integer id);
    boolean isUsernameExists(String username);

    void updateUserAvatar(Integer userId, String avatarUrl);

    void updateUserPassword(Integer userId, String newPassword);
}
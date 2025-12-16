package org.example.notebook.sevice;


import org.example.notebook.dao.userMapper;
import org.example.notebook.pojo.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class userServiceImpl implements userService {

    @Autowired
    private userMapper userMapper;

    @Override
    public user authenticate(String username, String password) {
        return userMapper.findByUsernameAndPassword(username, password);
    }

    @Override
    public boolean register(String username, String password) {
        try {
            // 检查用户名是否已存在
            if (isUsernameExists(username)) {
                System.out.println("用户名已存在: " + username);
                return false;
            }

            // 创建新用户
            user newUser = new user();
            newUser.setUsername(username);
            newUser.setPassword(password);
            // 设置默认头像
            newUser.setAvatar("/images/avatar/user.png");

            // 插入数据库
            int result = userMapper.insert(newUser);
            return result > 0;
        } catch (Exception e) {
            System.err.println("注册过程发生异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public user findById(Integer id) {
        return userMapper.findById(id);
    }

    @Override
    public boolean isUsernameExists(String username) {
        return userMapper.findByUsername(username) != null;
    }

    @Override
    public void updateUserAvatar(Integer id, String avatarUrl) {
        userMapper.updateUserAvatar(id, avatarUrl);
    }
    @Override
    public void updateUserPassword(Integer id, String newPassword) {
        userMapper.updateUserPassword(id, newPassword);
    }
}
package com.car.rental.module.auth.service;

import com.car.rental.module.auth.model.UserVO;

public interface ProfileService {

    UserVO getInfo(Long userId);

    void updateProfile(Long userId, String nickname, String email, String phone);

    void updateAvatar(Long userId, String avatar);

    void changePassword(Long userId, String oldPassword, String newPassword);
}

package org.unitech.msauth.service;

import org.unitech.msauth.model.dto.request.UserUpdateRequest;
import org.unitech.msauth.model.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    UserResponse getUserByEmail(String email);

    UserResponse getUserByFin(String fin);
}
package com.epam.finaltask.service;

import java.util.UUID;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.enums.Role;

public interface UserService {

    UserDTO register(UserDTO userDTO);

    UserDTO updateUser(String username, UserDTO userDTO);

    UserDTO getUserByUsername(String username);

    void changeUserRole(Long userId, Role role);

    UserDTO getUserById(Long id);

}

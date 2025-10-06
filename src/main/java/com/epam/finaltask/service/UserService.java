package com.epam.finaltask.service;

import com.epam.finaltask.DTO.UserDTO;
import com.epam.finaltask.model.enums.Role;

import java.util.List;

public interface UserService {

    UserDTO register(UserDTO userDTO);

    UserDTO updateUser(String username, UserDTO userDTO);

    UserDTO getUserByUsername(String username);

    void changeUserRole(Long userId, Role role);

    UserDTO getUserById(Long id);

    public UserDTO getUserByEmail(String email);

    List<UserDTO> findAllUsers();

}

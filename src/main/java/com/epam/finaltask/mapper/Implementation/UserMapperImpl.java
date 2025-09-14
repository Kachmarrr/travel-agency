package com.epam.finaltask.mapper.Implementation;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    private final VoucherMapper voucherMapper;

    public UserMapperImpl(VoucherMapper voucherMapper) {
        this.voucherMapper = voucherMapper;
    }

    @Override
    public User toUser(UserDTO dto) {
        if (dto == null) return null;

        User user = User.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .phoneNumber(dto.getPhoneNumber())
                .active(Boolean.TRUE.equals(dto.isActive()))
                .build();

        return user;
    }

    @Override
    public UserDTO toUserDTO(User user) {
        if (user == null) return null;

        UserDTO userDTO = UserDTO.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // чи варто так робити ?
                .role(user.getRole() != null ? user.getRole().name() : null)
                .phoneNumber(user.getPhoneNumber())
                .active(user.isActive())
                .balance(user.getBalance() != null ? user.getBalance().doubleValue() : null)
                .build();

        return userDTO;
    }
}

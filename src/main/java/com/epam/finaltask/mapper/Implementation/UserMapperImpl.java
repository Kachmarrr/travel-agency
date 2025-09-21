package com.epam.finaltask.mapper.Implementation;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.mapper.TourMapper;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.enums.Role;
import org.springframework.stereotype.Component;
import static com.epam.finaltask.mapper.Implementation.TourMapperImpl.*;
import java.math.BigDecimal;

@Component
public class UserMapperImpl implements UserMapper {

    private TourMapper tourMapper;

    public UserMapperImpl(TourMapper tourMapper) {
        this.tourMapper = tourMapper;
    }

    @Override
    public User toUser(UserDTO dto) {
        if (dto == null) return null;

        User user = User.builder()
                .id(dto.getId())
                .username(dto.getUsername())
//               .password(dto.getPassword()) -> map without password(for security, because password can be logged)
                .email(dto.getEmail())
                .role(dto.getRole())
                .balance(BigDecimal.valueOf(dto.getBalance()))
                .tours(dto.getTours().stream().map(tourDTO -> tourMapper.toTour(tourDTO)).toList())
//                .active(Boolean.TRUE.equals(dto.isActive()))
                .build();

        return user;
    }

    @Override
    public UserDTO toUserDTO(User user) {
        if (user == null) return null;

        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
//                .password(user.getPassword()) // чи варто так робити ?
                .email(user.getEmail())
                .role(user.getRole())
                .balance(user.getBalance().doubleValue())
                .tours(user.getTours().stream().map(tour -> tourMapper.toTourDTO(tour)).toList())
//                .active(user.isActive())
                .build();

        return userDTO;
    }
}

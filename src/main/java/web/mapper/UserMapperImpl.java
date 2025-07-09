package web.mapper;

import org.springframework.stereotype.Component;
import web.dto.UserDto;
import web.model.User;

import java.util.stream.Collectors;


@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getAge(),
                user.getRoles().stream()
                        .map(role -> role.getName().toLowerCase())
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public User toEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setAge(dto.getAge());
        return user;
    }
}

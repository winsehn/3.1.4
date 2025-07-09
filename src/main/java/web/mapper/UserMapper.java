package web.mapper;

import web.dto.UserDto;
import web.model.User;

public interface UserMapper {
    UserDto toDto(User user);

    User toEntity(UserDto dto);

}

package web.facade;

import web.dto.UserDto;

import java.util.List;

public interface UserFacade {
    UserDto addUser(UserDto userDto, String password);

    UserDto updateUser(UserDto userDto, String password);

    Long deleteUser(Long id);

    List<UserDto> getAllUsers();

    UserDto findByEmail(String email);
}

package web.facade;

import org.springframework.stereotype.Component;
import web.dto.UserDto;
import web.mapper.UserMapper;
import web.model.Role;
import web.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserFacadeImpl implements UserFacade {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserFacadeImpl(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }


    @Override
    public UserDto addUser(UserDto userDto, String password) {
        return userMapper.toDto(userService.addUser(userMapper.toEntity(userDto),
                password,
                mapRoles(userDto.getRoles())).orElseThrow());
    }

    @Override
    public UserDto updateUser(UserDto userDto, String password) {
        return userMapper.toDto(userService.updateUser(userMapper.toEntity(userDto),
                password,
                mapRoles(userDto.getRoles())).orElseThrow());
    }

    @Override
    public Long deleteUser(Long id) {
        return userService.deleteUser(id).orElseThrow();
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDto findByEmail(String email) {
        return userService.findByEmail(email).map(userMapper::toDto).orElseThrow();
    }

    private Set<Role> mapRoles(Set<String> set) {
        return set.stream().map(name -> new Role(name.toLowerCase())).collect(Collectors.toSet());
    }
}

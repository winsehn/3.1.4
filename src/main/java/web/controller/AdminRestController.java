package web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import web.dto.UserDto;
import web.dto.UserDtoRequest;
import web.facade.UserFacade;

import java.util.List;

@RestController
@RequestMapping(value = "/api/admin")
public class AdminRestController {

    private final UserFacade userFacade;

    public AdminRestController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return userFacade.getAllUsers();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userFacade.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/users")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDtoRequest userDtoRequest) {
        UserDto createdUser = userFacade.addUser(userDtoRequest.getUserDto(), userDtoRequest.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping(value = "/users")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDtoRequest userDtoRequest) {
        UserDto updatedUser = userFacade.updateUser(userDtoRequest.getUserDto(), userDtoRequest.getPassword());
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }
}

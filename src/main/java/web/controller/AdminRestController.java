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
import web.service.RoleService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/admin")
public class AdminRestController {

    private final UserFacade userFacade;
    private final RoleService roleService;

    public AdminRestController(UserFacade userFacade, RoleService roleService) {
        this.userFacade = userFacade;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public ResponseEntity<List <UserDto>> getAllUsers() {
        return ResponseEntity.ok(userFacade.getAllUsers());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Long> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok(userFacade.deleteUser(id));
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
    @GetMapping(value = "/me")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
         return ResponseEntity.ok(userFacade.findByEmail(principal.getName()));
    }
    @GetMapping(value = "/roles")
    public ResponseEntity<List<String>> getAllRoles() {
        return ResponseEntity.ok(roleService.findAllRoleName());
    }
}

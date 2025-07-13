package web.service;

import web.model.Role;
import web.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {

    Optional<User> addUser(User user, String password, Set<Role> roles);

    Optional<User> updateUser(User user, String password, Set<Role> roles);

    Optional<Long> deleteUser(Long id);

    Optional<User> findUser(Long id);

    List<User> getAllUsers();

    List<User> getAllUsersWithRole();

    Optional<User> findByEmail(String email);

}

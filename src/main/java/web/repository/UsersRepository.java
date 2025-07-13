package web.repository;

import web.model.User;

import java.util.List;
import java.util.Optional;

public interface UsersRepository {
    void addUser(User user);

    Optional<Long> deleteUser(Long id);

    User updateUser(User user);

    Optional<User> findUser(Long id);

    List<User> getAllUsers();

    List<User> getAllUsersWithRole();

    Optional<User> findByEmail(String email);
}

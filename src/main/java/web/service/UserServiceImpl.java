package web.service;

import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import web.model.Role;
import web.model.User;
import web.model.auth.Password;
import web.repository.UsersRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;

    private final RoleService roleService;

    private final PasswordEncoder passwordEncoder;


    public UserServiceImpl(UsersRepository usersRepository, RoleService roleService,
                           PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User addUser(User user, String password, Set<Role> roles) {
        if (usersRepository.findByEmail(user.getEmail()).isPresent()) {
            return null;
        }
        user.setPassword(new Password(password, passwordEncoder));
        user.setRoles(roleService.reSetRoles(roles));
        usersRepository.addUser(user);
        return user;
    }

    @Override
    @Transactional
    public User updateUser(User user, String password, Set<Role> roles) {
        User existing = usersRepository.findUser(user.getId());

        Optional<User> findUserByMail = usersRepository.findByEmail(user.getEmail());
        if (findUserByMail.isPresent() && !findUserByMail.get().getId().equals(user.getId())) {
            return null;
        }

        if (password != null && !password.isBlank()) {
            if (!passwordEncoder.matches(password, existing.getPassword())) {
                existing.setPassword(new Password(password, passwordEncoder));
            } else {
                existing.setPassword(new Password(existing.getPassword()));
            }
        } else {
            existing.setPassword(new Password(existing.getPassword()));
        }
        existing.setRoles(roleService.reSetRoles(roles));
        existing.setEmail(user.getEmail());
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setAge(user.getAge());
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        usersRepository.deleteUser(id);
    }

    @Override
    @Transactional
    public List<User> getAllUsers() {
        return usersRepository.getAllUsers();
    }

    @Override
    @Transactional
    public List<User> getAllUsersWithRole() {
        return usersRepository.getAllUsersWithRole();
    }

    @Override
    @Transactional
    public Optional<User> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User findUser(Long id) {
        return usersRepository.findUser(id);
    }
}

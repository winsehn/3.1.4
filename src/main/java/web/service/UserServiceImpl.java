package web.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import web.exception.EmailAlreadyExistsException;
import web.exception.PasswordLenghtException;
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
    public Optional<User> addUser(User user, String password, Set<Role> roles) {
        checkEmailExist(user);
        checkPassLenght(password);
        user.setPassword(new Password(password, passwordEncoder));
        user.setRoles(roleService.reSetRoles(roles));
        usersRepository.addUser(user);
        return Optional.of(user);
    }

    @Override
    @Transactional
    public Optional<User> updateUser(User user, String password, Set<Role> roles) {
        checkEmailExist(user);
        checkPassLenght(password);
        User persist = usersRepository.findUser(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: " + user.getId()));
        if (StringUtils.hasText(password) && !passwordEncoder.matches(password, persist.getPassword())) {
            user.setPassword(new Password(password, passwordEncoder));
        } else {
            user.setPassword(new Password(persist.getPassword()));
        }
        user.setRoles(roleService.reSetRoles(roles));
        return Optional.of(usersRepository.updateUser(user));
    }

    @Override
    @Transactional
    public Optional<Long> deleteUser(Long id) {
        return usersRepository.deleteUser(id);
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
    public Optional<User> findUser(Long id) {
        return usersRepository.findUser(id);
    }

    private void checkEmailExist(User user) {
        usersRepository.findByEmail(user.getEmail()).ifPresent(exUser -> {
            if (!exUser.getId().equals(user.getId())) {
                throw new EmailAlreadyExistsException(user.getEmail());
            }
        });
    }

    private void checkPassLenght(String password) {
        if (StringUtils.hasText(password) && password.length() < 4) {
            throw new PasswordLenghtException();
        }
    }
}

package ru.orangemaks.kursach.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.orangemaks.kursach.Models.Role;
import ru.orangemaks.kursach.Models.User;
import ru.orangemaks.kursach.Repositories.RoleRepository;
import ru.orangemaks.kursach.Repositories.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
public class UserService implements UserDetailsService {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return user;
    }

    public User findUserById(Long userId) {
        Optional<User> userFromDb = userRepository.findById(userId);
        return userFromDb.orElse(new User());
    }

    public List<User> allUsers() {
        return userRepository.findAll();
    }

    public String saveUser(User user) {
        log.info("saveUser "+user.getUsername()+" "+user.getEmail());
        if (userRepository.findByUsername(user.getUsername()) != null) {
            log.info("user with this username already exist");
            return "Пользователь с таким именем уже существует";
        }
        if(userRepository.findByEmail(user.getEmail())!=null){
            log.info("user with this email already exist");
            return "Пользователь с таким Email уже существует";
        }

        user.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "";
    }

    public boolean deleteUser(Long userId) {
        log.info("delete user with id="+userId);
        if (userRepository.findById(userId).isPresent()) {
            userRepository.deleteById(userId);
            log.info("user successfully deleted");
            return true;
        }
        log.info("Not found user");
        return false;
    }

    public List<User> usergtList(Long idMin) {
        return em.createQuery("SELECT u FROM User u WHERE u.id = :paramId", User.class)
                .setParameter("paramId", idMin).getResultList();
    }
}

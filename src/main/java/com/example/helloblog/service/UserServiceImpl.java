package com.example.helloblog.service;

import com.example.helloblog.dto.RolesDto;
import com.example.helloblog.dto.UserDto;
import com.example.helloblog.entity.Role;
import com.example.helloblog.entity.User;
import com.example.helloblog.repository.RoleRepository;
import com.example.helloblog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAll(int pageNo, int pageSize, String sortBy, String dir) {
        Sort sort = dir.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable paging = PageRequest.of(pageNo, pageSize, sort);
        Page<User> pagedResult = userRepository.findAll(paging);
        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return null;
        }
    }

    @Override
    public User findById(int userId) {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id " + userId + " is not found.");
        }
        return user.get();
    }

    @Override
    public void deleteById(int userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User save(UserDto userDto) {
        Set<Role> roles = new HashSet<>();
        if (userDto.getUsername().equals("admin")) {
            roles.add(roleRepository.findByName("ROLE_ADMIN"));
        }
        roles.add(roleRepository.findByName("ROLE_USER"));
        User user = new User(userDto.getUsername(),
                passwordEncoder.encode(userDto.getPassword()),
                roles);
        userRepository.save(user);
        return user;
    }

    @Override
    public User update(User user, UserDto userDto) {
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
        return user;
    }

    @Override
    public User updateRoles(User user, RolesDto rolesDto) {
        user.setRoles(rolesDto.getNames().stream().map(role -> roleRepository.findByName(role)).collect(Collectors.toSet()));
        userRepository.save(user);
        return user;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}

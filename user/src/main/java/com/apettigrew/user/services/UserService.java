package com.apettigrew.user.services;

import com.apettigrew.user.dtos.UserDto;
import com.apettigrew.user.dtos.UserRegisterDto;
import com.apettigrew.user.entities.User;
import com.apettigrew.user.respositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("skipNullModelMapper")
    private ModelMapper modelMapper;

    public Page<User> getAllUsers(Pageable pageable ) {
        return userRepository.findAll(pageable);
    }

    public User getUserByUuid(UUID uuid) {
        return userRepository.findByUuid(uuid).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User createUser(UserDto userDto) {
        final User user =  modelMapper.map(userDto, User.class);
        return userRepository.save(user);
    }

    public User registerUser(UserRegisterDto userRegisterDto) {
        final User user =  modelMapper.map(userRegisterDto, User.class);
        return userRepository.save(user);
    }

    public User updateUser(UUID uuid, UserDto userDetails) {
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> new RuntimeException("User not found"));
        modelMapper.map(userDetails,user);
        return userRepository.save(user);
    }

    public void deleteUser(UUID uuid) {
        final var today = new Date();
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> new RuntimeException("User not found"));
       user.setDeletedAt(today);
        userRepository.save(user);
    }
}

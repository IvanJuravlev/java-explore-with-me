package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.ObjectNotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserService {
    private final UserRepository userRepository;

    @Transactional
    public UserDto create(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        log.info("User created with id {}", user.getId());
        return UserMapper.toUserDto(user);
    }

    public List<UserDto> findAll(List<Long> ids, Pageable pageable) {
        if (!ids.isEmpty()) {
            return userRepository.findAllByIdIn(ids, pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAll(pageable).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
            }
    }

    @Transactional
    public void deleteUser(long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            throw new ObjectNotFoundException(String.format("User with id %x not found", userId));
        });
        userRepository.deleteById(userId);
        log.info("User with id {} deleted", userId);
    }
}

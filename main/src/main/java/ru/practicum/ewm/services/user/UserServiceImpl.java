package ru.practicum.ewm.services.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.entity.dto.user.UserDto;
import ru.practicum.ewm.entity.mapper.UserMapper;
import ru.practicum.ewm.entity.model.User;
import ru.practicum.ewm.repositorys.UserRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto save(UserDto userDto) {

        User user = userMapper.fromDto(userDto);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> findAllByIdIn(List<Long> ids, Integer from, Integer size) {

        Pageable pageable = PageRequest.of(from, size);

        return userRepository.findAllByIdIn(ids, pageable).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }
}

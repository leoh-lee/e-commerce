package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.user.dto.UserCreateResult;
import kr.hhplus.be.server.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserSearchResult getUserById(long userId) {
        return UserSearchResult.fromEntity(userRepository.findById(userId).orElseThrow(UserNotFoundException::new));
    }

    @Transactional
    public UserCreateResult createUser(UserCreateDto userCreateDto) {
        User user = new User(userCreateDto.name());

        userRepository.save(user);

        return UserCreateResult.fromEntity(user);
    }

}

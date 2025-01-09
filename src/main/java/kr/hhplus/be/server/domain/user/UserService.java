package kr.hhplus.be.server.domain.user;

import kr.hhplus.be.server.domain.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserSearchResult getUserById(long userId) {
        return UserSearchResult.fromEntity(userRepository.findById(userId).orElseThrow(UserNotFoundException::new));
    }

    public void createUser(UserCreateDto userCreateDto) {
        User user = new User(userCreateDto.name());

        userRepository.save(user);
    }

}

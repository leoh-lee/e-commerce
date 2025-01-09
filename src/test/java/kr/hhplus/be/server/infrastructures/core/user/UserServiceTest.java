package kr.hhplus.be.server.infrastructures.core.user;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.domain.user.UserSearchResult;
import kr.hhplus.be.server.domain.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("사용자 ID로 사용자를 조회한다.")
    void getUserById_returnsUser() {
        // given
        long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, "테스트 유저")));

        // when
        UserSearchResult result = userService.getUserById(userId);

        // then
        assertThat(result.id()).isEqualTo(userId);
    }

}

package kr.hhplus.be.server.infrastructures.core.user;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public void save(User user) {
        userJpaRepository.save(user);
    }

    @Override
    public boolean existsById(Long userId) {
        return userJpaRepository.existsById(userId);
    }

}

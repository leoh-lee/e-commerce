package kr.hhplus.be.server.domain.user;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(Long id);

    void save(User user);

    public boolean existsById(Long userId);
}

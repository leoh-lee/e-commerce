package kr.hhplus.be.server.domain.user.dto;

import kr.hhplus.be.server.domain.user.User;

public record UserCreateResult(
        Long userId,
        String name
) {

    public static UserCreateResult fromEntity(User user) {
        return new UserCreateResult(user.getId(), user.getName());
    }
}

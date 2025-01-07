package kr.hhplus.be.server.domain.user;

public record UserSearchResult(
        Long id,
        String name
) {

    public static UserSearchResult fromEntity(User user) {
        return new UserSearchResult(user.getId(), user.getName());
    }

}

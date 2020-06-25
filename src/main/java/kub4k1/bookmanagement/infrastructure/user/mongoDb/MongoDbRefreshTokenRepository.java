package kub4k1.bookmanagement.infrastructure.user.mongoDb;

import kub4k1.bookmanagement.adapter.security.securityToken.RefreshTokenRepository;
import kub4k1.bookmanagement.domain.user.dto.UserDto;
import kub4k1.bookmanagement.infrastructure.configuration.mongoDb.MongoDbQueryAndUpdateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MongoDbRefreshTokenRepository implements RefreshTokenRepository {

    private final MongoTemplate mongoTemplate;
    private final UserConverter userConverter = new UserConverter();
    private final MongoDbQueryAndUpdateUtil mongoDbQueryAndUpdateUtil = new MongoDbQueryAndUpdateUtil();

    @Override
    public void saveRefreshToken(String refreshToken, String username) {
        mongoTemplate.updateFirst(mongoDbQueryAndUpdateUtil.usernameQuery(username),
                mongoDbQueryAndUpdateUtil.setRefreshTokenUpdate(refreshToken),
                UserDocument.class);
    }

    @Override
    public boolean isRefreshTokenExist(String refreshToken) {
        return mongoTemplate.exists(mongoDbQueryAndUpdateUtil.refreshTokenQuery(refreshToken), UserDocument.class);
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {
        mongoTemplate.updateFirst(mongoDbQueryAndUpdateUtil.refreshTokenQuery(refreshToken),
                mongoDbQueryAndUpdateUtil.deleteRefreshTokenUpdate(),
                UserDocument.class);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public UserDto getUserByRefreshToken(String refreshToken) {
        return userConverter.toDto(mongoTemplate.findOne(mongoDbQueryAndUpdateUtil.refreshTokenQuery(refreshToken),
                UserDocument.class));
    }
}

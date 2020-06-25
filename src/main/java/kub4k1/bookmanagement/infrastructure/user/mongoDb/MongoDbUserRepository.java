package kub4k1.bookmanagement.infrastructure.user.mongoDb;

import kub4k1.bookmanagement.domain.user.dto.UserDto;
import kub4k1.bookmanagement.domain.user.port.outgoing.UserRepository;
import kub4k1.bookmanagement.infrastructure.configuration.mongoDb.MongoDbQueryAndUpdateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class MongoDbUserRepository implements UserRepository {
    
    private final MongoTemplate mongoTemplate;
    private final UserConverter userConverter = new UserConverter();
    private final MongoDbQueryAndUpdateUtil mongoDbQueryAndUpdateUtil = new MongoDbQueryAndUpdateUtil();

    @Override
    public UserDto createUser(UserDto userDto) {
        UserDocument savedUser = mongoTemplate.insert(userConverter.toDocument(userDto));

        return userConverter.toDto(savedUser);
    }

    @Override
    public void activateUser(UserDto activatedUser) {
        mongoTemplate.updateFirst(mongoDbQueryAndUpdateUtil.userIdQuery(activatedUser.getId()),
                mongoDbQueryAndUpdateUtil.activateUserUpdate(), UserDocument.class);
        mongoTemplate.updateFirst(mongoDbQueryAndUpdateUtil.userIdQuery(activatedUser.getId()),
                mongoDbQueryAndUpdateUtil.deleteActivationTokenUpdate(), UserDocument.class);
    }

    @Override
    public void saveActivationToken(String token, String userId) {
        mongoTemplate.updateFirst(mongoDbQueryAndUpdateUtil.userIdQuery(userId),
                mongoDbQueryAndUpdateUtil.setActivationTokenUpdate(token), UserDocument.class);
    }

    @Override
    public boolean isTokenExists(String token) {
        return mongoTemplate.exists(mongoDbQueryAndUpdateUtil.activationTokenQuery(token), UserDocument.class);
    }

    @Override
    public void restoreActivationToken(String newToken, String userId) {
        mongoTemplate.updateFirst(mongoDbQueryAndUpdateUtil.userIdQuery(userId),
                mongoDbQueryAndUpdateUtil.setActivationTokenUpdate(newToken), UserDocument.class);
    }
}

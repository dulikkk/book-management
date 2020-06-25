package kub4k1.bookmanagement.infrastructure.configuration.mongoDb;

import kub4k1.bookmanagement.domain.book.dto.BookStatusDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.springframework.data.mongodb.core.query.Criteria.where;

public class MongoDbQueryAndUpdateUtil {

    public Query userIdQuery(String userId) {
        return new Query(where("id").is(userId));
    }

    public Query emailQuery(String email) {
        return new Query(where("email").is(email));
    }

    public Query usernameQuery(String username) {
        return new Query(where("username").is(username));
    }

    public Query activationTokenQuery(String activationToken) {
        return new Query(where("activationToken").is(activationToken));
    }

    public Query userIdAndBookStatusIsActivePageableQuery(String userId,
                                                          Pageable pageable) {
        Query query = new Query(where("userId").is(userId).and("status").is(BookStatusDto.ACTIVE));
        query.with(pageable);

        return query;
    }

    public Query refreshTokenQuery(String refreshToken) {
        return new Query(where("refreshToken").is(refreshToken));
    }

    public Update activateUserUpdate() {
        return new Update().set("active", true);
    }

    public Update deleteActivationTokenUpdate() {
        return new Update().unset("activationToken");
    }

    public Update setActivationTokenUpdate(String activationToken) {
        return new Update().set("activationToken", activationToken);
    }

    public Update setRefreshTokenUpdate(String refreshToken) {
        return new Update().set("refreshToken", refreshToken);
    }

    public Update deleteRefreshTokenUpdate() {
        return new Update().unset("refreshToken");
    }
}

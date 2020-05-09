package front.repository;

import front.data.type.UserType;
import front.data.User;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Repository
@Slf4j
public class UserRepository {


    static final String ID = "_id";
    static final String ID_ID = "_id._id";
    static final String TYPE = "_id.userType";
    static final String GAME_ID = "_id.gameId";
    static final String USER_ID = "_id.userId";
    static final String CHARACTER_NO = "_id.characterNo";

    private MongoTemplate mongoTemplate;

    public UserRepository(MongoTemplate mongoTemplate) {

        this.mongoTemplate = mongoTemplate;

    }

    public static Query query(String gameId, String searchNo, UserType type){

        return new Query(Criteria.where(ID_ID).is(type+"_"+searchNo+"_"+gameId));

    }

    public static Query query(List<String> userIds){

        return new Query(Criteria.where(ID_ID).in(userIds));

    }

    public User saveCharacter(User user) {

        return mongoTemplate.save(user);

    }

    public User saveMember(String gameId, String memberNo) {

        User user = new User(new User.UserId(UserType.M, memberNo, gameId, null, null));
        return mongoTemplate.save(user);

    }

    public Optional<User> findOne(String gameId, String searchNo, UserType type) {

        Query query = query(gameId, searchNo, type);

        val test = mongoTemplate.findOne(query, User.class);

        return Optional.ofNullable(test);

    }

    public List<User> find(List<User.UserId> userIds) {

        val ids = userIds.stream().map( userId -> userId.getUserType().getId().apply(userId)).collect(Collectors.toList());

        Query query = query(ids);

        return mongoTemplate.find(query, User.class);

    }





}

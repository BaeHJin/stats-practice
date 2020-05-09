package core.repository;

import core.data.UserStats;
import core.data.type.UpdateType;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserStatsRepository {

    private static final String ID = "_id";
    private static final String USER_ID = "_id.userId";
    private static final  String SERVICE_ID = "_id.serviceId";
    private static final  String STATS_ID = "_id.statsId";

    private MongoTemplate mongoTemplate;
    private ServiceRepository serviceRepository;


    public UserStatsRepository(MongoTemplate mongoTemplate, ServiceRepository serviceRepository) {

        this.mongoTemplate = mongoTemplate;
        this.serviceRepository = serviceRepository;

    }

    public UserStats update(ObjectId userId, String statsId, String serviceId, Long value, UpdateType updateType){

        val query = UserStatsQuery.upsert( new UserStats.UserStatsId(userId, statsId, serviceId) );
        val clauses = updateType.setClauses().apply(value);

        val collectionName = serviceRepository.getCollectionNameSuffix(serviceId);

        if(collectionName.isPresent())
            return mongoTemplate.findAndModify(query, clauses, new FindAndModifyOptions().upsert(true).returnNew(true), UserStats.class, collectionName.get());

        else
            return mongoTemplate.findAndModify(query, clauses, new FindAndModifyOptions().upsert(true).returnNew(true), UserStats.class);

    }


    public Optional<UserStats> findOne(ObjectId userId, String statsId, String serviceId){

        val query = UserStatsQuery.findOne(new UserStats.UserStatsId(userId, statsId, serviceId));

        val collectionName = serviceRepository.getCollectionNameSuffix(serviceId);

        if(collectionName.isPresent())
            return Optional.ofNullable(mongoTemplate.findOne(query, UserStats.class, collectionName.get()));
        
        else
            return Optional.ofNullable(mongoTemplate.findOne(query, UserStats.class));

    }

    public Page<UserStats> find(List<ObjectId> userId, String serviceId, List<String> statsIds, Pageable pageable){

        val query = UserStatsQuery.findWithPage(userId, serviceId, statsIds, pageable);
        val collectionName = serviceRepository.getCollectionNameSuffix(serviceId);
        List<UserStats> userStats;
        Long count;

        if(collectionName.isPresent()) {
            userStats = mongoTemplate.find(query, UserStats.class, collectionName.get());
            count = mongoTemplate.count(query, UserStats.class, collectionName.get());

        }else {
            userStats = mongoTemplate.find(query, UserStats.class);
            count = mongoTemplate.count(query, UserStats.class);
        }

        return PageableExecutionUtils.getPage(
                userStats,
                pageable,
                () -> count);


    }

    private static class UserStatsQuery{

        private static Query findOne(UserStats.UserStatsId userStats){
            return new Query(Criteria.where(ID).is(userStats));
        }

        private static Query findWithPage(List<ObjectId> userId, String serviceId, List<String> statsIds, Pageable pageable){

            return new Query(Criteria.where(SERVICE_ID).is(serviceId))
                    .addCriteria(Criteria.where(USER_ID).in(userId))
                    .addCriteria(Criteria.where(STATS_ID).in(statsIds))
                    .with(pageable);
        }

        private static Query upsert(UserStats.UserStatsId userStatsId){
            return new Query(Criteria.where(ID).is(userStatsId));
        }

    }

}

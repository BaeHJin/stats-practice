package core.repository;

import core.data.Stats;
import core.data.type.StatusType;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class StatsRepository {

    private final MongoTemplate mongoTemplate;

    public StatsRepository(MongoTemplate mongoTemplate) {

        this.mongoTemplate = mongoTemplate;

    }

    static final String ID = "_id";
    static final String SERVICE_ID = "_id.serviceId";
    static final String STATUS = "status";
    static final String DESCRIPTION = "description";


    public static Query findOneQuery(Stats.Id statsId){
        return new Query(Criteria.where(ID).is(statsId));
    }

    public static Query findQuery(String serviceId){

        Query query = new Query();
        query.addCriteria(Criteria.where(SERVICE_ID).is(serviceId));

        return query;

    }

    public static Query findQuery(String serviceId, @Nullable String statsId, @Nullable StatusType status, @Nullable String title, Pageable pageable){

        Query query = new Query();
        if(StringUtils.isEmpty(statsId))
            query.addCriteria(Criteria.where(SERVICE_ID).is(serviceId));
        else
            query.addCriteria(Criteria.where(ID).is(new Stats.Id(statsId, serviceId)));

        if( !StringUtils.isEmpty(status) )
            query.addCriteria(Criteria.where(STATUS).is(status));

        if( !StringUtils.isEmpty(title) )
            query.addCriteria(Criteria.where(DESCRIPTION).regex(".*"+title+".*"));

        query.with(pageable);

        return query;

    }


    public static Query deleteQuery(Set<Stats.Id> statsIds) {

        Query query = new Query();
        query.addCriteria(Criteria.where(ID).in(statsIds));
        query.addCriteria(Criteria.where(STATUS).is(StatusType.INACTIVE));

        return query;
    }


    public Optional<Stats> findOne(String statsId, String serviceId) {

        val query = findOneQuery(new Stats.Id(statsId, serviceId));

        return Optional.ofNullable(mongoTemplate.findOne(query, Stats.class));

    }

    public Stats insert(Stats stats) {

        return mongoTemplate.insert(stats);

    }

    public Stats update(Stats stats) {

        return mongoTemplate.save(stats);
    }

    public List<Stats> find(String serviceId) {

        val query = findQuery(serviceId);

         return mongoTemplate.find(query, Stats.class);
    }

    public Page<Stats> find(String serviceId, @Nullable String statsId, @Nullable StatusType status, @Nullable String title, Pageable pageable) {

        val query = findQuery(serviceId, statsId, status, title, pageable);

        val stats = mongoTemplate.find(query, Stats.class);

        return PageableExecutionUtils.getPage(
                stats,
                pageable,
                () -> mongoTemplate.count(query, Stats.class));
    }

    public Set<String> delete(Set<Stats.Id> statsIds) {

        Query query = deleteQuery(statsIds);

        val deleteStats =  mongoTemplate.findAllAndRemove(query, Stats.class);

        return deleteStats.stream().map( stats -> stats.getId().getStatsId()).collect(Collectors.toSet());
    }

}





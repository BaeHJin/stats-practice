package core.repository;

import core.data.Service;
import lombok.val;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Repository
public class ServiceRepository {

    private InternalRepository internalRepository;

    public ServiceRepository(InternalRepository internalRepository) {

        this.internalRepository = internalRepository;

    }

    @Cacheable("collectionNameSuffix")
    public Optional<String> getCollectionNameSuffix(String serviceId) {

        val service = internalRepository.findById(serviceId);

        if (service.isPresent())
            return Optional.of(service.get().getCollectionNameSuffix());

        else
            return Optional.empty();

    }

    public interface InternalRepository extends MongoRepository<Service, String> {
    }

}

package core.repository;

import core.config.MongoConfig;
import core.data.Service;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MongoConfig.class, ServiceRepository.class})
@Slf4j
public class ServiceRepositoryTest {

    @Autowired
    private ServiceRepository.InternalRepository internalRepository;

    @Autowired
    private ServiceRepository repository;

    @Before
    public void before() {

        internalRepository.deleteAll();

        val givenService = Service.of("A", "TEST");

        internalRepository.save(givenService);

    }

    @Test
    public void testGetUserStatsCollectionName() {

        // when
        val actual = repository.getCollectionNameSuffix("A");

        // then
        assertThat(actual).hasValue("TEST");

    }

    @Test
    public void testGetUserStatsCollectionName_default() {

        // when
        val actual = repository.getCollectionNameSuffix("B");

        // then
        assertThat(actual).isNotPresent();

    }

}

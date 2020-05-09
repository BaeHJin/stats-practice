package core.repository;

import core.config.CacheConfig;
import core.data.Service;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.awaitility.Duration;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CacheConfig.class, ServiceRepository.class})
@Slf4j
public class ServiceRepositoryCacheTest {

    @MockBean
    private ServiceRepository.InternalRepository internalRepository;

    @Autowired
    private ServiceRepository repository;

    @Test
    public void testGetUserStatsCollectionNameSuffix() {

        // given
        val givenService1 = Service.of("A", "TEST");
        val givenService2 = Service.of("A", "TEST2");
        when(internalRepository.findById("A"))
                .thenReturn(Optional.of(givenService1))
                .thenReturn(Optional.of(givenService2))
                .thenReturn(Optional.empty());

        // when, then
        assertThat(repository.getCollectionNameSuffix("A")).hasValue("TEST");
        assertThat(repository.getCollectionNameSuffix("A")).hasValue("TEST");

        await().atMost(Duration.TWO_SECONDS).until(() -> repository.getCollectionNameSuffix("A"), Matchers.is(Optional.of("TEST2")));
        await().atMost(Duration.TWO_SECONDS).until(() -> !repository.getCollectionNameSuffix("A").isPresent());

        verify(internalRepository, times(3)).findById("A");

    }

}

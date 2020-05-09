package front.repository;

import core.config.MongoConfig;

import front.data.type.UserType;
import front.data.User;
import lombok.val;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MongoConfig.class, UserRepository.class})
//@EnableAutoConfiguration
// 실제 원격 테스트 하려면,
// -- 아래 주석을 해제하고
// -- com.github.tomakehurst.wiremock.client.WireMock.verify(RequestPatternBuilder) 부분을 주석처리 한다.
//@TestPropertySource(
//        properties = {
//                "client.mobile.host=localhost"
//        }
//)
@AutoConfigureWireMock(port = 0)
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private MongoTemplate mongoTemplate;

    User user;
    User user2;

    @Before
    public void before() {

        mongoTemplate.dropCollection("user");

        user = new User(new User.UserId(UserType.S, "100", "testGame", null, "1234"));

        mongoTemplate.save(user);

        user2 = new User(new User.UserId(UserType.S, "100", "testGame", null, "5678"));

        mongoTemplate.save(user2);

    }

    @Test
    public void testFindOneQueryCharacter() {
        //given
        val type = UserType.S;
        val expected = new Query(Criteria.where("_id._id").is(type+"_"+"1234"+"_"+"testGame"));
        //when
        val actual = repository.query("testGame", "1234", type);

        //then
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testFindOneQueryMember() {
        //given
        val type = UserType.M;
        val expected = new Query(Criteria.where("_id._id").is(type+"_"+"1234"+"_"+"testGame"));

        //when
        val actual = repository.query("testGame", "1234", type);

        //then
        Assert.assertEquals(actual, expected);
    }


    @Test
    public void testFindOne() {

        //given
        val type = UserType.S;

        //when
        val actual = repository.findOne("testGame", "1234", type);

        //then
        Assert.assertEquals(actual.get().getId().getCharacterNo(), "1234");

    }


    @Test
    public void testFindOneNotFound() {

        //given
        val type = UserType.S;

        //when
        val actual = repository.findOne("testGame", "notExistcharacterNo", type);

        //then
        Assert.assertTrue(!actual.isPresent());

    }

    @Test
    public void testFindByUserIds() {

        //given
//        User user = new User(new User.UserId(UserType.S, "100", "testGame", null, "1234"));
//        User user2 = new User(new User.UserId(UserType.S, "100", "testGame", null, "5678"));
        val userIds = Lists.newArrayList(user.getId(), user2.getId());
        val expected = Lists.newArrayList(user, user2);

        //when
        val actual = repository.find(userIds);

        //then
        assertThat(actual).isEqualTo(expected);

    }

    @Test
    public void testSave() {

        //given
        User user = new User(new User.UserId(UserType.S, "100", "testGame", null, "1234"));

        //when
        val actual = repository.saveCharacter(user);

        //then
        assertThat(actual.getId().getCharacterNo()).isEqualTo("1234");

    }

    @Test
    public void testSaveMember() {

        //given
        User user = new User(new User.UserId(UserType.M, "100", "testGame", null, null));

        //when
        val actual = repository.saveMember("testGame","100");

        //then
        assertThat(actual).isEqualToIgnoringGivenFields(user, "userId");

    }


}

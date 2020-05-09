package front.service;

import front.data.type.UserType;
import front.data.User;
import front.repository.MemberRepository;
import front.repository.UserRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class UserServiceTest {

    UserService service;
    UserRepository userRepository;
    MemberRepository memberRepository;

    @Before
    public void setUp(){

        userRepository = mock(UserRepository.class);
        memberRepository = mock(MemberRepository.class);
        service = new UserService(userRepository, memberRepository);

    }


    @Test
    public void test(){
        Number testval = 1.1111;
        log.info("double value : " + testval.doubleValue());
        log.info("double value * 1000 : " + (testval.doubleValue()*1000) );
        Long t = Math.round(testval.doubleValue()*1000);
        log.info("double value * 1000 : " +  t);
    }

    @Test
    public void testGetOneCharacter() {

        //given
        val givenUserId = new User.UserId(UserType.S, "100", "testGame", null, "999");
        val givenUser = new User(givenUserId);
        when(userRepository.findOne("testGame","999", UserType.S)).thenReturn(Optional.ofNullable(givenUser));

        //when
        val actual = service.getOne("testGame", "999", UserType.S);


        //then
        Assertions.assertThat(actual.get()).isEqualTo(givenUser);

    }

    @Test
    public void testGetOneMember() {

        //given
        val givenUserId = new User.UserId(UserType.M, "100", "testGame", null, null);
        val givenUser = new User(givenUserId);
        when(userRepository.findOne("testGame","100", UserType.M)).thenReturn(Optional.ofNullable(givenUser));

        //when
        val actual = service.getOne("testGame", "100", UserType.M);


        //then
        Assertions.assertThat(actual.get()).isEqualTo(givenUser);

    }

    @Test
    public void testGetUserIds() {

        //given
        val givenUserId = new User.UserId(UserType.S, "100", "testGame", null, "999");
        val givenUserId2 = new User.UserId(UserType.S, "101", "testGame", null, "900");
        val givenUser = new User(givenUserId);
        val givenUser2 = new User(givenUserId2);
        val givenUserIds = Lists.newArrayList(givenUser.getId(), givenUser2.getId());
        val expected = Lists.newArrayList(givenUser.getUserId(), givenUser2.getUserId());
        val givenUsers = Lists.newArrayList(givenUser, givenUser2);

        //when
        when(userRepository.find(givenUserIds)).thenReturn(givenUsers);
        val actual = service.getUserIds(givenUserIds);

        //then
        Assertions.assertThat(actual).isEqualTo(expected);


    }



    @Test
    public void testSetCharacter() {

        //given
        val givenUserId = new User.UserId(UserType.S, "100", "testGame", null, "999");
        val givenUser = new User(givenUserId);

        when(memberRepository.getCharacter("testGame","999")).thenReturn(givenUser);
        when(userRepository.saveCharacter(givenUser)).thenReturn(givenUser);

        //when
        val actual = service.setCharacter("testGame", "999");

        //then
        Assertions.assertThat(actual).isEqualTo(givenUser);

    }


    @Test
    public void testSetMember() {

        //given
        val givenUserId = new User.UserId(UserType.M, "100", "testGame", null, null);
        val givenUser = new User(givenUserId);

        when(userRepository.saveMember("testGame","100")).thenReturn(givenUser);

        //when
        val actual = service.setMember("testGame", "100");

        //then
        Assertions.assertThat(actual).isEqualTo(givenUser);

    }

    @Test
    public void testSetUserMemberType() {

        //given
        val givenUserId = new User.UserId(UserType.M, "100", "testGame", null, null);
        val givenUser = new User(givenUserId);

        when(userRepository.saveMember("testGame","100")).thenReturn(givenUser);

        //when
        val actual = service.setUser("testGame", "100", UserType.M);

        //then
        Assertions.assertThat(actual).isEqualTo(givenUser);

    }

    @Test
    public void testSetUserCharacterType() {

        //given
        val givenUserId = new User.UserId(UserType.S, "100", "testGame", null, "123");
        val givenUser = new User(givenUserId);

        when(memberRepository.getCharacter("testGame", "123")).thenReturn(givenUser);
        when(userRepository.saveCharacter(givenUser)).thenReturn(givenUser);

        //when
        val actual = service.setUser("testGame", "123", UserType.S);

        //then
        Assertions.assertThat(actual).isEqualTo(givenUser);

    }


}

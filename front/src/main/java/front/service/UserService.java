package front.service;

import front.data.type.UserType;
import front.data.User;
import front.repository.MemberRepository;
import front.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private UserRepository userRepository;
    private MemberRepository memberRepository;

    public UserService(UserRepository userRepository, MemberRepository memberRepository){
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
    }


    public Optional<User> getOne(String gameId, String searchNo, UserType type){

        return userRepository.findOne(gameId, searchNo, type);
    }


    public List<ObjectId> getUserIdsFromUsers(List<User> users){

        return users.stream().map(User::getUserId).collect(Collectors.toList());

    }


    public List<ObjectId> getUserIds(List<User.UserId> requestUserIds){

        val users = get(requestUserIds);

        return getUserIdsFromUsers(users);

    }

    public List<User> get(List<User.UserId> userIds){

        return userRepository.find(userIds);
    }



    public User setUser(String gameId, String userNo, UserType userType){

        if(userType.equals(UserType.S))
            return setCharacter(gameId, userNo);

        else
            return setMember(gameId, userNo);

    }

    public User setCharacter(String gameId, String characterNo) {

        val character = memberRepository.getCharacter(gameId, characterNo);

        return userRepository.saveCharacter(character);
    }


    public User setMember(String gameId, String memberNo) {

        return userRepository.saveMember(gameId, memberNo);
    }
}

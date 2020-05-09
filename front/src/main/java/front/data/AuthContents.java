package front.data;

import lombok.Value;

import javax.validation.constraints.NotEmpty;

@Value
public class AuthContents {

    String aud; // 토큰을 사용하는 대상자 정보 ( 클라이언트 정보, 마켓 게임 아이디 또는 application_no)

    String sub; // 토큰 발급 주체 ( {member_no}:{service_id}:{version} 또는 server:{서버식별키} )

    String uid; // 사용자 식별자, 게임에 제공되는 member_no

    @NotEmpty
    String sid; // 서비스 아이디, 게임 아이디

    Long exp; // 만료 시간

    Pld pld; // 사용자 객체

    String typ; //  토큰을 발급하는 주체를 구분하기 위한 정보. (member 또는 server)

    @Value
    public static class Pld {

        String providerCd; // 로그인 프로바이더의 타입

        String deviceId; // 로그인 디바이스 식별자

        String loginCountryCd; // 접속 국가 코드

        String loginTimezone; //접속 타임존

        @NotEmpty
        String memberNo; // 스토브 플랫폼 회원 고유 식별번호

        String countryCd; // 회원 가입 시 국가 코드

        String languageCd; // 회원 가입 시 언어 코드

        String timezone; // 회원 가입시 타임 존

        Integer utcOffset; // 회원 가입시 표준시 (분단위)

        String regDt; // 회원 가입 일시

        String nickname; // 스토브 플랫폼 닉네임

        String profileImgUrl; // 스토브 플랫폼 이미지

        String personVerifyYn; // 본인인증 여부

        String emailVerifyYn; //이메일 인증 여부
    }

}


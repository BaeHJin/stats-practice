package front.data;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import front.exception.UnauthorizedException;
import lombok.val;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class HeaderTest {

    private String ownerContent = "ewogICAgInNpZCI6ICJTVE9WRV9URVNURVIiLAogICAgImRpZCI6ICIxNTc5NTA4NzYxMTY4IiwKICAgICJ0eXAiOiAibWVtYmVyIiwKICAgICJzdWIiOiAiezE1Nzk1MTcyMzYwMzB9U1RPVkVfVEVTVEVSMTU3OTU2OTUxMjk5NCIsCiAgICAicGxkIjogewogICAgICAgICJuaWNrbmFtZSI6ICJzdGF0c1Rlc3QiLAogICAgICAgICJyZWdfZHQiOiAxNTc5NTE3MjM2MDMwLAogICAgICAgICJjb3VudHJ5X2NkIjogIktSIiwKICAgICAgICAidXRjX29mZnNldCI6IDU0MCwKICAgICAgICAicHJvZmlsZV9pbWdfdXJsIjogIjE1Nzk1MTcyMzYwMzBfcHJvZmlsZV9pbWciLAogICAgICAgICJ0aW1lem9uZSI6ICJBc2lhXC9TZW91bCIsCiAgICAgICAgIm1lbWJlcl9ubyI6IDEyMzQsCiAgICAgICAgImxhbmd1YWdlX2NkIjogIktPIgogICAgfSwKICAgICJhdWQiOiAidGVzdGdhbWVfaW9zMiIsCiAgICAiZXhwIjogMTU3OTU5MTExMjk5NCwKICAgICJwY2QiOiAiVEVTVCIKfQ==";

    @Test
    public void testSetAuditContents(){

        //given
        val header = new Header();

        //when
        header.setAuditContents(getAuthContent());

        //then
        assertThat(header.getGameId()).isEqualTo("STOVE_TESTER");
        assertThat(header.getMemberNo()).isEqualTo("1234");



    }

    private AuthContents getAuthContent(){

        try{
        String decodedOwnerContent = StandardCharsets.UTF_8
                .decode(ByteBuffer.wrap(org.apache.commons.codec.binary.Base64.decodeBase64(ownerContent)))
                .toString();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(decodedOwnerContent, AuthContents.class);

        } catch(IOException e) {
            throw new UnauthorizedException("Invalid Access Token");

        }

    }
}

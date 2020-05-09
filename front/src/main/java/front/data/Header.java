package front.data;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@Data
@RequestScope
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class Header {

    String memberNo;
    String gameId;

    public void setAuditContents(final AuthContents authContents){
        this.memberNo = String.valueOf(authContents.getPld().getMemberNo());
        this.gameId = authContents.getSid();

    }

}

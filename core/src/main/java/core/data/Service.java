package core.data;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data(staticConstructor = "of")
public class Service {

    @Id
    @NonNull
    private String serviceId;

    @NonNull
    private String collectionNameSuffix;


}

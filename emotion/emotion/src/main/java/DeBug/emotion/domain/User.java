package DeBug.emotion.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import java.time.LocalDateTime;

//유저 정보
@Document(collection = "Users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @Email
    //사용자 Email
    private String _id;

    private String Name;

    private String Picture;

    private String Channels_Id;
    private String class_name = "basic";
    private LocalDateTime date = null;
}

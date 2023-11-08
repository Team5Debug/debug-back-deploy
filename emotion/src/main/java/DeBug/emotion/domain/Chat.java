package DeBug.emotion.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;


//채팅 정보
@Getter
@Setter
@NoArgsConstructor
public class Chat {

    private String dateTime;
    private String message;
    private int emotion3;
    private int emotion7;

}

package DeBug.emotion.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

//방송정보
@Document(collection = "BroadCast")
@Getter
@Setter
@NoArgsConstructor
public class BroadCast {

    @Id
    //BCID
    private String _id;
    private String URI;
    private String Title;
    private String ThumbnailsUrl;
    private String ActualStartTime;
    private String ActualEndTime;
    private Integer HighViewer;
    private Integer LowViewer;
    public int[] All_Emotion3 = new int[3];
    public  int[] All_Emotion7 = new int[3];
    private int[] One_Hour_Emotion = new int[24];
    @DBRef
    private User user;
}

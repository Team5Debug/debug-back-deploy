package DeBug.emotion.domain;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Topic {
    private Topic_data positive_topicData;
    private Topic_data negative_topicData;
    private Topic_data emotion7_topicData;
    private int emotion7;
}

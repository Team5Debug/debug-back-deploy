package DeBug.emotion.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class Day_Total_Data {

    public int[] All_Emotion3 = new int[3];
    public int[] All_Emotion7 = new int[7];
}

package DeBug.emotion.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DayTotalData {

    public int[] All_Emotion3 = new int[3];
    public int[] All_Emotion7 = new int[7];
    public HourData[] One_Hour_Emotion = new HourData[24];

}

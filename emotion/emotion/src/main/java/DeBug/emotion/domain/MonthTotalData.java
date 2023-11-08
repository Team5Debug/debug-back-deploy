package DeBug.emotion.domain;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthTotalData {
    public int[] All_Emotion3 = new int[3];
    public int[] All_Emotion7 = new int[7];
    public DayTotalData[] day_total_data = new DayTotalData[31];
}

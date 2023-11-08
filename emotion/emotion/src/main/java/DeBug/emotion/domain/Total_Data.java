package DeBug.emotion.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Total_Data {

    private List<BroadCast> broadCasts = new ArrayList<BroadCast>();
    private List<YearTotalData> years = new ArrayList<YearTotalData>();
}

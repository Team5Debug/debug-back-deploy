package DeBug.emotion.domain;

import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FeedbackData {
    private JSONObject Viewer;
    private String published;
    public List<Chat> cd = new ArrayList<Chat>();
}

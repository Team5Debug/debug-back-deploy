package DeBug.emotion.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "Purchase_History")
public class Purchase_History {
    @Id
    //apply_num
    private String _id;
    private String name;
    private String amount;
    private String merchant_uid;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    @DBRef
    private User user;

    public Purchase_History(String id, String name, String amount,String merchant_uid) {
        this._id = id;
        this.name = name;
        this.amount = amount;
        this.merchant_uid = merchant_uid;
        this.start_date = LocalDateTime.now();
        this.end_date = LocalDateTime.now().plusMonths(1);
    }


}

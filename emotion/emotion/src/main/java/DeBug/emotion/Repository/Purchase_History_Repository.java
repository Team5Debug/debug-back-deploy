package DeBug.emotion.Repository;

import DeBug.emotion.domain.BroadCast;
import DeBug.emotion.domain.Purchase_History;
import DeBug.emotion.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface Purchase_History_Repository extends MongoRepository<Purchase_History, String> {
    List<Purchase_History> findByUser(User user);
}

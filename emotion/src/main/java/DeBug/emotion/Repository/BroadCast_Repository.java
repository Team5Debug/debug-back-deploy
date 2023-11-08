package DeBug.emotion.Repository;

import DeBug.emotion.domain.BroadCast;
import DeBug.emotion.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BroadCast_Repository extends MongoRepository<BroadCast,String> {
    List<BroadCast> findByUser(User user);
}

package DeBug.emotion.Repository;

import DeBug.emotion.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

//몽고디비 연결
public interface User_Repository extends MongoRepository<User, String> {
    User findOneBy_id(String id);
}

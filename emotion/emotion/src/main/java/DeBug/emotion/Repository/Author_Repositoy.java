package DeBug.emotion.Repository;

import DeBug.emotion.domain.Author;
import DeBug.emotion.domain.BroadCast;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface Author_Repositoy extends MongoRepository<Author,String> {
    List<Author> findByBroadCast(BroadCast broadcast);
}

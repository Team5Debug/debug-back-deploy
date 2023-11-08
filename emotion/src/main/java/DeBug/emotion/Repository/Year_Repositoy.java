package DeBug.emotion.Repository;

import DeBug.emotion.domain.User;
import DeBug.emotion.domain.YearTotalData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface Year_Repositoy extends MongoRepository<YearTotalData, String> {
    List<YearTotalData> findByUser(User user);
}

package misc.repositories;

import misc.model.TrendDesign;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.*;

@Repository
public interface TrendDesignRepository extends CrudRepository<TrendDesign, Long>{
    
}

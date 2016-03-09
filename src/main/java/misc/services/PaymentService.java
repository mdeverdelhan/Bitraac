package misc.services;

import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;
import org.springframework.stereotype.*;

@Service
public class PaymentService {

    public void charge() throws AuthenticationException, InvalidRequestException, APIConnectionException, APIException, CardException {
        Charge.create(null);
        // @TODO
    }
    
}

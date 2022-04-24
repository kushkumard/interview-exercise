package com.acme.mytrader.price;

import com.acme.mytrader.execution.ExecutionService;
import com.acme.mytrader.model.LimitOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@AllArgsConstructor
@Data
@Builder
public class PriceListenerImpl implements PriceListener {

    private final LimitOrder limitOrder;
    private final ExecutionService executionService;
    private final PriceSource priceSource;
    private boolean limitOrderExecuted;


    @Override
    //Basic implementation of the PriceListener, priceUpdate is triggered on this Listener for the security used while registering the listener
    public void priceUpdate(String security, double price) {
        if(!limitOrderExecuted && price < limitOrder.getThreshold()) {
            executionService.buy(limitOrder.getSecurity(), price, limitOrder.getLot());
            limitOrderExecuted = true;
            priceSource.removePriceListener(this);
        }
    }

}

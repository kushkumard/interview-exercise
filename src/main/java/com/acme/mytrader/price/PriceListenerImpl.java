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

    @Override
    public void priceUpdate(String security, double price) {
        if(price < limitOrder.getThreshold()) {
            executionService.buy(limitOrder.getSecurity(), price, limitOrder.getLot());
        }
    }

}

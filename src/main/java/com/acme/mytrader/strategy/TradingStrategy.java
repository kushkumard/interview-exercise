package com.acme.mytrader.strategy;

import com.acme.mytrader.execution.ExecutionService;
import com.acme.mytrader.model.LimitOrder;
import com.acme.mytrader.price.PriceListenerImpl;
import com.acme.mytrader.price.PriceSource;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * <pre>
 * User Story: As a trader I want to be able to monitor stock prices such
 * that when they breach a trigger level orders can be executed automatically
 * </pre>
 */
@RequiredArgsConstructor
public class TradingStrategy {

    private final ExecutionService executionService;
    private final PriceSource priceSource;

    public void executeOrders(List<LimitOrder> limitOrderList) {
        limitOrderList.stream().forEach(limitOrder -> priceSource.addPriceListener(PriceListenerImpl.builder()
                .limitOrder(limitOrder).executionService(executionService).priceSource(priceSource).build()));
    }
}
package com.acme.mytrader.strategy;

import com.acme.mytrader.execution.ExecutionService;
import com.acme.mytrader.model.LimitOrder;
import com.acme.mytrader.price.PriceListenerImpl;
import com.acme.mytrader.price.PriceSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TradingStrategyTest {

    @Mock
    private ExecutionService executionService;

    @Mock
    private PriceSource priceSource;

    @InjectMocks
    private TradingStrategy tradingStrategy;


    @Test
    public void shouldExecuteOrdersSuccessfully() {
        LimitOrder limitOrderDBK = new LimitOrder("DBK", 65.00, 100);
        LimitOrder limitOrderIBM = new LimitOrder("IBM", 55.00, 100);
        LimitOrder limitOrderGOOGL = new LimitOrder("GOOGL", 75.00, 100);
        PriceListenerImpl priceListenerImplDBK = PriceListenerImpl.builder().limitOrder(limitOrderDBK)
                .executionService(executionService).priceSource(priceSource).build();
        PriceListenerImpl priceListenerImplIBM = PriceListenerImpl.builder().limitOrder(limitOrderIBM)
                .executionService(executionService).priceSource(priceSource).build();
        PriceListenerImpl priceListenerImplGOOGL = PriceListenerImpl.builder().limitOrder(limitOrderGOOGL)
                .executionService(executionService).priceSource(priceSource).build();

        tradingStrategy.executeOrders(List.of(limitOrderDBK, limitOrderIBM, limitOrderGOOGL ));

        priceListenerImplDBK.priceUpdate("DBK", 60);
        priceListenerImplIBM.priceUpdate("IBM", 50);
        priceListenerImplGOOGL.priceUpdate("GOOGL", 70);

        verify(priceSource, times(3)).addPriceListener(any(PriceListenerImpl.class));
        verify(priceSource, times(3)).removePriceListener(any(PriceListenerImpl.class));
        verify(executionService, times(3))
                .buy(any(String.class),any(Double.class), any(Integer.class));
    }

    @Test
    public void shouldNotExecuteOrdersForUpdatesNotBreachingThreshold() {
        LimitOrder limitOrderDBK = new LimitOrder("DBK", 65.00, 100);
        LimitOrder limitOrderIBM = new LimitOrder("IBM", 55.00, 100);
        LimitOrder limitOrderGOOGL = new LimitOrder("GOOGL", 75.00, 100);

        PriceListenerImpl priceListenerImplDBK = PriceListenerImpl.builder().limitOrder(limitOrderDBK)
                .executionService(executionService).priceSource(priceSource).build();
        PriceListenerImpl priceListenerImplIBM = PriceListenerImpl.builder().limitOrder(limitOrderIBM)
                .executionService(executionService).priceSource(priceSource).build();
        PriceListenerImpl priceListenerImplGOOGL = PriceListenerImpl.builder().limitOrder(limitOrderGOOGL)
                .executionService(executionService).priceSource(priceSource).build();

        tradingStrategy.executeOrders(List.of(limitOrderDBK, limitOrderIBM, limitOrderGOOGL ));

        priceListenerImplDBK.priceUpdate("DBK", 60);
        priceListenerImplIBM.priceUpdate("IBM", 60);
        priceListenerImplGOOGL.priceUpdate("GOOGL", 80);

        verify(priceSource, times(3)).addPriceListener(any(PriceListenerImpl.class));
        verify(priceSource, times(1)).removePriceListener(any(PriceListenerImpl.class));
        verify(executionService, times(1)).buy(any(String.class),any(Double.class), any(Integer.class));
    }

}

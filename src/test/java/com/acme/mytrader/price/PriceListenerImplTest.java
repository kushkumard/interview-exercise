package com.acme.mytrader.price;


import com.acme.mytrader.execution.ExecutionService;
import com.acme.mytrader.model.LimitOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PriceListenerImplTest {

    @Mock
    private ExecutionService executionService;

    @Mock
    private PriceSource priceSource;

    ArgumentCaptor<String> acString = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Double> acDouble = ArgumentCaptor.forClass(Double.class);
    ArgumentCaptor<Integer> acInteger = ArgumentCaptor.forClass(Integer.class);

    @Test
    public void shouldBuySecurityWhenThresholdIsBreached() {
        PriceListenerImpl priceListenerImpl = new PriceListenerImpl(new LimitOrder("IBM", 55.00, 100), executionService, priceSource,false);
        priceListenerImpl.priceUpdate("IBM", 50.00);

        verify(executionService, times(1)).buy(acString.capture(), acDouble.capture(), acInteger.capture());
        assertThat(acInteger.getValue()).isEqualTo(100);
        assertEquals(acString.getValue(), "IBM");
        assertThat(acDouble.getValue()).isEqualTo(50.00);
    }

    @Test
    public void shouldNotBuySecurityWhenThresholdIsSameAsPrice() {
        PriceListenerImpl priceListenerImpl = new PriceListenerImpl(new LimitOrder("IBM", 55.00, 100), executionService, priceSource, false);
        priceListenerImpl.priceUpdate("IBM", 55.00);
        verify(executionService, times(0)).buy(acString.capture(), acDouble.capture(), acInteger.capture());
    }

    @Test
    public void shouldNotBuySecurityWhenThresholdIsNotBreached() {
        PriceListenerImpl priceListenerImpl = new PriceListenerImpl(new LimitOrder("IBM", 55.00, 100), executionService, priceSource, false);
        priceListenerImpl.priceUpdate("IBM", 57.00);

        verify(executionService, times(0)).buy(acString.capture(), acDouble.capture(), acInteger.capture());
    }

    @Test
    public void shouldBuySecurityOnlyOnceForMultiplePriceUpdates() {
        PriceListenerImpl priceListenerImpl = new PriceListenerImpl(new LimitOrder("IBM", 55.00, 100), executionService, priceSource,false);
        priceListenerImpl.priceUpdate("IBM", 50.00);
        priceListenerImpl.priceUpdate("IBM", 47.00);
        priceListenerImpl.priceUpdate("IBM", 42.00);
        priceListenerImpl.priceUpdate("IBM", 40.00);

        verify(executionService, times(1))
                .buy(acString.capture(), acDouble.capture(), acInteger.capture());
        assertThat(priceListenerImpl.isLimitOrderExecuted()).isTrue();
        assertThat(acInteger.getValue()).isEqualTo(100);
        assertEquals(acString.getValue(), "IBM");
        assertThat(acDouble.getValue()).isEqualTo(50.00);
    }
}

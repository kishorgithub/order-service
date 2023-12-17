package com.bitmyra.configs;

import com.bitmyra.lob.OrderBook;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfigs {


    @Bean
    @Qualifier("tickedOrderBook")
    public OrderBook getTickSizeOrderBook() {
        return new OrderBook(1d);
    }
}

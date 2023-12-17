package com.bitmyra;

import com.bitmyra.dto.OrderRequestTO;
import com.bitmyra.dto.OrderResponseTO;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    public OrderResponseTO placeOrder(OrderRequestTO orderRequestTO) {
        System.out.println("Customer id is");
        return null;
    }
}

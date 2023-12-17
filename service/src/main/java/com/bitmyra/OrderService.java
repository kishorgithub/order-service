package com.bitmyra;

import com.bitmyra.dto.OrderRequestTO;
import com.bitmyra.dto.OrderResponseTO;
import com.bitmyra.lob.Order;
import com.bitmyra.lob.OrderBook;
import com.bitmyra.lob.OrderReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderService {

    @Autowired
    private OrderBook tickedOrderBook;

    static int txnId;

    public OrderResponseTO placeOrder(OrderRequestTO orderRequestTO) {
        OrderReport orderReport;

        try {
            orderReport = tickedOrderBook.processOrder(composeOrder(orderRequestTO), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        System.out.println(tickedOrderBook);
        return OrderResponseTO.builder().status(orderReport.toString()).build();
    }


    private Order composeOrder(OrderRequestTO orderRequestTO) throws IOException {

        System.out.println("order side : " + orderRequestTO.getSide());

        int price;
        int qty;

        Order newOrder;
        price = ThreadLocalRandom.current().nextInt(100, 200);
        qty = ThreadLocalRandom.current().nextInt(1, 100);


        newOrder = new Order(System.currentTimeMillis(), orderRequestTO.getLimit(), orderRequestTO.getQuantity(),
                txnId++, orderRequestTO.getSide(), orderRequestTO.getPrice());

        System.out.println("Incoming Order ---------------------- " + newOrder);

        return newOrder;
    }
}

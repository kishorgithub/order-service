package com.bitmyra;

import com.bitmyra.dto.OrderRequestTO;
import com.bitmyra.dto.OrderResponseTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping()
    public ResponseEntity<OrderResponseTO> processOrder(
            @RequestBody
            @Valid
            OrderRequestTO orderRequestTO) {

        return new ResponseEntity<>(orderService.placeOrder(orderRequestTO), HttpStatus.OK);
    }

}

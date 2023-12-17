package com.bitmyra.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderRequestTO {
    @NotEmpty
    private String customerId;
    private Double price;
    private String assetId;
    private Double quantity;
    private String orderType; //TODO: Write enum - OFFER, BID
    private Boolean stopLoss;
    private Double stopLossPrice;
    private Boolean marketOrder;
}

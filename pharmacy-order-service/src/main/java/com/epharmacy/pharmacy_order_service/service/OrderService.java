package com.epharmacy.pharmacy_order_service.service;

import java.util.List;

import com.epharmacy.pharmacy_order_service.dto.requestdto.CancelOrderRequestDto;
import com.epharmacy.pharmacy_order_service.dto.requestdto.PlaceOrderRequestDto;
import com.epharmacy.pharmacy_order_service.dto.responsedto.OrderResponseDto;

public interface OrderService {
OrderResponseDto placeorder(PlaceOrderRequestDto placeorderRequestdto);
List<OrderResponseDto> getCustomerorders(Long customerId);
OrderResponseDto cancelOrder(Long orderId,CancelOrderRequestDto cabcelOrderrequestdto);

}

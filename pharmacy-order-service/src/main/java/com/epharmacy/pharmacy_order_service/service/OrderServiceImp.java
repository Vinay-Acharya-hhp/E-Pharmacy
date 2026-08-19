package com.epharmacy.pharmacy_order_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.epharmacy.pharmacy_order_service.apiResponse.ApiResponse;
import com.epharmacy.pharmacy_order_service.dto.requestdto.CancelOrderRequestDto;
import com.epharmacy.pharmacy_order_service.dto.requestdto.PlaceOrderRequestDto;
import com.epharmacy.pharmacy_order_service.dto.responsedto.AddressDTO;
import com.epharmacy.pharmacy_order_service.dto.responsedto.OrderResponseDto;
import com.epharmacy.pharmacy_order_service.entity.DeliveryStatus;
import com.epharmacy.pharmacy_order_service.entity.Order;
import com.epharmacy.pharmacy_order_service.entity.OrderStatus;
import com.epharmacy.pharmacy_order_service.exception.AddressNotFoundException;
import com.epharmacy.pharmacy_order_service.feignclient.CustomerAddressFeignClient;
import com.epharmacy.pharmacy_order_service.repository.OrderItemrepo;
import com.epharmacy.pharmacy_order_service.repository.OrderRepo;

@Service
public class OrderServiceImp implements OrderService{
	
	private OrderRepo orderrepo;
	private OrderItemrepo orderItemrepo;
	private ModelMapper modelmapper;
	private CustomerAddressFeignClient customerAddressFeignClient;
	

	public OrderServiceImp(OrderRepo orderrepo, OrderItemrepo orderItemrepo,
			ModelMapper modelmapper,CustomerAddressFeignClient customerAddressFeignClient) {
		this.orderrepo = orderrepo;
		this.orderItemrepo = orderItemrepo;
		this.modelmapper = modelmapper;
		this.customerAddressFeignClient=customerAddressFeignClient;
	}

	@Override
	public OrderResponseDto placeorder(Long customerId,PlaceOrderRequestDto placeorderRequestdto) {
		
		ApiResponse<AddressDTO> address = customerAddressFeignClient
				 .getaddress(placeorderRequestdto.getAddressId(), customerId);
		if (address == null || address.getData() == null) {
	        throw new AddressNotFoundException("Address not found for customer " + customerId);
	    }

			        
			       
		Order order=new Order();
		order.setCustomerId(customerId);
		order.setAddressId(placeorderRequestdto.getAddressId());
		order.setOrdervalueBeforeDiscount(placeorderRequestdto.getOrderValueBeforeDiscount());
		double discount=calculatediscount(placeorderRequestdto.getOrderValueBeforeDiscount());
		order.setDiscountAmount(discount);
		order.setHealthCoinUsed(0.0);
		double finalvalue=placeorderRequestdto.getOrderValueBeforeDiscount()-discount;
		order.setFinalAmount(finalvalue);
		order.setOrderDate(LocalDateTime.now());
		order.setExpectedDeliveryDate(LocalDate.now().plusDays(2));
		order.setOrderStatus(OrderStatus.PROCESSING);
		order.setDeliveryStatus(DeliveryStatus.AWAITING_CONFIRMATION);
		order.setOrderId(order.getOrderId());
		orderrepo.save(order);
		return modelmapper.map(order, OrderResponseDto.class);
	}

	private double calculatediscount(Double amount) {

		if(amount<=99) {
			return amount*0.1;
		}else if(amount <=599) {
			return amount*0.15;
		}else if(amount<=999) {
			return amount*0.2;
		}else {
			return amount*0.25;
		}
		
	}

	@Override
	public List<OrderResponseDto> getCustomerorders(Long customerId) {
		List<Order> list=orderrepo.findByCustomerId(customerId);
		return list.stream().map(orderlist ->modelmapper.map(orderlist,OrderResponseDto.class )).toList();
	}

	@Override
	public OrderResponseDto cancelOrder(Long orderId, CancelOrderRequestDto cabcelOrderrequestdto) {
		Order order =orderrepo.findById(orderId).orElseThrow();
		if(order.getOrderStatus()!=OrderStatus.PROCESSING) {
			
		}
		order.setOrderStatus(OrderStatus.CANCELLED);
		order.setDeliveryStatus(DeliveryStatus.CANCELLED);
		Order cancelOrder=orderrepo.save(order);
		
		return modelmapper.map(cancelOrder, OrderResponseDto.class);
	}
	
	
}

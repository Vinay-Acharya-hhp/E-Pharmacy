package com.epharmacy.pharmacy_order_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epharmacy.pharmacy_order_service.apiResponse.ApiResponse;
import com.epharmacy.pharmacy_order_service.configuration.JWTService;
import com.epharmacy.pharmacy_order_service.dto.requestdto.CancelOrderRequestDto;
import com.epharmacy.pharmacy_order_service.dto.requestdto.PlaceOrderRequestDto;
import com.epharmacy.pharmacy_order_service.dto.responsedto.AddressDTO;
import com.epharmacy.pharmacy_order_service.dto.responsedto.CartResponseDto;
import com.epharmacy.pharmacy_order_service.dto.responsedto.MedicineResponseDTO;
import com.epharmacy.pharmacy_order_service.dto.responsedto.OrderItemResponseDto;
import com.epharmacy.pharmacy_order_service.dto.responsedto.OrderPaymentResponseDto;
import com.epharmacy.pharmacy_order_service.dto.responsedto.OrderResponseDto;
import com.epharmacy.pharmacy_order_service.entity.DeliveryStatus;
import com.epharmacy.pharmacy_order_service.entity.Order;
import com.epharmacy.pharmacy_order_service.entity.OrderItem;
import com.epharmacy.pharmacy_order_service.entity.OrderStatus;
import com.epharmacy.pharmacy_order_service.exception.AddressNotFoundException;
import com.epharmacy.pharmacy_order_service.feignclient.CartItemFeignClient;
import com.epharmacy.pharmacy_order_service.feignclient.CustomerAddressFeignClient;
import com.epharmacy.pharmacy_order_service.feignclient.MedicineFeignClient;
import com.epharmacy.pharmacy_order_service.repository.OrderItemrepo;
import com.epharmacy.pharmacy_order_service.repository.OrderRepo;

import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service

public class OrderServiceImp implements OrderService{
	@Autowired
	private OrderRepo orderrepo;
	@Autowired
	private OrderItemrepo orderItemrepo;
	@Autowired
	private ModelMapper modelmapper;
	@Autowired
	private CustomerAddressFeignClient customerAddressFeignClient;
	@Autowired
	private CartItemFeignClient cartItemFeignClient;
	@Autowired
	private MedicineFeignClient medicineFeignClient;
	



		@Override
		@Transactional
		public OrderResponseDto placeorder(
		        Long customerId,
		        PlaceOrderRequestDto placeorderRequestdto) {

		    System.out.println("Customer ID: " + customerId);
		    System.out.println(
		            "Address ID: "
		                    + placeorderRequestdto.getDeliveryAddress().getAddressId()
		    );

		    ApiResponse<AddressDTO> addressResponse;

		    try {

		        addressResponse =
		                customerAddressFeignClient.getaddress(
		                        placeorderRequestdto.getDeliveryAddress().getAddressId(),
		                        customerId
		                );

		    } catch (FeignException e) {

		        throw new AddressNotFoundException(
		                "Address not found for customer "
		                        + customerId
		        );
		    }

		    if (addressResponse == null
		            || addressResponse.getData() == null) {

		        throw new AddressNotFoundException(
		                "Address not found for customer "
		                        + customerId
		        );
		    }

		    Order order = new Order();

		    order.setCustomerId(customerId);
		    Long addressId=placeorderRequestdto.getDeliveryAddress().getAddressId();

		    order.setAddressId(addressId
		            
		    );

		    ApiResponse<List<CartResponseDto>> cartResponse;

		    try {

		        cartResponse =
		                cartItemFeignClient
		                        .getCartMedicine(customerId);

		    } catch (FeignException e) {

		        throw new IllegalStateException(
		                "Unable to get cart for customer "
		                        + customerId
		        );
		    }


		    if (cartResponse == null
		            || cartResponse.getData() == null
		            || cartResponse.getData().isEmpty()) {

		        throw new IllegalStateException(
		                "Cart is empty"
		        );
		    }


		    List<CartResponseDto> cartItems =
		            cartResponse.getData();

		    List<OrderItem> orderItems =
		            new ArrayList<>();


		    for (CartResponseDto cartItem : cartItems) {

		        Long medicineId =
		                cartItem.getMedicineId();

		        ApiResponse<MedicineResponseDTO>
		                medicineResponse;

		        try {

		            medicineResponse =
		                    medicineFeignClient
		                            .getById(medicineId);

		        } catch (FeignException e) {

		            throw new IllegalStateException(
		                    "Medicine not found: "
		                            + medicineId
		            );
		        }


		        if (medicineResponse == null
		                || medicineResponse.getData() == null) {

		            throw new IllegalStateException(
		                    "Medicine not found: "
		                            + medicineId
		            );
		        }


		        MedicineResponseDTO medicine =
		                medicineResponse.getData();


		      
		        OrderItem orderItem =
		                new OrderItem();


		        orderItem.setMedicineId(
		                medicine.getId()
		        );


		        orderItem.setMedicineName(
		                medicine.getMedicineName()
		        );


		        orderItem.setQuantity(
		                cartItem.getQuantity()
		        );


		        orderItem.setPrice(
		                medicine.getPrice()
		        );


		        // Important
		        orderItem.setOrder(order);


		        orderItems.add(orderItem);
		    }


		    order.setOrderItems(orderItems);


		    double total =
		            orderItems.stream()
		                    .mapToDouble(item ->
		                            item.getPrice()
		                                    * item.getQuantity()
		                    )
		                    .sum();


		    double discount =
		            calculatediscount(total);


		    double finalAmount =
		            total - discount;


		    order.setDiscountAmount(discount);

		    order.setFinalAmount(finalAmount);

		    order.setOrderDate(
		            LocalDateTime.now()
		    );


		    order.setExpectedDeliveryDate(
		            LocalDate.now().plusDays(2)
		    );


		    order.setOrderStatus(
		            OrderStatus.PROCESSING
		    );


		    order.setDeliveryStatus(
		            DeliveryStatus.AWAITING_CONFIRMATION
		    );


		    Order savedOrder =
		            orderrepo.save(order);


		    OrderResponseDto response =
		            new OrderResponseDto();


		    response.setOrderId(
		            savedOrder.getOrderId()
		    );

		    response.setCustomerId(
		            savedOrder.getCustomerId()
		    );

		    response.setAddressId(
		            savedOrder.getAddressId()
		    );

		    response.setDiscount(
		            savedOrder.getDiscountAmount()
		    );

		    response.setAmountPaid(
		            savedOrder.getFinalAmount()
		    );

		    response.setOrderDate(
		            savedOrder.getOrderDate()
		    );

		    response.setExpectedDeliveryDate(
		            savedOrder.getExpectedDeliveryDate()
		    );

		    response.setOrderStatus(
		            savedOrder.getOrderStatus()
		    );

		    response.setDeliveryStatus(
		            savedOrder.getDeliveryStatus()
		    );

		    List<OrderItemResponseDto> itemResponses =
		            savedOrder.getOrderItems()
		                    .stream()
		                    .map(item -> {

		                        OrderItemResponseDto itemDto =
		                                new OrderItemResponseDto();


		                        itemDto.setOrderItemId(
		                                item.getOrderItemId()
		                        );


		                        itemDto.setMedicineId(
		                                item.getMedicineId()
		                        );


		                        itemDto.setMedicineName(
		                                item.getMedicineName()
		                        );


		                        itemDto.setQuantity(
		                                item.getQuantity()
		                        );


		                        itemDto.setPrice(
		                                item.getPrice()
		                        );


		                        return itemDto;

		                    })
		                    .toList();
		    response.setOrderItems(itemResponses);
		    return response;
	}
	    
	    private double calculatediscount(double total) {

	        if (total > 1000) {

	            return total * 0.15;

	        } else if (total > 500) {

	            return total * 0.10;

	        } else if (total > 100) {

	            return total * 0.05;

	        } else {

	            return 0;
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

	@Override
	public void paymentSuccess(Long orderId, Long paymentId) {

	    Order order =
	            
	    		orderrepo.findById(orderId).orElseThrow(()->new IllegalStateException(
                        "Order not found with id: "
                                + orderId
                ));
	                    


	    // 2. Check whether already processed

	    if (order.getOrderStatus()
	            == OrderStatus.CONFIRMED) {

	        return;
	    }


	    // 3. Make sure payment was expected

	    if (order.getOrderStatus()
	            != OrderStatus.PROCESSING) {

	        throw new IllegalStateException(
	                "Order is not waiting for payment"
	        );
	    }


	    // 4. Save payment ID

	    order.setPaymentId(paymentId);


	    // 5. Change order status

	    order.setOrderStatus(
	            OrderStatus.CONFIRMED
	    );


	    // 6. Save order

	    orderrepo.save(order);


	    // 7. Get order items

	    List<OrderItem> orderItems =
	            orderItemrepo
	                    .findByOrderItemId(orderId);


	    // 8. Reduce medicine quantity

	    for (OrderItem item : orderItems) {

	        medicineFeignClient.updateStock(
	                item.getMedicineId(),
	                item.getQuantity()
	        );
	    }


	    // 9. Clear customer's cart

	    cartItemFeignClient.deleteAllMedicine(
	            order.getCustomerId()
	    );
		
	}

	@Override
	public OrderPaymentResponseDto getidamount(Long orderId) {
		Order order =orderrepo.findById(orderId).orElseThrow();
		OrderPaymentResponseDto response=new OrderPaymentResponseDto();
		response.setAmount(order.getFinalAmount());
		response.setOrderId(order.getOrderId());
		response.setOrderStatus(order.getOrderStatus());
		return response;
	}
	
	
}

package com.epharmacy.pharmacy_order_service.controler;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epharmacy.pharmacy_order_service.apiResponse.ApiResponse;
import com.epharmacy.pharmacy_order_service.configuration.JWTService;
import com.epharmacy.pharmacy_order_service.dto.requestdto.CancelOrderRequestDto;
import com.epharmacy.pharmacy_order_service.dto.requestdto.PlaceOrderRequestDto;
import com.epharmacy.pharmacy_order_service.dto.responsedto.OrderResponseDto;
import com.epharmacy.pharmacy_order_service.service.OrderService;

@RestController
@RequestMapping("/order")
public class OrderContoller {
	private OrderService service;
	private JWTService jwtService;
	
	public OrderContoller(OrderService service,JWTService jwtService) {
		this.service = service;
		this.service=service;
	}
	@PostMapping("/place-order")
	public ResponseEntity<ApiResponse<OrderResponseDto>> placeorder
	(@RequestHeader("Authorization")String authorization,
			@RequestBody PlaceOrderRequestDto placeorderRequestdto
			 ) {
		String token =authorization.trim();
		if(token.startsWith("Bearer ")) {
			token=token.substring(7).trim();
		}
		Long customerId=jwtService.extractCustomerId(token);
		OrderResponseDto data =service.placeorder(customerId,placeorderRequestdto);
		ApiResponse<OrderResponseDto> response = new ApiResponse<>(data,true,201);
		return new ResponseEntity<>(response,HttpStatus.CREATED);
	}
	@GetMapping("/view-order/customer/{customerId}")
	public ResponseEntity<ApiResponse<List<OrderResponseDto>>> getCustomerorders(@PathVariable Long customerId){
		List<OrderResponseDto> data =service.getCustomerorders(customerId);
		ApiResponse<List<OrderResponseDto>> response= new ApiResponse<>(data,true,200);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	@PutMapping("cancel-order/{orderId}")
	public ResponseEntity<ApiResponse<OrderResponseDto>> cancelOrder(@PathVariable Long orderId,@RequestBody CancelOrderRequestDto cabcelOrderrequestdto) {
		OrderResponseDto data=service.cancelOrder(orderId, cabcelOrderrequestdto);
		ApiResponse<OrderResponseDto> response=new ApiResponse<>(data,true,200);
		return new ResponseEntity<>(response,HttpStatus.OK);
	}

}

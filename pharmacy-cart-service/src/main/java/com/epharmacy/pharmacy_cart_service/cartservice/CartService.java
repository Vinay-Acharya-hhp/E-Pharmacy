package com.epharmacy.pharmacy_cart_service.cartservice;

import java.util.List;

import com.epharmacy.pharmacy_cart_service.dto.requestdto.AddCartRequestDto;
import com.epharmacy.pharmacy_cart_service.dto.requestdto.UpdateQuantityDto;
import com.epharmacy.pharmacy_cart_service.dto.responsedto.CartResponseDto;

public interface CartService {
CartResponseDto addMedicineToCart(Long medicineId,
		                          Long customerId,
		                          AddCartRequestDto addrequestdto);
List<CartResponseDto> getCartMedicine( Long customerId);

CartResponseDto updateQuantity(Long medicineId,
        Long customerId,
       UpdateQuantityDto updatequantiyrequestdto);
void deleteMedicine(Long medicineId,Long customerId);
void deleteAllMedicine(Long customerId);
}

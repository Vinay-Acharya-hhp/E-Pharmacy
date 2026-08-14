package com.epharmacy.pharmacy_cart_service.cartservice;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.epharmacy.pharmacy_cart_service.dto.requestdto.AddCartRequestDto;
import com.epharmacy.pharmacy_cart_service.dto.requestdto.UpdateQuantityDto;
import com.epharmacy.pharmacy_cart_service.dto.responsedto.CartResponseDto;
import com.epharmacy.pharmacy_cart_service.entity.Cart;
import com.epharmacy.pharmacy_cart_service.exception.CartIsEmptyException;
import com.epharmacy.pharmacy_cart_service.exception.MedicineAlreadyExistsException;
import com.epharmacy.pharmacy_cart_service.exception.MedicineNotFoundException;
import com.epharmacy.pharmacy_cart_service.repository.CartRepo;

@Service
public class CartServiceImp implements CartService {

    private final ModelMapper modelMapper;
    private final CartRepo repo;

    public CartServiceImp(ModelMapper modelMapper, CartRepo repo) {
        this.modelMapper = modelMapper;
        this.repo = repo;
    }

    @Override
    public CartResponseDto addMedicineToCart(
            Long medicineId,
            Long customerId,
            AddCartRequestDto addrequestdto) {

        // Validate quantity
        if (addrequestdto.getQuantity() == null|| addrequestdto.getQuantity() <= 0) {

            throw new IllegalArgumentException(      "Quantity must be greater than zero");
        }

        // Check whether medicine already exists in customer's cart
        if (repo.findByCustomerIdAndMedicineId( customerId,medicineId)         
        .isPresent()) {
            throw new MedicineAlreadyExistsException(
                    "Medicine is already present in the cart"
            );
        }

        // Create Cart entity
        Cart cart = new Cart();

        cart.setCustomerId(customerId);
        cart.setMedicineId(medicineId);
        cart.setQuantity(addrequestdto.getQuantity());

        // Save
        Cart savedCart = repo.save(cart);

        // Convert Entity -> DTO
        return modelMapper.map(
                savedCart,
                CartResponseDto.class
        );
    }

    @Override
    public List<CartResponseDto> getCartMedicine(Long customerId) {

        List<Cart> cartList = repo.findByCustomerId(customerId);

        return cartList.stream()
                .map(cart -> modelMapper.map(   cart,  CartResponseDto.class )) .toList();
                      
                         
    }

    @Override
    public CartResponseDto updateQuantity(
            Long medicineId,
            Long customerId,
            UpdateQuantityDto updatequantityrequestdto) {

        // Validate quantity
        if (updatequantityrequestdto.getQuantity() == null
                || updatequantityrequestdto.getQuantity() <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        // Find cart item
        Cart cart = repo.findByCustomerIdAndMedicineId(
                customerId,
                medicineId
        ).orElseThrow(() ->
                new RuntimeException(
                        "Medicine not found in cart"
                )
        );

        // Update quantity
        cart.setQuantity(
                updatequantityrequestdto.getQuantity()
        );

        // Save
        Cart updatedCart = repo.save(cart);

        // Convert Entity -> DTO
        return modelMapper.map(
                updatedCart,
                CartResponseDto.class
        );
    }

    @Override
    public void deleteMedicine(
            Long medicineId,
            Long customerId) {

        Cart cart = repo.findByCustomerIdAndMedicineId(
                customerId,
                medicineId
        ).orElseThrow(() ->
                new MedicineNotFoundException(
                        "Medicine not found in cart"
                )
        );

        repo.delete(cart);
    }

    @Override
    public void deleteAllMedicine(Long customerId) {

        List<Cart> cartList = repo.findByCustomerId(customerId);

        if (cartList.isEmpty()) {
            throw new CartIsEmptyException(
                    "Cart is already empty"
            );
        }

        repo.deleteByCustomerId(customerId);
    }
}
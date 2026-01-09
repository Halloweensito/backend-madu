package com.Osiris.backendMadu.Mapper;

import com.Osiris.backendMadu.DTO.Order.OrderItemResponse;
import com.Osiris.backendMadu.DTO.Order.OrderResponse;
import com.Osiris.backendMadu.DTO.Order.OrderSummaryResponse;
import com.Osiris.backendMadu.Entity.Order;
import com.Osiris.backendMadu.Entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    OrderResponse toResponse(Order order);

    @Mapping(target = "total", source = ".", qualifiedByName = "calculateLineTotal")
    OrderItemResponse toItemResponse(OrderItem item);

    @Mapping(target = "itemsCount", expression = "java(order.getItems() != null ? order.getItems().size() : 0)")
    OrderSummaryResponse toSummaryResponse(Order order);


    @Named("calculateLineTotal")
    default BigDecimal calculateLineTotal(OrderItem item) {
        if (item.getPrice() == null || item.getQuantity() == null) {
            return BigDecimal.ZERO;
        }
        return item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
    }
}
package com.Osiris.backendMadu.Mapper;

import com.Osiris.backendMadu.DTO.Order.OrderItemResponse;
import com.Osiris.backendMadu.DTO.Order.OrderRequest;
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

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "status", ignore = true) // lo seteas en el service
    @Mapping(target = "subtotal", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "items", ignore = true) // se agregan en el service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toEntity(OrderRequest dto);

}
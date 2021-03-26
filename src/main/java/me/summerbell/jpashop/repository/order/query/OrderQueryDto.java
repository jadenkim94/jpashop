package me.summerbell.jpashop.repository.order.query;

import lombok.Getter;
import lombok.Setter;
import me.summerbell.jpashop.domain.Address;
import me.summerbell.jpashop.domain.OrderItem;
import me.summerbell.jpashop.domain.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
public class OrderQueryDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Address address;
    private List<OrderItemQueryDto> orderItems;

    public OrderQueryDto(Long orderId, String name, LocalDateTime orderDate, OrderStatus status, Address address) {
        this.orderId = orderId;
        this.name = name;
        this.orderDate = orderDate;
        this.status = status;
        this.address = address;
    }
}

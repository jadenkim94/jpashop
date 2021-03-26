package me.summerbell.jpashop.api;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.summerbell.jpashop.domain.Address;
import me.summerbell.jpashop.domain.Order;
import me.summerbell.jpashop.domain.OrderItem;
import me.summerbell.jpashop.domain.OrderStatus;
import me.summerbell.jpashop.repository.OrderSearch;
import me.summerbell.jpashop.repository.order.OrderRepository;
import me.summerbell.jpashop.repository.order.query.OrderFlatDto;
import me.summerbell.jpashop.repository.order.query.OrderQueryDto;
import me.summerbell.jpashop.repository.order.query.OrderQueryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    /**
     * 엔티티 직접 노출
     * Hibernate5Module 을 사용하고
     * N+1 문제가 발생하는
     * 최악의 상황
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }


    /**
     * DTO 반환 /// Item 엔티티 노출은? item 역시 dto 로 감쌀것
     * 여전히 n+1
     */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }

    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3(){
        List<Order> orders = orderRepository.findAllWithItem();
        return orders.stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
    }

    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit){

        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);
        return orders.stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4(){
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5(){
        return orderQueryRepository.findOrderQueryDto_optimization();
    }

    @GetMapping("/api/v6/orders")
    public List<OrderFlatDto> ordersV6(){
        return orderQueryRepository.findOrderQueryDto_flat();
    }



    @Getter
    static class OrderDto{

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());
        }
    }

    @Getter
    static class OrderItemDto{

        private String itemName;    // 상품명
        private int orderPrice;     // 주문가격
        private int count;          // 주문수량

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();

        }

    }

}

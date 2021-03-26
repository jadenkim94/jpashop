package me.summerbell.jpashop.service;

import lombok.RequiredArgsConstructor;
import me.summerbell.jpashop.domain.Delivery;
import me.summerbell.jpashop.domain.Member;
import me.summerbell.jpashop.domain.Order;
import me.summerbell.jpashop.domain.OrderItem;
import me.summerbell.jpashop.domain.item.Item;
import me.summerbell.jpashop.repository.ItemRepository;
import me.summerbell.jpashop.repository.MemberRepositoryOld;
import me.summerbell.jpashop.repository.order.OrderRepository;
import me.summerbell.jpashop.repository.OrderSearch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepositoryOld memberRepository;
    private final ItemRepository itemRepository;


    /**
     *  주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count){
        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);
        return order.getId();
    }

    /**
     *  취소
     */
    @Transactional
    public void cancelOrder(Long orderId){
        // 주문 엔테티 조회
        Order order = orderRepository.findOne(orderId);
        // 주문 취소
        order.cancel();
    }


    /**
     *  검색
     */
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAll(orderSearch);
    }
    
}

package me.summerbell.jpashop.service;

import me.summerbell.jpashop.domain.Address;
import me.summerbell.jpashop.domain.Member;
import me.summerbell.jpashop.domain.Order;
import me.summerbell.jpashop.domain.OrderStatus;
import me.summerbell.jpashop.domain.item.Book;
import me.summerbell.jpashop.domain.item.Item;
import me.summerbell.jpashop.exception.NotEnoughStockException;
import me.summerbell.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;


    @Test
    void 상품주문() throws Exception {
    //given
        Member member = createMember();

        Item book = createBook("JPA", 10000, 10);

        //when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
    //then
        Order getOrder = orderRepository.findOne(orderId);
        // 상품주문시 상태는 ORDER
        assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.ORDER);
        // 주문한 상품종류 수 확인
        assertThat(getOrder.getOrderItems()).size().isEqualTo(1);
        // 주문 가격은 가격 * 수량
        assertThat(getOrder.getTotalPrice()).isEqualTo(book.getPrice() * orderCount);
        // 주문 수량만큼 재고가 주는지
        assertThat(book.getStockQuantity()).isEqualTo(8);

    }

    @Test
    void 재고수량초과_상품주문() throws Exception {
        //given
        Member member = createMember();
        Item book =  createBook("JPA", 10000, 10);

        int orderCount = 11;

        //when //then 재고수량 예외발생해야한다.
        assertThrows(NotEnoughStockException.class, ()->{
            orderService.order(member.getId(), book.getId(), orderCount);
        });

    }

    @Test
    void 주문취소() throws Exception {
    //given
        Member member = createMember();
        Item book = createBook("JPA", 10000, 10);
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

    //when
        orderService.cancelOrder(orderId);

    //then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(getOrder.getStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(book.getStockQuantity()).isEqualTo(10);


    }

    private Item createBook(String name, int price, int stockQuantity) {
        Item book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }



}
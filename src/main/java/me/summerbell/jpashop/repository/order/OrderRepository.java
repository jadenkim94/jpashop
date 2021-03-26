package me.summerbell.jpashop.repository.order;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.summerbell.jpashop.domain.Order;
import me.summerbell.jpashop.domain.OrderStatus;
import me.summerbell.jpashop.domain.QMember;
import me.summerbell.jpashop.domain.QOrder;
import me.summerbell.jpashop.repository.OrderSearch;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }


    // queryDsl
    public List<Order> findAll(OrderSearch orderSearch){
        JPAQueryFactory query = new JPAQueryFactory(em);
        QOrder order = QOrder.order;
        QMember member = QMember.member;

        return query
                .select(order)
                .from(order)
                .join(order.member, member)
                .where(statusEq(orderSearch.getOrderStatus()), nameLike(orderSearch.getMemberName()))
                .limit(1000)
                .fetch();
    }
    private BooleanExpression nameLike(String memberName){
        if(!StringUtils.hasText(memberName)){
            return null;
        }
        return QMember.member.name.like(memberName);
    }

    private BooleanExpression statusEq(OrderStatus statusCond){
        if(statusCond == null){
            return null;
        }
        return QOrder.order.status.eq(statusCond);
    }


    /*public List<Order> findAll(OrderSearch orderSearch){
        List<Order> resultList = em.createQuery("select o from Order o join o.member m" +
                " where o.status = :status " +
                " and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())
                .setParameter("name", orderSearch.getMemberName())
                .setFirstResult(0)
                .setMaxResults(100)
                .getResultList();

    }*/
    public List<Order> findAllByCriteria(OrderSearch orderSearch){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        if(orderSearch.getOrderStatus() != null){
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        if(StringUtils.hasText(orderSearch.getMemberName())){
            Predicate name =
                    cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o"+
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }


    /**
     * distinct 를 사용해 SQL 에 distinct 가 추가되고, 그에 더해 같은 엔티티가 조회되면 애플리케이션에서 중복을 걸러줌
     * 하지만 이 경우 페이징을 처리하면 1:n 기준 1을 기준으로 페이징처리를 해야하는 상황에서, n 을 기준으로 row 가 생성됨으로
     * 하이버네이트는 경고를 남기고 DB데이터를 모두 읽어서 메모리에서 페이징을 시도. -> outOfMemory 유발
     */
    public List<Order> findAllWithItem() {
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class)
                .getResultList();
    }



    /**
     * 한계돌파!
     * 대부분의 페이징 + 컬렉션 엔티티 조회문제는 이 방법으로 해결할 수 있다.
     *
     * 1. 먼저 ToOne 관계는 필요한 경우 페치조인한다. ( ToOne 관계는 row 수가 order 와 동일하다 )
     * 2. 컬렉션은 지연로딩으로 조회한다.
     * 3. 지연 로딩 성능 최적화를 위해 hibernate.default_batch_fetch_size, @BatchSize 를 적용한다
     */
    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o"+
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();

    }



}

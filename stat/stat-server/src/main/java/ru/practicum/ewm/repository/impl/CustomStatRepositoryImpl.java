package ru.practicum.ewm.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;
import ru.practicum.ewm.repository.CustomStatRepository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@Transactional
@RequiredArgsConstructor
public class CustomStatRepositoryImpl implements CustomStatRepository {

    private final EntityManager entityManager;

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ViewStats> criteriaQuery = criteriaBuilder.createQuery(ViewStats.class);
        Root<EndpointHit> statsRoot = criteriaQuery.from(EndpointHit.class);
        List<Predicate> predicateList = new ArrayList<>();

        criteriaQuery.select(criteriaBuilder.construct(ViewStats.class,
                statsRoot.get("app"),
                statsRoot.get("uri"),
                unique ? criteriaBuilder.countDistinct(statsRoot.get("ip")) : criteriaBuilder.count(statsRoot.get("ip")))
        );

        criteriaQuery.groupBy(
                statsRoot.get("app"),
                statsRoot.get("uri"),
                statsRoot.get("ip")
        );

        criteriaQuery.orderBy(criteriaBuilder
                .desc(unique ? criteriaBuilder.countDistinct(statsRoot.get("ip")) : criteriaBuilder.count(statsRoot.get("ip"))));

        predicateList.add(criteriaBuilder.between(statsRoot.get("timestamp"), start, end));

        if (uris != null)
            predicateList.add(criteriaBuilder.and(statsRoot.get("uri").in(uris)));

        criteriaQuery.where(predicateList.toArray(Predicate[]::new));

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
package ru.practicum.ewm.repositorys.event.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.entity.enums.Sort;
import ru.practicum.ewm.entity.enums.Status;
import ru.practicum.ewm.entity.model.Event;
import ru.practicum.ewm.entity.model.QEvent;
import ru.practicum.ewm.repositorys.event.CustomEventRepository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class CustomEventRepositoryImpl implements CustomEventRepository {

    private final EntityManager entityManager;

    @Override
    public List<Event> adminGetEventsByParams(List<Long> users,
                                              List<Status> states,
                                              List<Long> categories,
                                              LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd,
                                              Integer from,
                                              Integer size) {

        BooleanExpression where = Expressions.ONE.eq(1);

        if (users != null && !users.isEmpty()) {
            where = where.and(QEvent.event.initiator.id.in(users));
        }

        if (states != null && !states.isEmpty()) {
            where = where.and(QEvent.event.state.in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            where = where.and(QEvent.event.category.id.in(categories));
        }

        if (rangeStart != null) {
            where = where.and(QEvent.event.eventDate.after(rangeStart));
        }

        if (rangeEnd != null) {
            where = where.and(QEvent.event.eventDate.before(rangeEnd));
        }

        return new JPAQuery<>(entityManager)
                .select(QEvent.event)
                .from(QEvent.event)
                .where(where)
                .offset(from)
                .limit(size)
                .fetch();
    }

    @Override
    public List<Event> publicGetEventsByParams(String text,
                                               List<Long> categories,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Boolean onlyAvailable,
                                               Sort sort,
                                               Integer from,
                                               Integer size, Status defaultStatus) {

        BooleanExpression where = QEvent.event.state.eq(defaultStatus);

        where = where.and(QEvent.event.state.eq(Status.PUBLISHED));

        if (text != null && !text.isEmpty()) {
            where.and(QEvent.event.annotation.containsIgnoreCase(text)).or(QEvent.event.description.containsIgnoreCase(text));
        }

        if (categories != null && !categories.isEmpty()) {
            where.and(QEvent.event.category.id.in(categories));
        }

        where.and(QEvent.event.eventDate.between(rangeStart, rangeEnd));

        if (rangeStart == null && rangeEnd == null) {
            where.and(QEvent.event.eventDate.after(LocalDateTime.now()));
        }

        return new JPAQuery<>(entityManager)
                .select(QEvent.event)
                .from(QEvent.event)
                .where(where)
                .orderBy(QEvent.event.eventDate.desc())
                .offset(from)
                .limit(size)
                .fetch();
    }
}

package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findEventByIdAndInitiatorId(long eventId, long userId);

    List<Event> findEventsByInitiatorId(long userId, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE e.category.id = :categoryId")
    List<Event> findFirstByOrderByCategoryAsc(long categoryId, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE e.id IN :eventIds")
    List<Event> findEventsByIds(List<Long> eventIds);

//    @Query("select event from Event event " +
//            "where event.id IN (:ids)")
//    List<Event> findByIds(@Param("ids") List<Long> ids);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE LOWER(e.annotation) LIKE %:text% OR LOWER(e.description) LIKE %:text% " +
            "AND (:categories is null OR e.category.id IN (:categories)) " +
            "AND e.paid = :paid " +
            "AND (:state is null OR e.eventState = :state) " +
            "AND (coalesce(:start, 'null') is null OR e.eventDate >= :start) " +
            "AND (coalesce(:end, 'null') is null OR e.eventDate <= :end)")
    List<Event> findPublicEvents(String text, List<Long> categories, boolean paid, LocalDateTime start,
                                 LocalDateTime end, EventState state, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE (:users is null OR e.initiator.id IN (:users)) " +
            "AND (:states is null OR e.eventState IN (:states)) " +
            "AND (:categories is null OR e.category.id IN (:categories)) " +
            "AND (coalesce(:start, 'null') is null OR e.eventDate >= :start) " +
            "AND (coalesce(:end, 'null') is null OR e.eventDate <= :end) ")
    List<Event> findAdminEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                LocalDateTime start, LocalDateTime end, Pageable pageable);
}
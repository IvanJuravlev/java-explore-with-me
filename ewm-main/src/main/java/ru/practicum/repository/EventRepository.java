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
            "WHERE e.id IN :eventIds")
    List<Event> findEventsByIds(List<Long> eventIds);


    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE (:users is null OR e.initiator.id IN (:users)) " +
            "AND (:states is null OR e.eventState IN (:states)) " +
            "AND (:categories is null OR e.category.id IN (:categories)) " +
            "AND (coalesce(:start, 'null') is null OR e.eventDate >= :start) " +
            "AND (coalesce(:end, 'null') is null OR e.eventDate <= :end) ")
    List<Event> findAdminEvents(List<Long> users, List<EventState> states, List<Long> categories,
                                LocalDateTime start, LocalDateTime end, Pageable pageable);


    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE " +
            "(" +
            ":text IS NULL " +
            "OR LOWER(e.description) LIKE CONCAT('%', :text, '%') " +
            "OR LOWER(e.annotation) LIKE CONCAT('%', :text, '%')" +
            ")" +
            "AND (:states IS NULL OR e.eventState IN (:states)) " +
            "AND (:categories IS NULL OR e.category.id IN (:categories)) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (CAST(:rangeStart AS date) IS NULL OR e.eventDate >= :rangeStart) " +
            "AND (CAST(:rangeEnd AS date) IS NULL OR e.eventDate <= :rangeEnd) " +
            "order by e.eventDate")
    List<Event> findPublicEvents(
            @Param("text") String text,
            @Param("states") List<EventState> states,
            @Param("categories") List<Long> categories,
            @Param("paid") Boolean paid,
            @Param("rangeStart") LocalDateTime rangeStart,
            @Param("rangeEnd") LocalDateTime rangeEnd,
            Pageable pageable);
}
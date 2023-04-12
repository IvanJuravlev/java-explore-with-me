package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.RequestStatus;

import java.util.Optional;
import java.util.List;

@Repository
public interface RequestsRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequesterId(Long userId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

    @Query("select request from Request request " +
            "where request.event.id = :eventId " +
            "and request.requester.id = :userId")
    Optional<Request> findByEventIdAndRequesterId(@Param("eventId") Long eventId,
                                                               @Param("userId") Long userId);

    @Query("select request from Request request " +
            "where request.event.id = :eventId " +
            "and request.event.initiator.id = :userId")
    List<Request> findByEventIdAndInitiatorId(@Param("eventId") Long eventId,
                                                           @Param("userId") Long userId);

    @Query("select p from Request p " +
            "where p.event.id = :eventId and p.status = 'CONFIRMED'")
    List<Request> findByEventIdConfirmed(@Param("eventId") Long eventId);

    @Query("select p from Request p " +
            "where p.status = 'CONFIRMED' " +
            "and p.event.id IN (:events)")
    List<Request> findConfirmedToListEvents(@Param("events") List<Long> events);

    @Query("select request from Request request " +
            "where request.event.id = :event " +
            "and request.id IN (:requestIds)")
    List<Request> findByEventIdAndRequestsIds(@Param("event") Long eventId,
                                                           @Param("requestIds") List<Long> requestIds);

    List<Request> findAllByEventIdAndStatus(Long eventId, RequestStatus status);
}
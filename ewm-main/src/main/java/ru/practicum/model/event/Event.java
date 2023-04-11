package ru.practicum.model.event;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.Category;
import ru.practicum.model.Location;
import ru.practicum.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "EVENTS")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @Column(name = "created_on")
    LocalDateTime createdOn;

    String description;

    @Column(name = "event_date")
    LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id")
    User initiator;

    @ManyToOne
    @JoinColumn(name = "location_id")
    Location location;

    Boolean paid;

    @Column(name = "participant_limit")
    Integer participantLimit;

    @Column(name = "published_on")
    LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_state")
    EventState eventState;

    String title;
}


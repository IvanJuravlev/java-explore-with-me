package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.event.Event;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "compilations")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToMany
    @JoinTable(name = "compilation_events",
                joinColumns = @JoinColumn(name = "compilation_id"),
                inverseJoinColumns = @JoinColumn(name = "event_id"))
    List<Event> events;
    Boolean pinned;
    String title;

}

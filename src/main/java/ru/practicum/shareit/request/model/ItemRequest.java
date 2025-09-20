package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "requestor_id", nullable = false)
    private Long requesterId;

    @Column(nullable = false)
    private LocalDateTime created;
}

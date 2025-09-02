package ru.practicum.shareit.request.model;

import java.time.LocalDateTime;

public class ItemRequest {
    private Long id;
    private String description;
    private Long requesterId;
    private LocalDateTime created;

    public ItemRequest() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getRequesterId() { return requesterId; }
    public void setRequesterId(Long requesterId) { this.requesterId = requesterId; }
    public LocalDateTime getCreated() { return created; }
    public void setCreated(LocalDateTime created) { this.created = created; }
}

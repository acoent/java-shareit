package ru.practicum.shareit.item.dto;

public class BookingShortDto {
    private Long id;
    private Long bookerId;

    public BookingShortDto() {
    }

    public BookingShortDto(Long id, Long bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookerId() {
        return bookerId;
    }

    public void setBookerId(Long bookerId) {
        this.bookerId = bookerId;
    }
}

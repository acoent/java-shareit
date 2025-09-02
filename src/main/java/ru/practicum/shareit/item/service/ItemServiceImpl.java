package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;
import ru.practicum.shareit.booking.repository.InMemoryBookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final InMemoryItemRepository itemRepo;
    private final InMemoryUserRepository userRepo;
    private final InMemoryBookingRepository bookingRepo;
    private final ConcurrentMap<Long, List<CommentDto>> comments = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, Object> locks = new ConcurrentHashMap<>();

    public ItemServiceImpl(InMemoryItemRepository itemRepo, InMemoryUserRepository userRepo, InMemoryBookingRepository bookingRepo) {
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        if (userId == null || !userRepo.existsById(userId)) throw new NotFoundException("User not found: " + userId);
        validateCreate(itemDto);
        Item model = ItemMapper.toModel(itemDto, userId);
        model.setId(null);
        Item saved = itemRepo.save(model);
        return ItemMapper.toDto(saved);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item existing = itemRepo.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
        if (!existing.getOwnerId().equals(userId)) throw new ForbiddenException("Only owner can edit item");
        if (itemDto.getName() != null) existing.setName(itemDto.getName());
        if (itemDto.getDescription() != null) existing.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) existing.setAvailable(itemDto.getAvailable());
        if (itemDto.getRequestId() != null) existing.setRequestId(itemDto.getRequestId());
        itemRepo.save(existing);
        return ItemMapper.toDto(existing);
    }

    @Override
    public ItemResponseDto getById(Long userId, Long itemId) {
        Item item = itemRepo.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
        BookingShortDto last = null, next = null;
        List<Booking> approved = bookingRepo.findByItemIdAndStatus(itemId, BookingStatus.APPROVED);
        LocalDateTime now = LocalDateTime.now();
        Optional<Booking> lastOpt = approved.stream().filter(b -> b.getEnd().isBefore(now) || b.getEnd().isEqual(now)).max(Comparator.comparing(Booking::getEnd));
        Optional<Booking> nextOpt = approved.stream().filter(b -> b.getStart().isAfter(now) || b.getStart().isEqual(now)).min(Comparator.comparing(Booking::getStart));
        if (lastOpt.isPresent()) last = new BookingShortDto(lastOpt.get().getId(), lastOpt.get().getBookerId());
        if (nextOpt.isPresent()) next = new BookingShortDto(nextOpt.get().getId(), nextOpt.get().getBookerId());

        List<CommentDto> comms = comments.getOrDefault(itemId, Collections.emptyList());
        return ItemMapper.toResponse(item, last, next, comms);
    }

    @Override
    public List<ItemResponseDto> getItemsByOwner(Long ownerId, int from, int size) {
        if (!userRepo.existsById(ownerId)) throw new NotFoundException("User not found: " + ownerId);
        List<Item> all = itemRepo.findAllByOwner(ownerId);
        List<Item> page = paginate(all, from, size);
        return page.stream().map(i -> {
            BookingShortDto last = null, next = null;
            List<Booking> approved = bookingRepo.findByItemIdAndStatus(i.getId(), BookingStatus.APPROVED);
            LocalDateTime now = LocalDateTime.now();
            Optional<Booking> lastOpt = approved.stream().filter(b -> b.getEnd().isBefore(now) || b.getEnd().isEqual(now)).max(Comparator.comparing(Booking::getEnd));
            Optional<Booking> nextOpt = approved.stream().filter(b -> b.getStart().isAfter(now) || b.getStart().isEqual(now)).min(Comparator.comparing(Booking::getStart));
            if (lastOpt.isPresent()) last = new BookingShortDto(lastOpt.get().getId(), lastOpt.get().getBookerId());
            if (nextOpt.isPresent()) next = new BookingShortDto(nextOpt.get().getId(), nextOpt.get().getBookerId());
            return ItemMapper.toResponse(i, last, next, comments.getOrDefault(i.getId(), Collections.emptyList()));
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text, int from, int size) {
        if (text == null || text.isBlank()) return Collections.emptyList();
        List<Item> found = itemRepo.searchAvailableByText(text);
        List<Item> page = paginate(found, from, size);
        return page.stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        if (!userRepo.existsById(userId)) throw new NotFoundException("User not found: " + userId);
        Item item = itemRepo.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found: " + itemId));

        boolean ok = bookingRepo.findByBookerIdAndItemId(userId, itemId).stream().anyMatch(b -> b.getStatus() == BookingStatus.APPROVED && (b.getEnd().isBefore(LocalDateTime.now()) || b.getEnd().isEqual(LocalDateTime.now())));
        if (!ok) throw new BadRequestException("User has not completed an approved booking for this item");

        CommentDto c = new CommentDto();
        c.setId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
        c.setText(commentDto.getText());
        c.setAuthorName(userRepo.findById(userId).map(u -> u.getName()).orElse("Unknown"));
        c.setCreated(LocalDateTime.now());

        comments.compute(itemId, (k, list) -> {
            if (list == null) list = new ArrayList<>();
            list.add(c);
            return list;
        });

        return c;
    }

    private void validateCreate(ItemDto dto) {
        if (dto == null) throw new BadRequestException("Item data required");
        if (dto.getName() == null || dto.getName().isBlank()) throw new BadRequestException("Item name required");
        if (dto.getDescription() == null) throw new BadRequestException("Item description required");
        if (dto.getAvailable() == null) throw new BadRequestException("Field 'available' required");
    }

    private <T> List<T> paginate(List<T> list, int from, int size) {
        if (from < 0) throw new BadRequestException("from must be >= 0");
        if (size <= 0) throw new BadRequestException("size must be > 0");
        int start = Math.min(from, list.size());
        int end = Math.min(start + size, list.size());
        return list.subList(start, end);
    }
}

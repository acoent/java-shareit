package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private final CommentRepository commentRepo;
    private final BookingRepository bookingRepo;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto create(Long ownerId, ItemDto dto) {
        User owner = userRepo.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User not found: " + ownerId));

        Item item = itemMapper.toModel(dto);
        item.setOwner(owner);
        Item saved = itemRepo.save(item);
        return itemMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ItemDto update(Long ownerId, Long itemId, ItemDto dto) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
        if (item.getOwner() == null || !item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Item not found for owner: " + itemId);
        }

        if (dto.getName() != null) item.setName(dto.getName());
        if (dto.getDescription() != null) item.setDescription(dto.getDescription());
        if (dto.getAvailable() != null) item.setAvailable(dto.getAvailable());

        Item saved = itemRepo.save(item);
        return itemMapper.toDto(saved);
    }

    @Override
    public ItemResponseDto getById(Long requesterId, Long itemId) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));

        List<Comment> comments = commentRepo.findByItem_IdOrderByCreatedDesc(itemId);
        List<CommentDto> commentDtos = comments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());

        BookingShortDto lastShort = null;
        BookingShortDto nextShort = null;

        if (item.getOwner() != null && item.getOwner().getId().equals(requesterId)) {
            LocalDateTime now = LocalDateTime.now();

            Optional<Booking> lastOpt = bookingRepo.findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(
                    itemId, BookingStatus.APPROVED, now);
            Optional<Booking> nextOpt = bookingRepo.findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(
                    itemId, BookingStatus.APPROVED, now);

            if (lastOpt.isPresent()) {
                Booking last = lastOpt.get();
                Long lastBookerId = last.getBooker() != null ? last.getBooker().getId() : null;
                lastShort = new BookingShortDto(last.getId(), lastBookerId);
            }
            if (nextOpt.isPresent()) {
                Booking next = nextOpt.get();
                Long nextBookerId = next.getBooker() != null ? next.getBooker().getId() : null;
                nextShort = new BookingShortDto(next.getId(), nextBookerId);
            }
        }

        return itemMapper.toResponseDto(item, lastShort, nextShort, commentDtos);
    }

    @Override
    public List<ItemResponseDto> getByOwner(Long ownerId, int from, int size) {
        userRepo.findById(ownerId).orElseThrow(() -> new NotFoundException("User not found: " + ownerId));

        if (from < 0) from = 0;
        if (size <= 0) throw new BadRequestException("size must be positive");

        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Item> itemsPage = itemRepo.findAllByOwner_Id(ownerId, pageRequest);

        LocalDateTime now = LocalDateTime.now();

        List<ItemResponseDto> result = itemsPage.getContent().stream().map(item -> {
            Long itemId = item.getId();

            BookingShortDto lastShort = null;
            BookingShortDto nextShort = null;

            Optional<Booking> lastOpt = bookingRepo.findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(
                    itemId, BookingStatus.APPROVED, now);
            Optional<Booking> nextOpt = bookingRepo.findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(
                    itemId, BookingStatus.APPROVED, now);

            if (lastOpt.isPresent()) {
                Booking last = lastOpt.get();
                Long lastBookerId = last.getBooker() != null ? last.getBooker().getId() : null;
                lastShort = new BookingShortDto(last.getId(), lastBookerId);
            }
            if (nextOpt.isPresent()) {
                Booking next = nextOpt.get();
                Long nextBookerId = next.getBooker() != null ? next.getBooker().getId() : null;
                nextShort = new BookingShortDto(next.getId(), nextBookerId);
            }

            List<Comment> comments = commentRepo.findByItem_IdOrderByCreatedDesc(itemId);
            List<CommentDto> commentDtos = comments.stream().map(commentMapper::toDto).collect(Collectors.toList());

            return itemMapper.toResponseDto(item, lastShort, nextShort, commentDtos);
        }).collect(Collectors.toList());

        return result;
    }

    @Override
    public List<ItemDto> search(String text, int from, int size) {
        if (text == null || text.isBlank()) return List.of();
        if (from < 0) from = 0;
        if (size <= 0) throw new BadRequestException("size must be positive");
        int page = from / size;
        List<Item> found = itemRepo.search(text, PageRequest.of(page, size));
        return found.stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long authorId, Long itemId, CommentDto dto) {
        User author = userRepo.findById(authorId)
                .orElseThrow(() -> new NotFoundException("User not found: " + authorId));
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepo.findByBooker_IdAndItem_IdAndStatusAndEndBefore(authorId, itemId,
                BookingStatus.APPROVED, now);

        if (bookings == null || bookings.isEmpty()) {
            throw new BadRequestException("User hasn't completed an approved booking for this item");
        }

        Comment comment = commentMapper.toModel(dto);
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(now);

        Comment saved = commentRepo.save(comment);
        return commentMapper.toDto(saved);
    }

    @Override
    public List<ItemDto> getByRequestId(Long requestId) {
        List<Item> items = itemRepo.findByRequestId(requestId);
        return items.stream()
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }
}

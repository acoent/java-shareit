package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.InMemoryCommentRepository;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final InMemoryItemRepository itemRepo;
    private final InMemoryUserRepository userRepo;
    private final InMemoryCommentRepository commentRepo;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDto create(Long ownerId, ItemDto dto) {
        if (!userRepo.existsById(ownerId)) throw new NotFoundException("User not found: " + ownerId);
        if (dto == null) throw new BadRequestException("Item data required");

        Item item = itemMapper.toModel(dto);
        item.setOwnerId(ownerId);

        item = itemRepo.save(item);
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto update(Long ownerId, Long itemId, ItemDto dto) {
        Item item = itemRepo.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
        if (!item.getOwnerId().equals(ownerId)) throw new NotFoundException("Item not found for owner: " + itemId);

        if (dto.getName() != null) item.setName(dto.getName());
        if (dto.getDescription() != null) item.setDescription(dto.getDescription());
        if (dto.getAvailable() != null) item.setAvailable(dto.getAvailable());

        itemRepo.save(item);
        return itemMapper.toDto(item);
    }

    @Override
    public ItemDto getById(Long requesterId, Long itemId) {
        return itemRepo.findById(itemId)
                .map(itemMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
    }

    @Override
    public List<ItemDto> getByOwner(Long ownerId, int from, int size) {
        validatePage(from, size);
        return itemRepo.findByOwnerId(ownerId).stream()
                .skip(from).limit(size)
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text, int from, int size) {
        validatePage(from, size);
        return itemRepo.searchAvailableByText(text).stream()
                .skip(from).limit(size)
                .map(itemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long authorId, Long itemId, CommentDto dto) {
        if (!userRepo.existsById(authorId)) throw new NotFoundException("User not found: " + authorId);
        itemRepo.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found: " + itemId));

        if (dto == null || dto.getText() == null || dto.getText().isBlank())
            throw new BadRequestException("Comment text required");

        Comment comment = commentMapper.toModel(dto);
        comment.setAuthorId(authorId);
        comment.setItemId(itemId);

        comment = commentRepo.save(comment);
        return commentMapper.toDto(comment);
    }

    private void validatePage(int from, int size) {
        if (from < 0) throw new BadRequestException("from must be >= 0");
        if (size <= 0) throw new BadRequestException("size must be > 0");
    }
}

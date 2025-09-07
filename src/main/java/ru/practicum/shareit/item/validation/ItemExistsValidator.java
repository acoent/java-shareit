package ru.practicum.shareit.item.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;

@Component
public class ItemExistsValidator implements ConstraintValidator<ItemExists, Long> {

    private final InMemoryItemRepository itemRepo;

    @Autowired
    public ItemExistsValidator(InMemoryItemRepository itemRepo) {
        this.itemRepo = itemRepo;
    }

    @Override
    public boolean isValid(Long itemId, ConstraintValidatorContext context) {
        if (itemId == null) return false;
        return itemRepo.existsById(itemId);
    }
}

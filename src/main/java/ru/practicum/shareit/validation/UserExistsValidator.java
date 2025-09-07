package ru.practicum.shareit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;

@Component
public class UserExistsValidator implements ConstraintValidator<UserExists, Long> {

    private final InMemoryUserRepository userRepo;

    @Autowired
    public UserExistsValidator(InMemoryUserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public boolean isValid(Long userId, ConstraintValidatorContext context) {
        if (userId == null) return false;
        return userRepo.existsById(userId);
    }
}

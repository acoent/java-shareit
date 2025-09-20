package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    // Paged method for items by owner
    Page<Item> findAllByOwner_Id(Long ownerId, Pageable pageable);

    // Non-paged variant (kept for backward compatibility)
    List<Item> findAllByOwner_Id(Long ownerId);

    @Query("select i from Item i " +
            "where i.available = true and (" +
            " upper(i.name) like upper(concat('%', :text, '%')) " +
            " or upper(i.description) like upper(concat('%', :text, '%')) )")
    List<Item> search(String text, Pageable pageable);

    @Query("select i from Item i " +
            "where i.available = true and (" +
            " upper(i.name) like upper(concat('%', :text, '%')) " +
            " or upper(i.description) like upper(concat('%', :text, '%')) )")
    List<Item> search(String text);
}

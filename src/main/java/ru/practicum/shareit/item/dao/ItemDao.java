package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao extends JpaRepository<Item, Long> {
    List<Item> findItemsByOwner(Long ownerId);

    @Query("SELECT i FROM Item i " +
            "WHERE UPPER(i.name) LIKE UPPER(concat('%', ?1, '%')) " +
            "OR UPPER(i.description) LIKE UPPER(concat('%', ?1, '%')) AND i.available IS true")
    List<Item> findItemsByText(String text);
}
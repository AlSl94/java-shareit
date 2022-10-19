package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto itemDto) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        return patch("/" + userId, itemId, itemDto);
    }

    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        return get("/" + userId, itemId);
    }

    public ResponseEntity<Object> getUserItems(Long userId, Integer from, Integer size) {
        String query = "?from=" + from;
        query = sizeNullValidation(query, size);
        return get(query, userId);
    }

    public ResponseEntity<Object> searchItem(String text, Integer from, Integer size) {
        String query = "/search?text=" + text + "&from=" + from;
        query = sizeNullValidation(query, size);
        return get(query);
    }

    private String sizeNullValidation(String query, Integer size) {
        if (size != null) {
            return query + ("&size=" + size);
        } else return query;
    }
}

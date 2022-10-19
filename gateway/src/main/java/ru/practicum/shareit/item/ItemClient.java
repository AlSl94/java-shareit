package ru.practicum.shareit.item;

import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.client.BaseClient;

public class ItemClient extends BaseClient {

    public ItemClient(RestTemplate rest) {
        super(rest);
    }

}

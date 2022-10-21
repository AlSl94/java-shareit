package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comment.CommentMapper;
import ru.practicum.shareit.comment.CommentService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    @MockBean
    private CommentService commentService;
    ObjectMapper mapper = new ObjectMapper();
    private User owner;
    private User booker;
    private Item item;
    private Booking booking;
    private Comment comment;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        owner = new User(1L, "user1", "user1@mail.ru");
        booker = new User(2L, "user2", "user2@mail.ru");
        item = new Item(1L, "item", "description",
                true, owner.getId(), null);
        comment = new Comment(1L, "Comment1", item, booker, LocalDateTime.now());
        booking = new Booking(1L, LocalDateTime.now().minusDays(5),
                LocalDateTime.now().minusDays(3), BookingStatus.APPROVED, 1L, booker.getId());
    }

    @Test
    void createItemTest() throws Exception {

        ItemDto itemDto = ItemMapper.toItemDto(item);

        when(itemService.create(any(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @Test
    void updateItemTest() throws Exception {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        when(itemService.create(any(), any()))
                .thenReturn(itemDto);

        itemDto.setName("updatedName");

        when(itemService.update(any(), any(), any()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updatedName"));
    }

    @Test
    void getItemTest() throws Exception {
        ItemInfoDto itemDto = ItemMapper.toItemInfoDto(item, null,
                BookingMapper.toBookingDto(booking), new ArrayList<>());
        when(itemService.getItem(any(), any())).thenReturn(itemDto);
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", owner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemDto.getName()));
    }

    @Test
    void getUserItemsTest() throws Exception {
        ItemInfoDto itemDto = ItemMapper.toItemInfoDto(item, null,
                BookingMapper.toBookingDto(booking), new ArrayList<>());
        when(itemService.getUserItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(itemDto));
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", owner.getId())
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));
    }

    @Test
    void searchItemTest() throws Exception {
        ItemDto itemDto = ItemMapper.toItemDto(item);
        when(itemService.search(itemDto.getDescription().substring(0, 4), 0, 20))
                .thenReturn(Collections.singletonList(itemDto));
        mockMvc.perform(get("/items/search")
                        .param("text", itemDto.getDescription().substring(0, 4))
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));
    }

    @Test
    void postCommentTest() throws Exception {
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        when(commentService.postComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", booker.getId())
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(commentDto.getText()));
    }
}
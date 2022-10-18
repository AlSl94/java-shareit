package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService requestService;

    ObjectMapper mapper = new ObjectMapper();
    private User requestor;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        requestor = new User(2L, "user2", "user2@mail.ru");
        request = new ItemRequest(1L, "request1", requestor.getId(), LocalDateTime.now());
    }

    @Test
    void createRequestTest() throws Exception {
        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(request);
        when(requestService.create(any(), any()))
                .thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()))
                .andExpect(jsonPath("$.description").value(requestDto.getDescription()));
    }

    @Test
    void getItemRequestByIdTest() throws Exception {
        ItemRequestDtoWithItems requestDto = ItemRequestMapper.toItemRequestDtoWithItems(request);
        when(requestService.getItemRequestById(any(), any()))
                .thenReturn(requestDto);
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDto.getId()));
    }

    @Test
    void getUserItemRequestsTest() throws Exception {
        ItemRequestDtoWithItems requestDto = ItemRequestMapper.toItemRequestDtoWithItems(request);
        when(requestService.getUserItemRequests(any()))
                .thenReturn(Collections.singletonList(requestDto));
        mockMvc.perform(get("/requests/")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()));
    }

    @Test
    void getItemRequestsTest() throws Exception {
        ItemRequestDtoWithItems requestDto = ItemRequestMapper.toItemRequestDtoWithItems(request);
        when(requestService.getAllItemRequests(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(requestDto));
        mockMvc.perform(get("/requests/all/")
                        .header("X-Sharer-User-Id", 2L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDto.getId()));
    }
}
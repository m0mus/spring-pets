package com.dmitrykornilov.pets.rest.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.dmitrykornilov.pets.mapper.PetMapper;
import com.dmitrykornilov.pets.rest.dto.PetDto;
import com.dmitrykornilov.pets.rest.dto.PetTypeDto;
import com.dmitrykornilov.pets.service.ApplicationTestConfig;
import com.dmitrykornilov.pets.service.PetsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ContextConfiguration(classes = ApplicationTestConfig.class)
@WebAppConfiguration
class PetRestControllerTests {
    @MockBean
    protected PetsService petsService;

    @Autowired
    private PetRestController petRestController;

    @Autowired
    private PetMapper petMapper;

    private MockMvc mockMvc;

    private final ObjectMapper mapper = createObjectMapper();

    private List<PetDto> petDtoList = new ArrayList<>();

    @BeforeEach
    void initPets() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(petRestController).build();

        var dogType = new PetTypeDto(2, "dog");

        petDtoList.clear();
        petDtoList.add(new PetDto(3, 1, "Falco", LocalDate.now(), dogType));
        petDtoList.add(new PetDto(4, 1, "Filimon", LocalDate.now(), dogType));
    }

    @Test
    void testGetPetSuccess() throws Exception {
        given(this.petsService.findPetById(3)).willReturn(petMapper.toPet(petDtoList.get(0)));
        this.mockMvc.perform(get("/api/pets/3")
                .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.name").value("Falco"));
    }

    @Test
    void testGetPetNotFound() throws Exception {
        given(petMapper.toPetDto(this.petsService.findPetById(-1))).willReturn(null);
        this.mockMvc.perform(get("/api/pets/999")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllPetsSuccess() throws Exception {
        var pets = petMapper.toPets(this.petDtoList);
        when(this.petsService.findAllPets()).thenReturn(pets);
        this.mockMvc.perform(get("/api/pets/")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.[0].id").value(3))
            .andExpect(jsonPath("$.[0].name").value("Falco"))
            .andExpect(jsonPath("$.[1].id").value(4))
            .andExpect(jsonPath("$.[1].name").value("Filimon"));
    }

    @Test
    void testGetAllPetsNotFound() throws Exception {
        petDtoList.clear();
        given(this.petsService.findAllPets()).willReturn(petMapper.toPets(petDtoList));
        this.mockMvc.perform(get("/api/pets/")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void testUpdatePetSuccess() throws Exception {
        given(this.petsService.findPetById(3)).willReturn(petMapper.toPet(petDtoList.get(0)));

        var petDto = petDtoList.get(0);
        petDto.setName("Fallius");
        var petAsJson = this.mapper.writeValueAsString(petDto);

        this.mockMvc.perform(put("/api/pets/3")
                .content(petAsJson).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().contentType("application/json"))
            .andExpect(status().isNoContent());

        this.mockMvc.perform(get("/api/pets/3")
                .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.id").value(3))
            .andExpect(jsonPath("$.name").value("Fallius"));

    }

    @Test
    void testUpdatePetError() throws Exception {
        var petDto = petDtoList.get(0);
        petDto.setName(null);

        var petDtoJson = this.mapper.writeValueAsString(petDto);
        this.mockMvc.perform(put("/api/pets/3")
                .content(petDtoJson).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    void testDeletePetSuccess() throws Exception {
        given(this.petsService.findPetById(3)).willReturn(petMapper.toPet(petDtoList.get(0)));
        this.mockMvc.perform(delete("/api/pets/3")
                                     .accept(MediaType.APPLICATION_JSON_VALUE)
                                     .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNoContent());
    }

    @Test
    void testDeletePetError() throws Exception {
        this.mockMvc.perform(delete("/api/pets/999")
                                     .accept(MediaType.APPLICATION_JSON_VALUE)
                                     .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    void testAddPetSuccess() throws Exception {
        var petDto = petDtoList.get(0);
        var petDtoJson = this.mapper.writeValueAsString(petDto);

        given(this.petsService.findPetById(3)).willReturn(petMapper.toPet(petDto));

        this.mockMvc.perform(post("/api/pets")
                .content(petDtoJson).accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk());
    }

    @Test
    void testAddPetError() throws Exception {
        this.mockMvc.perform(post("/api/pets")
                .content("").accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }

    private static ObjectMapper createObjectMapper() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}

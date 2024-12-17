package com.dmitrykornilov.pets.rest.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dmitrykornilov.pets.mapper.OwnerMapper;
import com.dmitrykornilov.pets.mapper.PetMapper;
import com.dmitrykornilov.pets.rest.advice.ExceptionControllerAdvice;
import com.dmitrykornilov.pets.rest.dto.OwnerDto;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;
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
class OwnerRestControllerTests {
    @Autowired
    private OwnerRestController ownerRestController;

    @Autowired
    private OwnerMapper ownerMapper;

    @Autowired
    private PetMapper petMapper;

    @MockBean
    private PetsService petsService;

    private MockMvc mockMvc;

    private final ObjectMapper mapper = createObjectMapper();

    private final List<OwnerDto> owners = new ArrayList<>();

    private final List<PetDto> pets = new ArrayList<>();

    @BeforeEach
    void initOwners() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(ownerRestController)
                .setControllerAdvice(new ExceptionControllerAdvice())
                .build();

        owners.clear();
        owners.add(new OwnerDto(1, "John", "Doe", "123 Camden High Street", "London ", "123456", new ArrayList<>()));
        owners.add(new OwnerDto(2, "Jane", "Harris", "45A Oxford Street", "Birmingham", "223322", Collections.emptyList()));
        owners.add(new OwnerDto(3, "Tim", "Fisher", "89 Tower Bridge Road", "Manchester", "789789", Collections.emptyList()));
        owners.add(new OwnerDto(4, "Joe", "Harris", "22 Kensington Gardens Square", "Liverpool", "222333",
                                Collections.emptyList()));

        var dogType = new PetTypeDto(2, "dog");

        pets.clear();
        pets.add(new PetDto(3, 1, "Falco", LocalDate.now(), dogType));
        pets.add(new PetDto(4, 1, "Filimon", LocalDate.now(), dogType));

        owners.get(0).getPets().add(pets.get(0));
        owners.get(0).getPets().add(pets.get(1));
    }

    @Test
    void testGetOwnerSuccess() throws Exception {
        given(this.petsService.findOwnerById(1)).willReturn(ownerMapper.toOwner(owners.get(0)));
        this.mockMvc.perform(get("/api/owners/1").accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void testGetOwnerNotFound() throws Exception {
        given(this.petsService.findOwnerById(2)).willReturn(null);
        this.mockMvc.perform(get("/api/owners/2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetOwnersByLastNameSuccess() throws Exception {
        owners.remove(0);
        owners.remove(1);
        given(this.petsService.findOwnerByLastName("Harris")).willReturn(ownerMapper.toOwners(owners));
        this.mockMvc.perform(get("/api/owners?lastName=Harris").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.[0].id").value(2))
                .andExpect(jsonPath("$.[0].firstName").value("Jane"))
                .andExpect(jsonPath("$.[1].id").value(4))
                .andExpect(jsonPath("$.[1].firstName").value("Joe"));
    }

    @Test
    void testGetOwnersListNotFound() throws Exception {
        given(this.petsService.findOwnerByLastName("0")).willReturn(Collections.emptyList());
        this.mockMvc.perform(get("/api/owners/?lastName=0").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllOwnersSuccess() throws Exception {
        given(this.petsService.findAllOwners()).willReturn(ownerMapper.toOwners(owners));
        this.mockMvc.perform(get("/api/owners/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].firstName").value("John"))
                .andExpect(jsonPath("$.[1].id").value(2))
                .andExpect(jsonPath("$.[1].firstName").value("Jane"));
    }

    @Test
    void testGetAllOwnersNotFound() throws Exception {
        given(this.petsService.findAllOwners()).willReturn(Collections.emptyList());
        this.mockMvc.perform(get("/api/owners/").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateOwnerSuccess() throws Exception {
        var ownerDto = owners.get(0);
        ownerDto.setId(null);

        var ownerDtoJson = this.mapper.writeValueAsString(ownerDto);
        this.mockMvc.perform(post("/api/owners/")
                                     .content(ownerDtoJson).accept(MediaType.APPLICATION_JSON_VALUE)
                                     .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreateOwnerError() throws Exception {
        var ownerDto = owners.get(0);
        ownerDto.setId(null);
        ownerDto.setFirstName(null);

        var ownerDtoJson = this.mapper.writeValueAsString(ownerDto);
        this.mockMvc.perform(post("/api/owners/")
                                     .content(ownerDtoJson).accept(MediaType.APPLICATION_JSON_VALUE)
                                     .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateOwnerSuccess() throws Exception {
        given(this.petsService.findOwnerById(1)).willReturn(ownerMapper.toOwner(owners.get(0)));

        var ownerId = owners.get(0).getId();
        var updatedOwnerDto = new OwnerDto();
        updatedOwnerDto.setId(ownerId);
        updatedOwnerDto.setFirstName("Johnny");
        updatedOwnerDto.setLastName("Doe");
        updatedOwnerDto.setAddress("123 Camden High Street");
        updatedOwnerDto.setCity("London ");
        updatedOwnerDto.setTelephone("123456");

        var updatedOwnerDtoJson = this.mapper.writeValueAsString(updatedOwnerDto);
        this.mockMvc.perform(put("/api/owners/" + ownerId)
                                     .content(updatedOwnerDtoJson).accept(MediaType.APPLICATION_JSON_VALUE)
                                     .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isNoContent());

        this.mockMvc.perform(get("/api/owners/" + ownerId)
                                     .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(ownerId))
                .andExpect(jsonPath("$.firstName").value("Johnny"));
    }

    @Test
    void testUpdateOwnerSuccessNoBodyId() throws Exception {
        given(this.petsService.findOwnerById(1)).willReturn(ownerMapper.toOwner(owners.get(0)));

        var ownerId = owners.get(0).getId();
        var updatedOwnerDto = new OwnerDto();
        updatedOwnerDto.setFirstName("Johnny");
        updatedOwnerDto.setLastName("Doe");
        updatedOwnerDto.setAddress("123 Camden High Street");
        updatedOwnerDto.setCity("London ");
        updatedOwnerDto.setTelephone("123456");

        var updatedOwnerDtoJson = this.mapper.writeValueAsString(updatedOwnerDto);
        this.mockMvc.perform(put("/api/owners/" + ownerId)
                                     .content(updatedOwnerDtoJson).accept(MediaType.APPLICATION_JSON_VALUE)
                                     .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType("application/json"))
                .andExpect(status().isNoContent());

        this.mockMvc.perform(get("/api/owners/" + ownerId)
                                     .accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").value(ownerId))
                .andExpect(jsonPath("$.firstName").value("Johnny"));

    }

    @Test
    void testUpdateOwnerError() throws Exception {
        var ownerDto = owners.get(0);
        ownerDto.setFirstName("");

        var ownerDtoJson = this.mapper.writeValueAsString(ownerDto);
        this.mockMvc.perform(put("/api/owners/1")
                                     .content(ownerDtoJson).accept(MediaType.APPLICATION_JSON_VALUE)
                                     .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteOwnerSuccess() throws Exception {
        given(this.petsService.findOwnerById(1)).willReturn(ownerMapper.toOwner(owners.get(0)));
        this.mockMvc.perform(delete("/api/owners/1")
                                     .accept(MediaType.APPLICATION_JSON_VALUE)
                                     .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteOwnerError() throws Exception {
        this.mockMvc.perform(delete("/api/owners/999")
                                     .accept(MediaType.APPLICATION_JSON_VALUE)
                                     .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreatePetSuccess() throws Exception {
        var petDto = pets.get(0);
        petDto.setId(999);

        var petDtoAsJson = this.mapper.writeValueAsString(petDto);
        this.mockMvc.perform(post("/api/owners/1/pets/")
                                     .content(petDtoAsJson).accept(MediaType.APPLICATION_JSON_VALUE)
                                     .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
    }

    @Test
    void testCreatePetError() throws Exception {
        var petDto = pets.get(0);
        petDto.setId(null);
        petDto.setName(null);

        var newPetAsJSON = this.mapper.writeValueAsString(petDto);
        this.mockMvc.perform(post("/api/owners/1/pets/")
                                     .content(newPetAsJSON).accept(MediaType.APPLICATION_JSON_VALUE)
                                     .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testGetOwnerPetSuccess() throws Exception {
        owners.remove(0);
        owners.remove(1);
        given(this.petsService.findAllOwners()).willReturn(ownerMapper.toOwners(owners));

        var owner = ownerMapper.toOwner(owners.get(0));
        given(this.petsService.findOwnerById(2)).willReturn(owner);

        var pet = petMapper.toPet(pets.get(0));
        pet.setOwner(owner);
        given(this.petsService.findPetById(1)).willReturn(pet);

        this.mockMvc.perform(get("/api/owners/2/pets/1")
                                     .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    void testGetOwnersPetsNotFound() throws Exception {
        given(this.petsService.findAllOwners()).willReturn(Collections.emptyList());
        this.mockMvc.perform(get("/api/owners/1/pets/1")
                                     .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private static ObjectMapper createObjectMapper() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}

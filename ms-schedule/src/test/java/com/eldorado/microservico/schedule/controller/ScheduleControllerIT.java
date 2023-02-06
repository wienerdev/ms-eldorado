package com.eldorado.microservico.schedule.controller;

import com.eldorado.microservico.schedule.domain.repository.ScheduleRepository;
import com.eldorado.microservico.schedule.dto.WeekEnum;
import com.eldorado.microservico.schedule.mocks.ConstantsMock;
import com.eldorado.microservico.schedule.mocks.WorkScheduleMock;
import com.eldorado.microservico.schedule.testcontainer.MongoDbContainer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
@ContextConfiguration(initializers = ScheduleControllerIT.MongoDbInitializer.class)
class ScheduleControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    public ObjectMapper objectMapper;

    @Autowired
    public ModelMapper modelMapper;

    private static MongoDbContainer mongoDbContainer;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeAll
    public static void startContainerMongo() {
        mongoDbContainer = new MongoDbContainer();
        mongoDbContainer.start();
    }

    @AfterEach
    public void clearMongoDb() {
        mongoTemplate.dropCollection("user");
    }

    @Test
    @SneakyThrows
    void createUserAndPersistResult() {
        List<String> workOfTimes = new ArrayList<>();
        workOfTimes.add("08:00");
        workOfTimes.add("10:00");
        workOfTimes.add("12:00");
        var scheduleDto = WorkScheduleMock.createValidWorkScheduleDto(UUID.randomUUID(), WeekEnum.MONDAY, workOfTimes, LocalDate.of(2023, 2, 5));

        log.info(objectMapper.writeValueAsString(scheduleDto));

        var response = mockMvc.perform(post("/schedule/work-schedule")
                        .header("Authorization", ConstantsMock.TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleDto)))
                .andExpect(status().is(201));

        Assertions.assertEquals(1, scheduleRepository.findAll().size());
        Assertions.assertEquals(201, response.andReturn().getResponse().getStatus());
    }

//    @Test
//    @SneakyThrows
//    void getUserLoginWithSuccess() {
//
//        var entity = UserEntity.builder()
//                .name("Matheus Nicolay")
//                .gender('M')
//                .birthDate(LocalDate.of(2001, 02, 07))
//                .document("128823834")
//                .userName("mijwn2@gmail.com")
//                .password("123456")
//                .build();
//
//        userRepository.save(entity);
//
//        var user = UserMock.createValidGetUserLoginDto();
//
//        var response = mockMvc.perform(post("/user/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(user)))
//                .andExpect(status().is(200));
//
//        String responseBody = response.andReturn().getResponse().getContentAsString();
//
//        log.info(responseBody);
//
//        var userResponse = objectMapper.readValue(responseBody, UserDto.class);
//
//        Assertions.assertEquals("mijwn2@gmail.com", userResponse.getUserName());
//        Assertions.assertEquals(200, response.andReturn().getResponse().getStatus());
//    }


    public static class MongoDbInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values = TestPropertyValues.of(
                    String.format("spring.data.mongodb.host=%s", mongoDbContainer.getHost(),
                            String.format("spring.data.mongodb.port=%s", mongoDbContainer.getPort()))
            );
            values.applyTo(configurableApplicationContext);
        }
    }

}
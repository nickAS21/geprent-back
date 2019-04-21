package com.georent.controller;

import com.georent.domain.TestObject;
import com.georent.dto.TestObjectDTO;
import com.georent.service.TestService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

/**
 * This is test controller.
 * When you run server it will listen to requests to endpoint mentioned in
 * {@link RequestMapping} annotation.
 * In this example this endpoint will be available: http://localhost:8080/test-controller
 * {@link GetMapping} shows that marked method will handle GET requests to http://localhost:8080/test-controller/*
 */
@Controller
@RequestMapping("test-controller")
public class TestController {

    private final TestService testService;

    @Autowired
    public TestController(TestService testService) {
        this.testService = testService;
    }

    /**
     * This method handles GET requests to endpoint: http://localhost:8080/test-controller/domain
     *
     * @return object of {@link TestObject} class wrapped with {@link ResponseEntity}
     */
    @GetMapping("/domain")
    public ResponseEntity<TestObject> getTestObject(){

        TestObject object = generateTestObject();
        return ResponseEntity.ok().body(object);

    }

    @GetMapping("/domain/{id}")
    public ResponseEntity<TestObject> getTestObjectById(@PathVariable Long id){
        TestObject testObjectById = testService.findTestObjectById(id);
        return ResponseEntity.ok(testObjectById);
    }

    @PostMapping("/domain")
    public ResponseEntity<TestObjectDTO> saveNewTestObject(@RequestBody TestObjectDTO dto){
        TestObject object = generateTestObject();

        object.setId(dto.getId());
        object.setName(dto.getName());
        TestObject saved = testService.saveTestObject(object);
        TestObjectDTO mapped = mapToDto(saved);
        return ResponseEntity.created(URI.create("/domain/" + mapped.getId())).body(mapped);
    }

    @DeleteMapping("/domain/{id}")
    public ResponseEntity<String> deleteTestObject(@PathVariable Long id){
        testService.deleteById(id);
        return ResponseEntity.ok("Object with ID: " + id + " is deleted.");
    }

    /**
     * This method handles GET requests to endpoint: http://localhost:8080/test-controller/dto
     * @return object of {@link TestObjectDTO} class wrapped with {@link ResponseEntity}
     */
    @GetMapping("/dto")
    public ResponseEntity<TestObjectDTO> getTestObjectDTO(){
        TestObject object = generateTestObject();
        return ResponseEntity.ok().body(mapToDto(object));
    }

    @PostMapping("/dto")
    public ResponseEntity<TestObjectDTO> saveObject(@RequestBody TestObjectDTO object){
        return ResponseEntity.ok(object);
    }

    @DeleteMapping("/dto/{id}")
    public ResponseEntity<String> deleteObject(@PathVariable Long id){
        TestObject object = generateTestObject();
        object.setId(id);
        TestObjectDTO dto = mapToDto(object);
        return ResponseEntity.ok().body("Object with ID: " + dto.getId() + " is deleted.");
    }

    private TestObject generateTestObject() {
        TestObject object = new TestObject();
        object.setId(RandomUtils.nextLong());
        object.setName(RandomStringUtils.randomAlphabetic(10));
        object.setSecretLong(42L);
        object.setSecretString("password");
        return object;
    }

    private TestObjectDTO mapToDto(TestObject object){
        TestObjectDTO dto = new TestObjectDTO();
        dto.setId(object.getId());
        dto.setName(object.getName());
        return dto;
    }

}

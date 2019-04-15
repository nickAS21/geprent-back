package com.georent.controller;

import com.georent.domain.TestObject;
import com.georent.dto.TestObjectDTO;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    /**
     * This method handles GET requests to endpoint: http://localhost:8080/test-controller/dto
     * @return object of {@link TestObjectDTO} class wrapped with {@link ResponseEntity}
     */
    @GetMapping("/dto")
    public ResponseEntity<TestObjectDTO> getTestObjectDTO(){
        TestObject object = generateTestObject();
        return ResponseEntity.ok().body(mapToDto(object));
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

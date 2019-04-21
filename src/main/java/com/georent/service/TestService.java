package com.georent.service;

import com.georent.domain.TestObject;
import com.georent.repository.TestObjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TestService {

    private final TestObjectRepository testObjectRepository;

    @Autowired
    public TestService(TestObjectRepository testObjectRepository) {
        this.testObjectRepository = testObjectRepository;
    }

    public TestObject saveTestObject(final TestObject object) {
        return testObjectRepository.save(object);
    }

    public TestObject findTestObjectById(Long id){
        Optional<TestObject> byId = testObjectRepository.findById(id);
        return byId.orElseThrow(() -> new IllegalArgumentException("No such object with ID: " + id));
    }

    public void deleteById(final Long id) {
        testObjectRepository.deleteById(id);
    }
}

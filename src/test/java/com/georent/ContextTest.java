package com.georent;

import com.georent.domain.TestObject;
import com.georent.repository.TestObjectRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ContextTest {

    @Autowired
    private TestObjectRepository repository;

    @Test
    void contextSuccessfullyStarted() {
        TestObject object = new TestObject();
        object.setId(666L);
        TestObject save = repository.save(object);
        Assertions.assertThat(save.getId()).isEqualTo(666);
    }
}

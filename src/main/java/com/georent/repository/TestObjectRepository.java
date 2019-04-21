package com.georent.repository;

import com.georent.domain.TestObject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestObjectRepository extends JpaRepository<TestObject, Long> {
}

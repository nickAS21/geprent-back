package com.georent.service;

import org.junit.Before;

import javax.persistence.EntityManagerFactory;

import static org.mockito.Mockito.mock;

public class DescriptionSearchServiceTest {


    private DescriptionSearchService descriptionSearchService;
    private EntityManagerFactory entityManagerFactory;
    private LotService lotService;
    private AWSS3Service awss3Service = mock(AWSS3Service.class);


    @Before
    public void setup() {
        descriptionSearchService = new DescriptionSearchService(entityManagerFactory, lotService, awss3Service);
    }

}

package com.georent.service;

import com.georent.domain.Description;
import org.apache.lucene.search.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.hibernate.search.query.dsl.PhraseTermination;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import java.util.List;

@Service
public class DescriptionSearchService {

    private final EntityManagerFactory entityManagerFactory;

    @Autowired
    public DescriptionSearchService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public List<Description> fuzzyLotSearch(String searchTerm) {
        SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
        Session currentSession = sessionFactory.openSession();
        FullTextSession fullTextSession = Search.getFullTextSession(currentSession);
        QueryBuilder queryBuilder = fullTextSession.getSearchFactory().buildQueryBuilder().forEntity(Description.class).get();
        Query query = queryBuilder.keyword().fuzzy().withEditDistanceUpTo(2).withPrefixLength(10).onFields("lotName", "lotDescription")
                .matching(searchTerm).createQuery();

        javax.persistence.Query fullTextQuery = fullTextSession.createFullTextQuery(query, Description.class);

        List<Description> resultList = fullTextQuery.getResultList();

        return resultList;
    }

}

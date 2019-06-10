package com.georent.domain;

import lombok.Data;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Description {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @MapsId
    private Lot lot;

    @CollectionTable(name="picture_ids")
    private ArrayList<Long> pictureIds = new ArrayList<Long>();

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "lot_description")
    private String lotDescription;
}

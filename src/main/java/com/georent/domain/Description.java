package com.georent.domain;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@Data
@Entity
public class Description {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Lot lot;

    @Column(name = "picture_id")
    private Long pictureId;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "lot_description")
    private String lotDescription;
}

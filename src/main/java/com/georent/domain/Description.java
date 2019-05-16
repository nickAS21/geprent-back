package com.georent.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Description {

    @Id
    private Long id;

    //    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Lot lot;

    @Column(name = "picture_id")
    private Long pictureId;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "lot_description")
    private String lotDescription;
}

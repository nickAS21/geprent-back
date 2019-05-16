package com.georent.domain;

import lombok.Data;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class GeoRentUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;

//    @OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER,mappedBy="clinicalCareTeam", orphanRemoval=true)
//    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
//    private List<Lot> lots;

    /* mappedBy - свойство в ContactTelDetailEntity, связанное с внешнем ключом в этой таблице
cascade - операция обновления должна распространяться на дочерние записи
orphanRemoval - после обновления, записи которых больше нет в наборе должны быть удалены из БД
 */
//    private Set<Lot> lots = new HashSet<Lot>();
//    @OneToMany(mappedBy = "lot_id", cascade = CascadeType.ALL, orphanRemoval = true)
//    public Set<Lot> getlot() {
//        return this.lots;
//    }

}

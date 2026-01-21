package kr.co.koreazinc.temp.model.entity.main;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MenuRoleMapId implements Serializable {

    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "role_cd", length = 15)
    private String roleCd;
}

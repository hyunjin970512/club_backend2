package kr.co.koreazinc.temp.model.entity.main;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu_role_map")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuRoleMap {

    @EmbeddedId
    private MenuRoleMapId id;

    // 🔗 FK: menu_info.menu_id
    @MapsId("menuId")   // 복합키의 menuId와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private MenuInfo menu;

    @Column(name = "use_at", nullable = false, length = 1)
    private String useAt;   // 'Y' / 'N'
}

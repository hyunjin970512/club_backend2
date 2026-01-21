package kr.co.koreazinc.temp.model.entity.main;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "menu_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // bigserial
    @Column(name = "menu_id")
    private Long menuId;

    @Column(name = "menu_name", nullable = false, length = 50)
    private String menuName;

    @Column(name = "menu_path", nullable = false, length = 200)
    private String menuPath;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @Column(name = "use_at", nullable = false, length = 1)
    private String useAt;   // 'Y' / 'N'

    @Column(name = "create_user", length = 20)
    private String createUser;

    @Column(name = "create_date")
    private LocalDateTime createDate;
    
    @OneToMany(mappedBy = "menu", fetch = FetchType.LAZY)
    private List<MenuRoleMap> roleMaps = new ArrayList<>();
}


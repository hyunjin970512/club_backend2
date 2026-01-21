package kr.co.koreazinc.app.model.main;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MenuDto {

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Get {
        private Long menuId;
        private String name;
        private String path;
        private Integer sortOrder;
    }
}

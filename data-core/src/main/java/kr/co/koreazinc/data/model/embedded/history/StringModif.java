package kr.co.koreazinc.data.model.embedded.history;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import kr.co.koreazinc.data.support.embedded.Modif;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Embeddable public class StringModif implements Modif {

    @Column
    private LocalDateTime at;

    @Column
    private String id;

    public StringModif() {
        this(LocalDateTime.now(), null);
    }

    public StringModif(String id) {
        this(LocalDateTime.now(), id);
    }
}
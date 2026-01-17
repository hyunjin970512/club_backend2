package kr.co.koreazinc.data.model.embedded.history;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;

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
@Embeddable public class UUIDModif implements Modif {

    @Column
    private LocalDateTime at;

    @Column
    @JdbcTypeCode(Types.NVARCHAR)
    private UUID id;

    public UUIDModif() {
        this(LocalDateTime.now(), null);
    }

    public UUIDModif(UUID id) {
        this(LocalDateTime.now(), id);
    }
}
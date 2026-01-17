package kr.co.koreazinc.data.listeners;

import java.util.function.Supplier;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import kr.co.koreazinc.data.support.embedded.Modif;
import kr.co.koreazinc.data.support.mapped.History;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HistoryListener {

    private static Supplier<Modif> supplier = (()->null);

    public HistoryListener setModif(Supplier<Modif> supplier) {
        HistoryListener.supplier = supplier;
        return this;
    }

    public Modif getModif() {
        return supplier.get();
    }

    @PrePersist
    public void prePersist(final Object object) {
        if (object instanceof History history) {
            history.created(this.getModif());
        }
    }

    @PreUpdate
    public void preUpdate(final Object object) {
        if (object instanceof History history) {
            history.updated(this.getModif());
        }
    }
}
package kr.co.koreazinc.data.model.listeners;

import java.util.UUID;

import jakarta.persistence.PrePersist;

public class UUIDGenerationListener {

    public static interface Setter {

        public void setId(UUID id);
    }

    @PrePersist
    public void prePersist(final Object object) {
        if (object instanceof Setter) ((Setter) object).setId(UUID.randomUUID());
    }
}
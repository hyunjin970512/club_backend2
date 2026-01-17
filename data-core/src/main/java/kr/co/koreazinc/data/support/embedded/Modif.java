package kr.co.koreazinc.data.support.embedded;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public interface Modif extends Serializable, Comparable<Modif>  {

    public LocalDateTime getAt();

    @Override
    public default int compareTo(Modif o) {
        return Objects.compare(this.getAt(), o.getAt(), LocalDateTime::compareTo);
    }
}
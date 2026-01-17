package kr.co.koreazinc.data.support.mapped;

import java.io.Serializable;

import kr.co.koreazinc.data.support.embedded.Modif;

public interface History extends Serializable {

    public void created(Modif modif);

    public void updated(Modif modif);
}
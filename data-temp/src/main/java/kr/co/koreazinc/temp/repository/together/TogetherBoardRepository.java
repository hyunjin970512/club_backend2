package kr.co.koreazinc.temp.repository.together;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import kr.co.koreazinc.data.repository.AbstractJpaRepository;
import kr.co.koreazinc.temp.model.entity.together.TogetherBoard;

@Repository
public class TogetherBoardRepository extends AbstractJpaRepository<TogetherBoard, Long> {
	public TogetherBoardRepository(@Autowired List<EntityManager> entityManagers) {
		super(TogetherBoard.class, entityManagers);
	}	
}

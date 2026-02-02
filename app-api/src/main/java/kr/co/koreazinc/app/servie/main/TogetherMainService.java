package kr.co.koreazinc.app.servie.main;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.koreazinc.app.model.main.AreaDto;
import kr.co.koreazinc.app.model.main.TogetherBoardListDto;
import kr.co.koreazinc.temp.repository.main.GetTogetherAreaRepository;
import kr.co.koreazinc.temp.repository.main.GetTogetherBoardRepository;
import kr.co.koreazinc.temp.repository.main.GetTogetherTypeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TogetherMainService {
	
    // 투게더 메인 > 사업장 조회
	private final GetTogetherAreaRepository getTogetherAreaRepository;
	
	// 투게더 메인 > 팀투게더 반투게더 조회
	private final GetTogetherTypeRepository getTogetherTypeRepository;
	
	// 투게더 메인 > 투게더 게시글 리스트 조회
	private final GetTogetherBoardRepository getTogetherBoardRepository;

	// 투게더 메인 > 사업장 조회
	public List<AreaDto> getAreas() {
		List<AreaDto> db = getTogetherAreaRepository
			.selectAreas(AreaDto.class)
			.fetch();
			
		List<AreaDto> out = new ArrayList<>();
		out.add(new AreaDto("ALL", "사업장 전체"));
		out.addAll(db);
		return out;
	}
	
	// 투게더 메인 > 팀투게더 반투게더 조회
	public List<AreaDto> getType() {
		List<AreaDto> db = getTogetherTypeRepository
			.selectAreas(AreaDto.class)
			.fetch();
			
		List<AreaDto> out = new ArrayList<>();
		out.add(new AreaDto("ALL", "전체"));
		out.addAll(db);
		return out;
	}
	
	// 투게더 메인 > 투게더 게시글 리스트 조회
	public List<TogetherBoardListDto> getBoardList(String siteType, String type, String q) {
		return getTogetherBoardRepository
			.selectBoardList(TogetherBoardListDto.class, siteType, type, q)
			.orderByLatest()
			.fetch();
	}
    
}



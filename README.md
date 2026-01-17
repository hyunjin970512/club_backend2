# 프로젝트 초기화 방법
    1. 전체적인 요청 흐름 확인하기
        a. controller / app-api > controller
        b. service / app-api > service
        c. repository / data-temp > repository
            - 프로시저 사용법, 함수 사용법 → AccountRepository 참조
        d. entity / data-temp > model > entity
    2. temp 검색 후 변경 가능한 내용 수정 하기
    3. data-temp > src > main > resources > config > custom-domain-temp-local: db 정보 수정

# config 추가 및 기본 설정 변경([예] 서버 포트 변경 )
    - app-api > application-local.yaml 수정

# ※ swagger 주소 : http://localhost:11000/swagger-ui/index.html

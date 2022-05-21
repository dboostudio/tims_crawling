## TIMS 근태 크롤러 사용법

1. root 폴더에서 `npm install` 실행 (node 설치 필수)
2. crawling.js 에 ID, PASSWORD에 본인 TIMS 아이디, 비밀번호, 소속사 값을 입력하고 파일을 저장합니다.
   - 소속사 값 : 0 - Soft, 1 - Data, 2 - A&C
   - TODO : 금요일이라면, 출근시간 입력받고 예상 퇴근시간 출력기능 추가
3. `node crawling.js` 실행

-- 실행 화면 --

<img width="430" alt="image" src="https://user-images.githubusercontent.com/60918109/169638427-97b097e2-d411-4123-97fa-e0bb5f407e20.png">

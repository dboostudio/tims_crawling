## TIMS 근태 크롤러 사용법

1. root 폴더에서 `npm install` 실행 (node 설치 필수)
2. crawling.js 에 TIMS 아이디, 비밀번호, 소속사 값을 입력하고 파일을 저장합니다.
   - 소속사 값 : 0 - Soft, 1 - Data, 2 - A&C
   - TODO : 금요일이라면, 출근시간 입력받고 예상 퇴근시간 출력기능 추가
4. `node crawling.js` 실행

-- 실행 화면 --

<img width="430" alt="image" src="https://user-images.githubusercontent.com/60918109/169638427-97b097e2-d411-4123-97fa-e0bb5f407e20.png">

## Update Note
22.05.31 - 반차, 연차시 근무시간 각각 4, 8시간 제외하는 로직 추가
22.06.24 - 콘솔 출력 스타일 변경
22.07.01 - [debug] 점심시간 미반영 해결

## TODO
[ ] 오늘의 출근 태그 시간을 받아오고, 금요일 예상 퇴근시간 출력
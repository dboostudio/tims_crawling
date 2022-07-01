const puppeteer = require('puppeteer');

(async () => {
    var timsID = ""; // 여기에 아이디를 입력하세요
    var timsPassword = ""; // 여기에 패스워드를 입력하세요
    var company = 0; // 여기에 소속값에 해당하는 숫자로 변경하세요. (0-Soft, 1-Data, 2-A&C)

    startCrawling(timsID, timsPassword, company);
})()


async function startCrawling(id, password, company, attendHour, attendMinute) {
    if(id == "" || password == ""){
        console.log("아이디 혹은 패스워드는 공백일 수 없습니다.")
        return;
    }
    if( company != 0 && company != 1 && company != 2){
        console.log("소속값이 이상합니다. 확인 후 다시 프로세스를 실행시켜주세요.")
        return;
    }
    console.log(id+"님의 근무 현황을 집계중입니다. 잠시만 기다려주세요....");

    const browser = await puppeteer.launch();
    const page = await browser.newPage();
    const tims_id = id;
    const tims_pw = password;

    //페이지로 가라
    await page.goto('https://tims.tmax.co.kr/login.html');

    //아이디랑 비밀번호 란에 값을 넣어라
    await page.evaluate((id, pw, com) => {
        document.querySelector('input[id="userId"]').value = id;
        document.querySelector('input[id="passwd"]').value = pw;
        switch (com){
            case 0 :
    	        document.querySelector('input[value="TS"]').click();
                break;
            case 1 :
                document.querySelector('input[value="TD"]').click();
                break;
            case 2 :
                document.querySelector('input[value="TO"]').click();
                break;
        }
    }, tims_id, tims_pw, company);

    //로그인 버튼을 클릭해라
    await Promise.all([
        page.click('img[id="btnLogin"]'),
        page.waitForNavigation({waitUntil: "networkidle2"})
    ])

    // 인사 이미지버튼 클릭
    const topFrame = await page.frames().find(f=>f.name()=="frameTop");
    const admissionButton = await topFrame.$('img[id="Image2"]');
    await admissionButton.click();
    await page.waitForTimeout(500);
    // await page.screenshot({path : '/Users/dboo/studio/gitlab/ToyProject/tims_crawling/images/click_admission.png'});

    const leftFrame = await page.frames().find(f=>f.name()=="frameLeft");
    const button1 = await leftFrame.$('a[id="mstMenuLink13"]');
    await button1.click();
    await page.waitForTimeout(1000);
    // await page.screenshot({path : '/Users/dboo/studio/gitlab/ToyProject/tims_crawling/images/click_attendance_parent.png'});

    const button2 = await leftFrame.$('a[id="subMenuLink135"]');
    await button2.click();
    await page.waitForTimeout(1000);
    // await page.screenshot({path : '/Users/dboo/studio/gitlab/ToyProject/tims_crawling/images/click_attendance_child.png'});

    const bodyFrame = page.frames().find(f=>f.name()=="frameBody");

    /*************** 출퇴근 집계 시작 *****************/
    let commute = [];
    var day = new Date().getDay(); // 0-6, sun-sat
    var week = ["일", "월", "화", "수", "목", "금", "토"]

    let count =1;

    // DAY-1
    const attend1 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(9)', element => {
        return element.innerHTML;
    });
    const leave1 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(12)', element => {
        return element.innerHTML;
    });
    const attendType1 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(15)', element => {
        return element.innerHTML;
    });
    const dayOfTheWeek1 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(8)', element => {
        return element.innerHTML;
    });
    let todayInfo = {
        "요일": dayOfTheWeek1,
        "출근": attend1,
        "퇴근": leave1,
        "근태구분" : attendType1,
    };
    if(day-count > 0){
        commute.unshift(todayInfo);
    }
    count++;

    // DAY-2
    const attend2 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(9)', element => {
        return element.innerHTML;
    });
    const leave2 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(12)', element => {
        return element.innerHTML;
    });
    const attendType2 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(15)', element => {
        return element.innerHTML;
    });
    const dayOfTheWeek2 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(8)', element => {
        return element.innerHTML;
    });
    todayInfo = {
        "요일": dayOfTheWeek2,
        "출근": attend2,
        "퇴근": leave2,
        "근태구분" : attendType2,
    };
    if(day-count > 0){
        commute.unshift(todayInfo);
    }
    count++;

    // DAY-3
    const attend3 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(9)', element => {
        return element.innerHTML;
    });
    const leave3 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(12)', element => {
        return element.innerHTML;
    });
    const attendType3 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(15)', element => {
        return element.innerHTML;
    });
    const dayOfTheWeek3 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(8)', element => {
        return element.innerHTML;
    });
    todayInfo = {
        "요일": dayOfTheWeek3,
        "출근": attend3,
        "퇴근": leave3,
        "근태구분" : attendType3,
    };
    if(day-count > 0){
        commute.unshift(todayInfo);
    }
    count++;

    // DAY-4
    const attend4 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(9)', element => {
        return element.innerHTML;
    });
    const leave4 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(12)', element => {
        return element.innerHTML;
    });
    const attendType4 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(15)', element => {
        return element.innerHTML;
    });
    const dayOfTheWeek4 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(8)', element => {
        return element.innerHTML;
    });
    todayInfo = {
        "요일": dayOfTheWeek4,
        "출근": attend4,
        "퇴근": leave4,
        "근태구분" : attendType4,
    };
    if(day-count > 0){
        commute.unshift(todayInfo);
    }
    count++;

    // DAY-5
    const attend5 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(9)', element => {
        return element.innerHTML;
    });
    const leave5 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(12)', element => {
        return element.innerHTML;
    });
    const attendType5 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(15)', element => {
        return element.innerHTML;
    });
    const dayOfTheWeek5 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(8)', element => {
        return element.innerHTML;
    });
    todayInfo = {
        "요일": dayOfTheWeek5,
        "출근": attend5,
        "퇴근": leave5,
        "근태구분" : attendType5,
    };
    if(day-count > 0){
        commute.unshift(todayInfo);
    }
    count++;

    // DAY-6
    const attend6 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(9)', element => {
        return element.innerHTML;
    });
    const leave6 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(12)', element => {
        return element.innerHTML;
    });
    const attendType6 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(15)', element => {
        return element.innerHTML;
    });
    const dayOfTheWeek6 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(8)', element => {
        return element.innerHTML;
    });
    todayInfo = {
        "요일": dayOfTheWeek6,
        "출근": attend6,
        "퇴근": leave6,
        "근태구분" : attendType6,
    };
    if(day-count > 0){
        commute.unshift(todayInfo);
    }
    count++;

    console.log("\n----------------------현재 근무 현황----------------------\n")

    let workTime = 0;
    let remainTime = 40*60;
    let desc = "";
    commute.forEach(function (work) {
        let attendHour = work['출근'].split(":")[0];
        let attendMin = work['출근'].split(":")[1];
        let leavHour = work['퇴근'].split(":")[0];
        let leaveMin = work['퇴근'].split(":")[1];

        var todayWork = (parseInt(leavHour) - parseInt(attendHour)) * 60 + parseInt(leaveMin) - parseInt(attendMin);

        // 오전 반차가 아니라면, 점심시간을 제외한다.
        if(work['근태구분'] != "반차(오전)"){
            todayWork = todayWork - 60;
        }

        // 반차라면 4시간 제외
        if(work['근태구분'].includes("반차")){
            remainTime = remainTime - 4*60;
        }

        // 휴가라면 8시간 제외 (월차휴가, 연차휴가 등)
        if(work['근태구분'].includes("휴가")){
            remainTime = remainTime - 8*60;
        } else { // 휴가가 아니라면 근무시간을 추가한다.
            workTime = workTime + todayWork;
        }
        work["워크"] = parseInt(todayWork/60) + "시간 " + todayWork%60 + "분";

        desc +=  work["요일"] + " || 출근: " + work["출근"] + ", 퇴근: " + work["퇴근"] + ", 워크타임: " + work["워크"] + ", " + work['근태구분'] + "\n";
    });
    console.log(desc);

    remainTime = remainTime - workTime; // 40시간 * 60분 - 근무시간(분)

    console.log("현재 " + parseInt(workTime/60) + "시간 " + workTime%60 + "분 근무했습니다." )
    console.log("\n----------------------잔여 근무 현황----------------------\n")
    if(remainTime > 0){
        console.log("남은 근무시간은 **" + parseInt(remainTime/60) + "시간 " + remainTime%60 + "분** 입니다. (점심시간 미포함)" )
    } else {
        console.log("축하합니다! 이번 주 근무 40시간을 모두 달성했습니다.")
        console.log("초과한 근무시간은 " + parseInt(-remainTime/60) + "시간 " + -remainTime%60 + "분 입니다." )
    }

    console.log("\n-------------------------------------------------------\n")
    console.log("근무시간 계산이 완료되었습니다. Ctrl + C 를 입력해 프로세스를 종료하세요.");

    await browser.close();
};
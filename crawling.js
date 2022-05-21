const puppeteer = require('puppeteer');

(async () => {
    const reader = require('readline').createInterface({
        input: process.stdin,
        output: process.stdout
    });

    startCrawling("TIMS 아이디", "TIMS 비밀번호");
})()


async function startCrawling(id, password, attendHour, attendMinute) {
    // console.log("Start Crawling with ID : " + id + ", PASSWORD : " + password);
    console.log("근무 현황을 집계중입니다. 잠시만 기다려주세요....");

    const browser = await puppeteer.launch();
    const page = await browser.newPage();
    const tims_id = id;
    const tims_pw = password;

    //페이지로 가라
    await page.goto('https://tims.tmax.co.kr/login.html');

    //아이디랑 비밀번호 란에 값을 넣어라
    await page.evaluate((id, pw) => {
    	document.querySelector('input[value="TO"]').click();
        document.querySelector('input[id="userId"]').value = id;
        document.querySelector('input[id="passwd"]').value = pw;
    }, tims_id, tims_pw);

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
    let todayInfo = {
        "요일": week[day-count],
        "출근": attend1,
        "퇴근": leave1
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
    todayInfo = {
        "요일": week[day-count],
        "출근": attend2,
        "퇴근": leave2
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
    todayInfo = {
        "요일": week[day-count],
        "출근": attend3,
        "퇴근": leave3
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
    todayInfo = {
        "요일": week[day-count],
        "출근": attend4,
        "퇴근": leave4
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
    todayInfo = {
        "요일": week[day-count],
        "출근": attend5,
        "퇴근": leave5
    };
    if(day-count > 0){
        commute.unshift(todayInfo);
    }
    count++;


    // DAY-7
    const attend7 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(9)', element => {
        return element.innerHTML;
    });
    const leave7 = await bodyFrame.$eval('table[id="listTable"] > tbody > tr:nth-last-child('+count+') > td:nth-child(12)', element => {
        return element.innerHTML;
    });
    todayInfo = {
        "요일": week[day-count],
        "출근": attend7,
        "퇴근": leave7
    };
    if(day-count > 0){
        commute.unshift(todayInfo);
    }
    count++;

    console.log("----------------------현재 근무 현황----------------------")
    console.log(commute);

    let workTime = 0;
    commute.forEach(function (work) {
        let attendHour = work['출근'].split(":")[0];
        let attendMin = work['출근'].split(":")[1];
        let leavHour = work['퇴근'].split(":")[0];
        let leaveMin = work['퇴근'].split(":")[1];

        var todayWork = (parseInt(leavHour) - parseInt(attendHour)) * 60 + parseInt(leaveMin) - parseInt(attendMin);
        workTime = workTime + todayWork;
    });

    let remainTime = 40*60 - workTime; // 40시간 * 60분 - 근무시간(분)

    console.log("현재 " + parseInt(workTime/60) + "시간 " + workTime%60 + "분 근무했습니다." )
    console.log("")
    console.log("----------------------잔여 근무 현황----------------------")
    if(remainTime >0){
        console.log("남은 근무시간은 **" + parseInt(remainTime/60) + "시간 " + remainTime%60 + "분** 입니다." )
    } else {
        console.log("축하합니다! 이번 주 근무 40시간을 모두 달성했습니다.")
        console.log("초과한 근무시간은 " + parseInt(-remainTime/60) + "시간 " + -remainTime%60 + "분 입니다." )
    }
    console.log("")
    console.log("-------------------------------------------------------")
    console.log("근무시간 계산이 완료되었습니다.");
    console.log("Ctrl + C 를 입력해 프로세스를 종료하세요.");


    await browser.close();
};
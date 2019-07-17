var calUtil = {
  //当前日历显示的年份
  showYear: 2018,
  //当前日历显示的月份
  showMonth: 6,
  days: [],
  // showYear:2019,
  // //当前日历显示的月份
  // showMonth:1,
  //当前日历显示的天数
  showDays: 10,
  eventName: "load",
  //初始化日历
  init: function (startTime, endTime, signList, sy, bqsignList, s) {
    if (typeof (s) == 'undefined') {
    } else {
      signList.splice('', '', s);
    }
    calUtil.bqsignList = bqsignList;
    calUtil.sy = sy;
    calUtil.draw(startTime, endTime, signList);
    calUtil.bindEnvent(signList);
  },
  draw: function (startTime, endTime, signList) {
    if (signList.length > 25) {
      //alert(21);
      // $("#sign_note").empty();
      // $("#sign_note").html('<button class="sign_contener" type="button"><i class="fa fa-calendar-check-o" aria-hidden="true"></i>&nbsp;已达标，获取1次抽奖</button>');
    }

    var str = calUtil.drawCal(startTime, endTime, signList);
    $("#calendar").html(str);
  },
  //绑定事件
  bindEnvent: function (signList) {

  },
  getDaysInmonth: function (iMonth, iYear) {
    var dPrevDate = new Date(iYear, iMonth, 0);
    return dPrevDate.getDate();
  },
  bulidCal: function (startTime, endTime) {
    var aMonth = new Array();
    aMonth[0] = new Array(7);
    aMonth[1] = new Array(7);
    aMonth[2] = new Array(7);
    aMonth[3] = new Array(7);
    aMonth[4] = new Array(7);
    aMonth[5] = new Array(7);
    aMonth[6] = new Array(7);
    var data = startTime.replace(/-/g, "/");
    var date = endTime.replace(/-/g, "/");
    //重新修改
    var first_month = [], second_month = [], days;

    var sta_date = new Date(data);
    var end_date = new Date(date);
    var sta_year = sta_date.getFullYear();
    var sta_month = sta_date.getMonth() + 1;
    var end_month = end_date.getMonth() + 1;
    var sta_day = sta_date.getDate();
    var end_day = end_date.getDate();
    var start_endday = calUtil.getDaysInmonth(sta_month, sta_year);
    if (sta_month == end_month) {

      var xcday = Math.floor(Math.abs(end_date - sta_date) / (24 * 3600 * 1000)) + 1;
      if(xcday == 10){
        for (var i = sta_day; i <= end_day; i++) {
          first_month.push(i);
        }
      }else{
        for (var i = sta_day; i <= start_endday; i++) {
          first_month.push(i);
        }
      }
      
      calUtil.days = first_month;
    } else {
      for (var i = sta_day; i <= start_endday; i++) {
        first_month.push(i)
      }
      for (var i = 1; i <= end_day; i++) {
        second_month.push(i);
      }
      calUtil.days = first_month.concat(second_month)
    }
    // var iDayOfFirst =7-sta_day%7;
    var iDayOfFirst = sta_date.getDay();

    var iVarDate = 0;
    var d, w;
    aMonth[0][0] = "周日";
    aMonth[0][1] = "周一";
    aMonth[0][2] = "周二";
    aMonth[0][3] = "周三";
    aMonth[0][4] = "周四";
    aMonth[0][5] = "周五";
    aMonth[0][6] = "周六";
    for (d = iDayOfFirst; d < 7; d++) {
      aMonth[1][d] = calUtil.days[iVarDate];
      iVarDate++;
    }
    for (w = 2; w < 7; w++) {
      for (d = 0; d < 7; d++) {
        if (iVarDate <= calUtil.days.length) {
          aMonth[w][d] = calUtil.days[iVarDate];
          iVarDate++;
        }
      }
    }
    return aMonth;
  },
  ifHasSigned: function (signList, day) {
    function findall(a, x) {
      for (var i = 0; i < a.length; i++) {
        if (a[i] == x) {
          return i;
        }

      }
    }
    function findElem(arrayToSearch, attr, val) {
      for (var i = 0; i < arrayToSearch.length; i++) {
        if (arrayToSearch[i][attr] == val) {
          return i;
        }
      }
      return -1;
    }
    var signed = false;
    for (var i = 0; i < signList.length; i++) {
      if (signList[i].signDay == day && signList[i].signDay == new Date().getDate()) {
        signed = "yiqiandao_yy";
        break;
        //new Date().getDate()
      } else if (signList[i].signDay == day && signList[i].signDay != new Date().getDate()) {
        if (calUtil.bqsignList.indexOf(signList[i].signDay) != '-1') {
          signed = "yiqiandao_bq";
          break;
        } else {
          signed = "yiqiandao";
          break;
        }
      } else if ((calUtil.sy && findall(calUtil.days, day) >= findall(calUtil.days, day)) || (signList[i] != day && findall(calUtil.days, new Date().getDate()) > findall(calUtil.days, day))) {
        // if(findall(calUtil.days,new Date().getDate()) == findall(calUtil.days,day))
        signed = "weiqiandao";
      } else if (signList[i] != day && findall(calUtil.days, new Date().getDate()) == findall(calUtil.days, day)) {
        if (findElem(signList, "sin", new Date().getDate()) == -1) {
          signed = "weiqiandao_y";
        } else {
          signed = "yiqiandao";
        }
      }
    }
    return signed;
  },
  drawCal: function (startTime, endTime, signList) {
    var myMonth = calUtil.bulidCal(startTime, endTime);
    var htmls = new Array();
    htmls.push("<div class='sign_main' id='sign_layer'>");
    htmls.push("<div class='sign_equal' id='sign_cal'>");
    htmls.push("<div class='sign_row'>");
    htmls.push("<div class='th_1 bold'>" + myMonth[0][0] + "</div>");
    htmls.push("<div class='th_2 bold'>" + myMonth[0][1] + "</div>");
    htmls.push("<div class='th_3 bold'>" + myMonth[0][2] + "</div>");
    htmls.push("<div class='th_4 bold'>" + myMonth[0][3] + "</div>");
    htmls.push("<div class='th_5 bold'>" + myMonth[0][4] + "</div>");
    htmls.push("<div class='th_6 bold'>" + myMonth[0][5] + "</div>");
    htmls.push("<div class='th_7 bold'>" + myMonth[0][6] + "</div>");
    htmls.push("</div>");
    var d, w, w1;
    var data = startTime.replace(/-/g, "/");
    var sta_date = new Date(data);
    if (sta_date.getDay() >= 5) {
      w1 = 6
    } else {
      w1 = 5;
    }
    for (w = 1; w <= w1; w++) {

      if (w == 1) {
        htmls.push("<div class='sign_row1 sign_row'>")
      } else if (w == 6) {
        htmls.push("<div class='sign_row6 sign_row'>");
      } else if (w == 5) {
        htmls.push("<div class='sign_row5 sign_row'>");
      } else if (w == 3) {
        htmls.push("<div class='sign_row3 sign_row'>");
      } else if (w == 4) {
        htmls.push("<div class='sign_row4 sign_row'>");
      } else if (w == 2) {
        htmls.push("<div class='sign_row2 sign_row'>");
      } else {
        htmls.push("<div class='sign_row'>");
      }
      for (d = 0; d < 7; d++) {
        var ifHasSigned = calUtil.ifHasSigned(signList, myMonth[w][d]);
        if (ifHasSigned == "yiqiandao_bq" && typeof (myMonth[w][d]) != 'undefined') {
          htmls.push("<div class='td_" + d + " on'><div class='bq-img'></div>" + (!isNaN(myMonth[w][d]) ? myMonth[w][d] : " ") + "</div>");
        } else if (ifHasSigned == "yiqiandao" && typeof (myMonth[w][d]) != 'undefined') {
          htmls.push("<div class='td_" + d + " on'>" + (!isNaN(myMonth[w][d]) ? myMonth[w][d] : " ") + "</div>");
        } else if (ifHasSigned == "yiqiandao_yy" && typeof (myMonth[w][d]) != 'undefined') {
          htmls.push("<div class='td_" + d + " yiqiandao_yy'>" + (!isNaN(myMonth[w][d]) ? myMonth[w][d] : " ") + "</div>");
        } else if (ifHasSigned == "weiqiandao" && typeof (myMonth[w][d]) != 'undefined') {
          htmls.push("<div class='td_" + d + " calendar_record weiqiandao'>" + (!isNaN(myMonth[w][d]) ? myMonth[w][d] : " ") + "</div>");
        } else if (ifHasSigned == "weiqiandao_y" && typeof (myMonth[w][d]) != 'undefined') {
          htmls.push("<div class='td_" + d + " calendar_record weiqiandao_y'>" + (!isNaN(myMonth[w][d]) ? myMonth[w][d] : " ") + "</div>");
        } else {
          if (!isNaN(myMonth[w][d])) {
            htmls.push("<div class='td_" + d + " calendar_record'>" + (!isNaN(myMonth[w][d]) ? myMonth[w][d] : " ") + "</div>");
          } else {
            htmls.push("<div class='td_" + d + " calendar_record1'>" + (!isNaN(myMonth[w][d]) ? myMonth[w][d] : " ") + "</div>");
          }
        }
      }
      htmls.push("</div>");
    }
    htmls.push("</div>");
    htmls.push("</div>");
    htmls.push("</div>");
    return htmls.join('');
  }
};
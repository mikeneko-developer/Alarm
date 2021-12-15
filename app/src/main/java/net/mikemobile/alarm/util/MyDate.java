package net.mikemobile.alarm.util;

import java.util.Calendar;

public class MyDate {
	public static final int YEAR = Calendar.YEAR;
	public static final int MONTH = Calendar.MONTH;
	public static final int DAY = Calendar.DAY_OF_MONTH;
	public static final int HOUR = Calendar.HOUR_OF_DAY;
	public static final int MINUTE = Calendar.MINUTE;
	public static final int SECOND = Calendar.SECOND;
	public static final int MILLLI = Calendar.MILLISECOND;
	public static final int WEEK = Calendar.WEEK_OF_MONTH;
	
	public static final int SUNDAY = Calendar.SUNDAY;
	public static final int MONDAY = Calendar.MONDAY;
	public static final int TUESDAY = Calendar.TUESDAY;
	public static final int WEDNESDAY = Calendar.WEDNESDAY;
	public static final int THURSDAY = Calendar.THURSDAY;
	public static final int FRIDAY = Calendar.FRIDAY;
	public static final int SATURDAY = Calendar.SATURDAY;

	
	/** ========================================================================================== **/
	//Staticメソッド
	/** ========================================================================================== **/
	public static long getTimeMillis(){
		Calendar calendar = Calendar.getInstance();
		return calendar.getTimeInMillis();
	}

	public static String getDateString(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
		
        String monthS;
        String dayS;
        
        monthS = String.valueOf(month);
        dayS = String.valueOf(day);
        
        
        if(month < 10){
            monthS = "0" +String.valueOf(month);
        }else {
            monthS = String.valueOf(month);
        }
        
        if(day < 10){
            dayS = "0" +String.valueOf(day);
        }else {
            dayS = String.valueOf(day);
        }

        String date = year + "/" + monthS +"/"+ dayS + "";
        
		return date;
	}

	public static final int DATE_YMD = 0;
	public static final int DATE_YM = 1;
	public static final int DATE_MD = 2;
	public static final int DATE_Y = 3;
	public static final int DATE_M = 4;
	public static final int DATE_D = 5;
	public static String getDateString(int type,long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
		
        String monthS;
        String dayS;
        
        monthS = String.valueOf(month);
        dayS = String.valueOf(day);
        
        
        if(month < 10){
            monthS = "0" +String.valueOf(month);
        }else {
            monthS = String.valueOf(month);
        }
        
        if(day < 10){
            dayS = "0" +String.valueOf(day);
        }else {
            dayS = String.valueOf(day);
        }
        
        String date = year + "/" + monthS +"/"+ dayS + "";
        
        if(type == DATE_YM){
        	date = year + "/" + monthS +"";
        }else if(type == DATE_MD){
        	date = monthS +"/"+ dayS + "";
        }else if(type == DATE_Y){
        	date = year + "";
        }else if(type == DATE_M){
        	date = monthS +"";
        }else if(type == DATE_D){
        	date = dayS + "";
        }
		return date;
	}
	public static String getDateJapanString(int type,long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
		
        String monthS;
        String dayS;
        
        monthS = String.valueOf(month);
        dayS = String.valueOf(day);
        
        
        if(month < 10){
            monthS = "0" +String.valueOf(month);
        }else {
            monthS = String.valueOf(month);
        }
        
        if(day < 10){
            dayS = "0" +String.valueOf(day);
        }else {
            dayS = String.valueOf(day);
        }
        
        String date = year + "年" + monthS +"月"+ dayS + "日";
        
        if(type == DATE_YM){
        	date = year + "年" + monthS +"月";
        }else if(type == DATE_MD){
        	date = monthS +"月"+ dayS + "日";
        }else if(type == DATE_Y){
        	date = year + "年";
        }else if(type == DATE_M){
        	date = monthS +"月";
        }else if(type == DATE_D){
        	date = dayS + "日";
        }
		return date;
	}
	
	public static String getTimeString(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String hourS = String.valueOf(hour);
        if(hour < 10)hourS = "0" + String.valueOf(hour);

        String minuteS = String.valueOf(minute);
        if(minute < 10)minuteS = "0" + String.valueOf(minute);

        String secondS = String.valueOf(second);
        if(second < 10)secondS = "0" + String.valueOf(second);
        
        String date = hourS + "時" + minute +"分"+ second + "秒";
        
		return date;
	}

	public static String getDateStringNum(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
		
        String monthS;
        String dayS;
        
        monthS = String.valueOf(month);
        dayS = String.valueOf(day);
        
        
        if(month < 10){
            monthS = "0" +String.valueOf(month);
        }else {
            monthS = String.valueOf(month);
        }
        
        if(day < 10){
            dayS = "0" +String.valueOf(day);
        }else {
            dayS = String.valueOf(day);
        }
        

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String hourS = String.valueOf(hour);
        if(hour < 10)hourS = "0" + String.valueOf(hour);

        String minuteS = String.valueOf(minute);
        if(minute < 10)minuteS = "0" + String.valueOf(minute);

        String secondS = String.valueOf(second);
        if(second < 10)secondS = "0" + String.valueOf(second);
        
        String date = year + "" + monthS +""+ dayS + ""+ hourS + ""+ minuteS + ""+ secondS + "";
        
		return date;
	}

	public static String getDateTimeString(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
		
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        
        String monthS;
        String dayS;
        
        monthS = String.valueOf(month);
        dayS = String.valueOf(day);
        
        
        if(month < 10){
            monthS = "0" +String.valueOf(month);
        }else {
            monthS = String.valueOf(month);
        }
        
        if(day < 10){
            dayS = "0" +String.valueOf(day);
        }else {
            dayS = String.valueOf(day);
        }
        
        String date = year + "年　" + monthS +"月"+ dayS + "日" + String.valueOf(hour) + "時" + String.valueOf(minute) + "分" + String.valueOf(second) + "秒";
        
		return date;
	}
	
	public static String TimeString(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String h = "" + hour;
        if(hour < 10)h = "0" + hour;

        String m = "" + minute;
        if(minute < 10)m = "0" + minute;
        
        String s = "" + second;
        if(second < 10)s = "0" + second;
        
        String date = h + ":" + m + ":" + s + "";
        
		return date;
	}
	
	public static String TimeJapanString(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String h = "" + hour;
        if(hour < 10)h = "0" + hour;

        String m = "" + minute;
        if(minute < 10)m = "0" + minute;
        
        String s = "" + second;
        if(second < 10)s = "0" + second;
        
        String date = h + "時" + m + "分" + s + "秒";
        
		return date;
	}

	public static final int TIME_HMS = 0;
	public static final int TIME_HM = 1;
	public static final int TIME_MS = 2;
	public static final int TIME_H = 3;
	public static final int TIME_M = 4;
	public static final int TIME_S = 5;
	public static String getTimeString(int type,long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String h = "" + hour;
        if(hour < 10)h = "0" + hour;

        String m = "" + minute;
        if(minute < 10)m = "0" + minute;
        
        String s = "" + second;
        if(second < 10)s = "0" + second;
        
        String date = h + ":" + m + ":" + s + "";
        
        if(type == TIME_HM){
        	date = h + ":" + m;
        }else if(type == TIME_MS){
        	date = m + ":" + s + "";
        }else if(type == TIME_H){
        	date = h + "";
        }else if(type == TIME_M){
        	date = m + "";
        }else if(type == TIME_S){
        	date = s + "";
        }
        
		return date;
	}
	
	public static String TimeJapanString(int type,long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String h = "" + hour;
        if(hour < 10)h = "0" + hour;

        String m = "" + minute;
        if(minute < 10)m = "0" + minute;
        
        String s = "" + second;
        if(second < 10)s = "0" + second;
        
        String date = h + "時" + m + "分" + s + "秒";
        
        if(type == TIME_HM){
        	date = h + "時" + m + "分";
        }else if(type == TIME_MS){
        	date = m + "分" + s + "秒";
        }else if(type == TIME_H){
        	date = h + "時";
        }else if(type == TIME_M){
        	date = m + "分";
        }else if(type == TIME_S){
        	date = s + "秒";
        }
        
		return date;
	}


    public static long getjastDate(long date){
    	int year = MyDate.getYear(date);
        int month = MyDate.getMonth(date);
        int day = MyDate.getDay(date);
        int hour = 0;
        int minute = 0;
        int second = 0;
        return MyDate.getTimeMillis(year, month, day, hour, minute, second);
    }
    
	public static int getYear(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		return calendar.get(Calendar.YEAR);
    }

	public static int getYear(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.YEAR);
    }

	public static int getMonth(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		return calendar.get(Calendar.MONTH) + 1;
    }

	public static int getMonth(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MONTH) + 1;
    }

	public static int getDay(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		return calendar.get(Calendar.DAY_OF_MONTH);
    }

	public static int getDay(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DAY_OF_MONTH);
    }
	
	public static int getHour(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		return calendar.get(Calendar.HOUR_OF_DAY);
    }

	public static int getHour(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.HOUR_OF_DAY);
    }

	public static int getMinute(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		return calendar.get(Calendar.MINUTE);
    }

	public static int getMinute(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.MINUTE);
    }

	public static int getSecond(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		return calendar.get(Calendar.SECOND);
    }

	public static int getSecond(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.SECOND);
    }
	
	public static int getWeek(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		return calendar.get(Calendar.DAY_OF_WEEK);
	}
	
	public static int getWeek(){
		Calendar calendar = Calendar.getInstance();
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	public static int getLastDay(int year,int month){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR,year);//年：
		calendar.set(Calendar.MONTH,month - 1);//月：
		calendar.set(Calendar.DATE, 1);//一日にセット
		
	    int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//最終日まであと何日かを取得する
        return max;
	}
	
	public static long getTimeMillis(int year,int month,int day,int hour,int minute){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR,year);//年：
		calendar.set(Calendar.MONTH,month - 1);//月：
		calendar.set(Calendar.DAY_OF_MONTH,day);//日：
		calendar.set(Calendar.HOUR_OF_DAY,hour);//時：
		calendar.set(Calendar.MINUTE,minute);//分：
		calendar.set(Calendar.SECOND,0);//秒：
		calendar.set(Calendar.MILLISECOND, 0);
		//calendar.set(Calendar.DAY_OF_WEEK, 0);
        
        return calendar.getTimeInMillis();
	}
	public static long getTimeMillis(int year,int month,int day,int hour,int minute,int second){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR,year);//年：
		calendar.set(Calendar.MONTH,month - 1);//月：
		calendar.set(Calendar.DAY_OF_MONTH,day);//日：
		calendar.set(Calendar.HOUR_OF_DAY,hour);//時：
		calendar.set(Calendar.MINUTE,minute);//分：
		calendar.set(Calendar.SECOND,second);//秒：
		calendar.set(Calendar.MILLISECOND, 0);
		//calendar.set(Calendar.DAY_OF_WEEK, 0);
        
        return calendar.getTimeInMillis();
	}

	/** ========================================================================================== **/
	//現在の曜日が日曜日から数えて何日めか取得する
	public static int weekCount(int week){
		int cnt = 0;
		switch(week){
        case Calendar.SUNDAY    :cnt = 0;//日
        case Calendar.MONDAY    :cnt = 1;//月
        case Calendar.TUESDAY   :cnt = 2;//火
        case Calendar.WEDNESDAY :cnt = 3;//水
        case Calendar.THURSDAY  :cnt = 4;//木
        case Calendar.FRIDAY    :cnt = 5;//金
        case Calendar.SATURDAY  :cnt = 6;//土
        default:;//不明
		}
		
		return cnt;
	}
	//現在の曜日が日曜日から数えて何日めか取得する
	public static String getWeekJapanString(int week){
		
		switch(week){
        case Calendar.SUNDAY    :return "日";//日
        case Calendar.MONDAY    :return "月";//月
        case Calendar.TUESDAY   :return "火";//火
        case Calendar.WEDNESDAY :return "水";//水
        case Calendar.THURSDAY  :return "木";//木
        case Calendar.FRIDAY    :return "金";//金
        case Calendar.SATURDAY  :return "土";//土
        default:;//不明
		}
		
		return "";
	}
	
	public static String getLongToTime(long time){
		long time_count = time + 1000;
		
		long millisecond = 1;
		long second =  1000 * millisecond;//1秒
		long minute = 60 * second;//1分
		long hour = 60 * minute;//1時間
		long day = 24 * hour;//1日
		
		int d = 0;
		if(time_count > day){
			d = (int) (time_count/ day);
			time_count = time_count%day;
		}

		int h = 0;
		if(time_count > hour){
			h = (int) (time_count/ hour);
			time_count = time_count%hour;
		}

		int m = 0;
		if(time_count > minute){
			m = (int) (time_count/ minute);
			time_count = time_count%minute;
		}

		int s = 0;
		if(time_count > second){
			s = (int) (time_count/ second);
			time_count = time_count%second;
		}
		
		String t_hour = String.valueOf(h);if(h<10)t_hour = "0" + String.valueOf(h);
		String t_minute = String.valueOf(m);if(m<10)t_minute = "0" + String.valueOf(m);
		String t_second = String.valueOf(s);if(s<10)t_second = "0" + String.valueOf(s);
		
		String text = t_hour + ":" + t_minute;
		
		return text;
	}
	
	
	public static int getLongToSecond(long time){
		long time_count = time + 1000;
		
		long millisecond = 1;
		long second =  1000 * millisecond;//1秒
		long minute = 60 * second;//1分
		long hour = 60 * minute;//1時間
		long day = 24 * hour;//1日
		
		long second_time = time/second;
		
		
		return (int)second_time;
	}
	
	
	public static long addDateTime(int key,int value,long date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date);
		
		calendar.add(key, value);
		
		return calendar.getTimeInMillis();
	}
	
	// ========================================================================== //
	//第何週か調べる
	public static int getWeekOfMonth(long date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date);
		
		return calendar.get(Calendar.WEEK_OF_MONTH);
		
	}
	
	//第何週の曜日か
	public static int getDayOfWeek(long date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date);
		
		return calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);
		
	}
	
	// ========================================================================== //
	//ロング型をPHPの日付フォーマットに変更する
	public static String getLongToPHPDateTimeString(long time){
		if(time == 0){
			return "0000-00-00 00:00:00";
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
		
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        
        String s_year = "";
        if(year < 10){s_year = "000";}
        else if(year < 100){s_year = "00";}
        else if(year < 1000){s_year = "0";}

        String s_month = "";
        if(month < 10){s_month = "0";}

        String s_day = "";
        if(day < 10){s_day = "0";}

        String s_hour = "";
        if(hour < 10){s_hour = "0";}

        String s_minute = "";
        if(minute < 10){s_minute = "0";}

        String s_second = "";
        if(second < 10){s_second = "0";}
        
        String text = s_year+year+"-"+s_month+month+"-"+s_day+day+""
        		+ " "+s_hour+hour+":"+s_minute+minute+":"+s_second+second+"";
		return text;
	}

	public static String getLongToPHPDateString(long time){
		if(time == 0){
			return "0000-00-00";
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
		int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
		
        String s_year = "";
        if(year < 10){s_year = "000";}
        else if(year < 100){s_year = "00";}
        else if(year < 1000){s_year = "0";}

        String s_month = "";
        if(month < 10){s_month = "0";}

        String s_day = "";
        if(day < 10){s_day = "0";}

        
        String text = s_year+year+"-"+s_month+month+"-"+s_day+day+"";
		return text;
	}

	public static String getLongToPHPTimeString(long time){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        
        String s_hour = "";
        if(hour < 10){s_hour = "0";}

        String s_minute = "";
        if(minute < 10){s_minute = "0";}

        String s_second = "";
        if(second < 10){s_second = "0";}
        
        String text = s_hour+hour+":"+s_minute+minute+":"+s_second+second+"";
		return text;
	}
	
	// ========================================================================== //
	//PHPの日付フォーマットをロング型に変更する
	public static long getPHPDateTimeStringToLong(String date_text){
		if(date_text.equals("0000-00-00 00:00:00")){
			return 0;
		}
		
		String[]datetime = date_text.split(" ");

		String[]date = datetime[0].split("-");
		String[]time = datetime[1].split(":");

		int year = Integer.parseInt(date[0]);
		int month = Integer.parseInt(date[1]);
		int day = Integer.parseInt(date[2]);

		int hour = Integer.parseInt(time[0]);
		int minute = Integer.parseInt(time[1]);
		int second = Integer.parseInt(time[2]);
		
		long date_time = MyDate.getTimeMillis(year, month, day, hour, minute, second);
		
		return date_time;
	}
	
	public static long getPHPDateStringToLong(String date_text){
		if(date_text.equals("0000-00-00")){
			return 0;
		}
		
		String[]date = date_text.split("-");
		
		int year = Integer.parseInt(date[0]);
		int month = Integer.parseInt(date[1]);
		int day = Integer.parseInt(date[2]);

		int hour = 0;
		int minute = 0;
		int second = 0;
		
		long date_time = MyDate.getTimeMillis(year, month, day, hour, minute, second);
		
		return date_time;
	}
	public static long getPHPTimeStringToLong(String date_text){
		
		String[]time = date_text.split(":");

		int year = 1700;
		int month = 1;
		int day = 1;

		int hour = Integer.parseInt(time[0]);
		int minute = Integer.parseInt(time[1]);
		int second = Integer.parseInt(time[2]);
		
		long date_time = MyDate.getTimeMillis(year, month, day, hour, minute, second);
		
		return date_time;
	}

	// ========================================================================== //
	//PHPの日付フォーマットでDATETIME から　DATEに変換
	public static String getPHPDateTimeStringToDateString(String date_text){
		String[]datetime = date_text.split(" ");
		return datetime[0];
	}

	// ========================================================================== //
	//PHPの日付フォーマットでDATETIME から　DATEに変換
	public static String getPHPDateTimeStringToTimeString(String date_text){
		String[]datetime = date_text.split(" ");
		return datetime[1];
	}


	// ========================================================================== //
	// 指定日、選択された曜日、当日・翌日判定フラグの三つから、次の曜日を特定する


}

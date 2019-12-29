package com.qmkj.niaogebiji.module.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.dialog.ReTestCalendaDialog;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.common.net.base.BaseObserver;
import com.qmkj.niaogebiji.common.net.helper.RetrofitHelper;
import com.qmkj.niaogebiji.common.net.response.HttpResponse;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.MobclickAgentUtils;
import com.qmkj.niaogebiji.common.utils.MobClickEvent.UmengEvent;
import com.qmkj.niaogebiji.common.utils.StringUtil;
import com.qmkj.niaogebiji.common.utils.TimeAppUtils;
import com.qmkj.niaogebiji.module.bean.AppointmentBean;
import com.qmkj.niaogebiji.module.bean.SchoolBean;
import com.qmkj.niaogebiji.module.bean.ShareBean;
import com.socks.library.KLog;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-25
 * 描述:测试失败
 */
public class TestResultFailActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.iv_right)
    ImageView iv_right;

    @BindView(R.id.test_grade)
    TextView test_grade;


    private SchoolBean.SchoolTest mSchoolTest;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_result_fail;
    }

    @Override
    protected void initView() {
        mExecutorService = Executors.newFixedThreadPool(2);
        mSchoolTest = (SchoolBean.SchoolTest) getIntent().getExtras().getSerializable("bean");

        tv_title.setText(mSchoolTest.getTitle());

        if(!TextUtils.isEmpty(mSchoolTest.getTime()) && !TextUtils.isEmpty(mSchoolTest.getQuestion_num())){
            long result = Long.parseLong(mSchoolTest.getTime()) * Long.parseLong(mSchoolTest.getQuestion_num());
            mins  = TimeAppUtils.convertSecToTimeString(result);
            KLog.d("tag",mins);
        }


        //如果已考过的有分数，就用以前的分数
        if(mSchoolTest.getRecord()!= null && mSchoolTest.getRecord().getScore()!= null){
            test_grade.setText(mSchoolTest.getRecord().getScore());
        }else{
            test_grade.setText(mSchoolTest.getMyScore());
        }

        iv_right.setVisibility(View.VISIBLE);
        iv_right.setImageResource(R.mipmap.icon_test_share_black);
    }


    @OnClick({R.id.iv_back,R.id.iv_right,
                R.id.toReTest,R.id.toLook})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.toLook:
                break;
            case R.id.iv_right:
                MobclickAgentUtils.onEvent(UmengEvent.academy_testdetail_score_share_2_0_0);
                showShareDialog();
                break;
            case R.id.toReTest:
                KLog.d("tag","判断是否预约");
                MobclickAgentUtils.onEvent(UmengEvent.academy_testdetail_score_appointment_2_0_0);
                showReTestSubmit();
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
        }
    }


    private void reserveTest() {
        Map<String,String> map = new HashMap<>();
        map.put("test_cate_id",mSchoolTest.getId() + "");
        String result = RetrofitHelper.commonParam(map);
        RetrofitHelper.getApiService().reserveTest(result)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe(new BaseObserver<HttpResponse<AppointmentBean>>() {
                    @Override
                    public void onSuccess(HttpResponse<AppointmentBean> httpResponse) {
                        KLog.d("tag",httpResponse.getReturn_data());

                        // 状态值 0之前没有预约过 1已预约过，但还没到预约时间 2可以参加预约考试
                        AppointmentBean temp = httpResponse.getReturn_data();
                        if(0 == temp.getStatus()){
                            showReTestCalendar();
                        }else if(1 == temp.getStatus()){
                            int time = temp.getDate();
                            ToastUtils.showShort("你在近期参加过该测试。如需重考请在" + TimeUtils.millis2String(time*1000L,"yyyy年MM月dd日") + "再来参加");
                        }else if(2 == temp.getStatus()){
                            UIHelper.toTestDetailActivity(TestResultFailActivity.this,mSchoolTest);
                            finish();
                        }
                    }
                });
    }




    /** --------------------------------- 分享  ---------------------------------*/
    private String mins;
    ShareBean bean = new ShareBean();
    Bitmap bitmap =  null;
    private ExecutorService mExecutorService;

    private void showShareDialog() {
        ShareWithLinkDialog alertDialog = new ShareWithLinkDialog(this).builder();
        alertDialog.setSharelinkView();
        alertDialog.setTitleGone();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnDialogItemClickListener(position -> {
            switch (position){
                case 0:
                    mExecutorService.submit(() -> {
                        bitmap = StringUtil.getBitmap(mSchoolTest.getIcon());
                        mHandler.sendEmptyMessage(0x111);
                    });

                    break;
                case 1:
                    mExecutorService.submit(() -> {
                        bitmap = StringUtil.getBitmap(mSchoolTest.getIcon());
                        mHandler.sendEmptyMessage(0x112);
                    });

                    break;
                case 2:
                    ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                    ToastUtils.showShort("链接复制成功！");
                    StringUtil.copyLink(mSchoolTest.getTitle() + "\n" +  mSchoolTest.getShare_url());

                    break;
                default:
            }
        });
        alertDialog.show();
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            bean.setBitmap(bitmap);
            bean.setImg(mSchoolTest.getIcon());
            bean.setLink(mSchoolTest.getShare_url());
            bean.setTitle("测一测：" + mSchoolTest.getTitle());
            bean.setContent(mins + "看看你能否成为合格的"  + mSchoolTest.getTitle());
            if(msg.what == 0x111){
                bean.setShareType("circle_link");
            }else{
                bean.setShareType("weixin_link");
            }
            StringUtil.shareWxByWeb(TestResultFailActivity.this,bean);

        }
    };


    /** --------------------------------- 重考  ---------------------------------*/
    Uri uri1 = CalendarContract.Events.CONTENT_URI;

    private static String CALANDER_URL = "content://com.android.calendar/calendars";
    private static String CALANDER_EVENT_URL = "content://com.android.calendar/events";
    private static String CALANDER_REMIDER_URL = "content://com.android.calendar/reminders";

    private static String CALENDARS_NAME = "test";
    private static String CALENDARS_ACCOUNT_NAME = "test@gmail.com";
    private static String CALENDARS_ACCOUNT_TYPE = "com.android.exchange";
    private static String CALENDARS_DISPLAY_NAME = "测试账户";
    private static int ONE_HOUR =  60 * 1000;
    private static int TWO_DAT =   48 * 60 * 1000  * 1000 ;

    //获取事件ID
    private long eventId;


    public void showReTestSubmit(){
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(this).builder();
        iosAlertDialog.setPositiveButton("重考", v -> {
            reserveTest();
        }).setNegativeButton("再想想", v -> {
        }).setMsg("要进行重考吗？").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }

    //获取默认值
    public void getDDD(){
        //获取当前时间
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);
        KLog.d("tag",year + "/" + (month+1) + "/" + date + " " +hour + ":" +minute + ":" + second);
    }

    //可以对每个时间域单独修改
    public String getDDD222(){
        //获取当前时间
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE) + 3;
        int hour = 9;
        int minute = 0;
        int second = 0;
        KLog.d("tag",year + "/" + (month+1) + "/" + date + " " +hour + ":" +minute + ":" + second);
        return  year + "/" + (month+1) + "/" + date + " " +hour + ":" +minute + ":" + second;
    }


    //13考试  --- 16号可以考试
    String endTime;
    long endLongTime;
    public void showReTestCalendar(){


        endTime = TimeAppUtils.getOldDate(3);

        endLongTime = TimeUtils.string2Millis(getDDD222(),"yyyy/MM/dd HH:mm:ss");

        getDDD222();

        String result = "最近的可重考时间为"+ endTime +"。请及时参加考试~";

        final ReTestCalendaDialog iosAlertDialog = new ReTestCalendaDialog(this).builder();
        iosAlertDialog.setTitle(result).setPositiveButton("帮我添加到日历", v -> {

            //发请求之前判断权限
            if (hasPermissions(this, permissions)) {
                toNext();
            }else{
                ActivityCompat.requestPermissions(this, permissions, 100);
            }

        }).setMsg("您已预约重考！").setCancelable(false).setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }


    String permissions[] = new String[]{
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_CALENDAR};



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        KLog.d("tag","权限是： " + permissions[0]);
        if(grantResults[0] == PackageManager.PERMISSION_DENIED){
            //用户勾选了不再提示，函数返回false
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                //解释原因，并且引导用户至设置页手动授权
                return;
            }
        }else{
            toNext();
        }
    }


    private void toNext(){
        addCalendarEvent(this,mSchoolTest.getTitle(),mSchoolTest.getDesc(),System.currentTimeMillis(),endLongTime);
    }


    //检查是否已经添加了日历账户，如果没有添加先添加一个日历账户再查询
    private  int checkAndAddCalendarAccount(Context context){
        int oldId = checkCalendarAccount(context);
        if( oldId >= 0 ){
            return oldId;
        }else{
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccount(context);
            } else {
                return -1;
            }
        }
    }

    private  int checkCalendarAccount(Context context) {
        Cursor userCursor = context.getContentResolver().query(Uri.parse(CALANDER_URL),
                null, null, null, null);
        try {
            //查询返回空值
            if (userCursor == null) {
                return -1;
            }
            int count = userCursor.getCount();
            //存在现有账户，取第一个账户的id返回
            if (count > 0) {
                userCursor.moveToFirst();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }


    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);

        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(CALANDER_URL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();

        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }


    //添加日历事件、日程
    public  void addCalendarEvent(Context context,String title, String description, long beginTime,long endTime){
        // 获取日历账户的id
        int calId = checkAndAddCalendarAccount(context);
        if (calId < 0) {
            // 获取账户id失败直接返回，添加日历事件失败
            return;
        }

        ContentValues event = new ContentValues();
        event.put("title", title);
        event.put("description", description);
        // 插入账户的id
        event.put("calendar_id", calId);


        // 如果起始时间为零，使用当前时间
        if (beginTime == 0) {
            Calendar beginCalendar = Calendar.getInstance();
            beginTime = beginCalendar.getTimeInMillis();
        }

        KLog.d("tag","开始时间 " + TimeAppUtils.timeStamp2Date(beginTime,""));

        Calendar mCalendar = Calendar.getInstance();
        //设置开始时间
        mCalendar.setTimeInMillis(beginTime);
        long start = mCalendar.getTime().getTime();
        //设置终止时间
        mCalendar.setTimeInMillis(start);
        long end = mCalendar.getTime().getTime();


        start = endTime;
        end = endTime;


        KLog.d("tag","结束时间 " + TimeAppUtils.timeStamp2Date(end,""));
        KLog.d("tag","结束时间1 " + TimeAppUtils.timeStamp2Date(endLongTime,""));
        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, end);
        //设置有闹钟提醒
        event.put(CalendarContract.Events.HAS_ALARM, 1);
        // 获取默认时区
        TimeZone tz = TimeZone.getDefault();
        //这个是时区，必须有，
        event.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());
        //添加事件
        Uri newEvent = context.getContentResolver().insert(Uri.parse(CALANDER_EVENT_URL), event);
        if (newEvent == null) {
            // 添加日历事件失败直接返回
            Toast.makeText(this, "插入事件失败!!!", Toast.LENGTH_SHORT).show();
            return;
        }
        eventId = ContentUris.parseId(newEvent);

        startCalendarForIntentToView(this,eventId);

        ToastUtils.showShort("添加到日历成功");

        //事件提醒的设定
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, ContentUris.parseId(newEvent));
        // 提前5分钟有提醒(提前0分钟时，值为0
        values.put(CalendarContract.Reminders.MINUTES, 0);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = context.getContentResolver().insert(Uri.parse(CALANDER_REMIDER_URL), values);
        if(uri == null || ContentUris.parseId(uri) == 0) {
            // 添加闹钟提醒失败直接返回
            return;
        }

    }

    // 删除日历事件、日程
    public static void deleteCalendarEvent(Context context,String title){
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(CALANDER_EVENT_URL),
                null, null, null, null);
        try {
            if (eventCursor == null)//查询返回空值
                return;
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title跟需要查询的title一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                    if (!TextUtils.isEmpty(title) && title.equals(eventTitle)) {
                        int id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));//取得id
                        Uri deleteUri = ContentUris.withAppendedId(Uri.parse(CALANDER_EVENT_URL), id);
                        int rows = context.getContentResolver().delete(deleteUri, null, null);
                        if (rows == -1) {
                            //事件删除失败
                            return;
                        }
                    }
                }
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }


    /**
     * 通过Intent启动系统日历来查看指定ID的事件
     *
     * @param eventID 要查看的事件ID
     */
    public static void startCalendarForIntentToView(Context context, long eventID) {
        Uri uri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(uri);
        if (null != intent.resolveActivity(context.getPackageManager())) {
            context.startActivity(intent);
        }
    }

}

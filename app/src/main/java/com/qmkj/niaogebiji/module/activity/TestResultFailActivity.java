package com.qmkj.niaogebiji.module.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.qmkj.niaogebiji.R;
import com.qmkj.niaogebiji.common.base.BaseActivity;
import com.qmkj.niaogebiji.common.dialog.CleanHistoryDialog;
import com.qmkj.niaogebiji.common.dialog.ProfessionAutherDialog;
import com.qmkj.niaogebiji.common.dialog.ReTestCalendaDialog;
import com.qmkj.niaogebiji.common.dialog.ShareWithLinkDialog;
import com.qmkj.niaogebiji.common.helper.UIHelper;
import com.qmkj.niaogebiji.module.bean.WxShareBean;
import com.socks.library.KLog;

import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.OnClick;

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


    @Override
    protected int getLayoutId() {
        return R.layout.activity_test_result_fail;
    }

    @Override
    protected void initView() {

        tv_title.setText("初级ASO测试");
        iv_right.setVisibility(View.VISIBLE);
        iv_right.setImageResource(R.mipmap.icon_test_share_black);
    }



    @OnClick({R.id.iv_back,R.id.iv_right,
                R.id.toReTest,R.id.toLook})
    public void clicks(View view){
        switch (view.getId()){
            case R.id.toLook:
                KLog.d("tag","h5 跳转");
                break;
            case R.id.iv_right:
                showShareDialog();
                break;
            case R.id.toReTest:
                KLog.d("tag","判断是否预约");
                showReTestSubmit();
                break;
            case R.id.iv_back:
                finish();
                break;
            default:
        }
    }


    /** --------------------------------- 分享  ---------------------------------*/

    private void showShareDialog() {
        ShareWithLinkDialog alertDialog = new ShareWithLinkDialog(this).builder();
        alertDialog.setSharelinkView();
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnDialogItemClickListener(position -> {
            switch (position){
                case 0:
                    KLog.d("tag","朋友圈 是张图片");
                    WxShareBean bean = new WxShareBean();
                    shareWxCircleByWeb(bean);
                    break;
                case 1:
                    KLog.d("tag","朋友 是链接");
                    WxShareBean bean2 = new WxShareBean();
                    shareWxByWeb(bean2);
                    break;
                case 2:
                    KLog.d("tag","复制链接");
                    //获取剪贴板管理器：
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 创建普通字符型ClipData
                    ClipData mClipData = ClipData.newPlainText("Label", "http://www.baidu.com");
                    // 将ClipData内容放到系统剪贴板里。
                    cm.setPrimaryClip(mClipData);
                    ToastUtils.setGravity(Gravity.BOTTOM,0, SizeUtils.dp2px(40));
                    ToastUtils.showShort("链接复制成功！");
                    break;
                default:
            }
        });
        alertDialog.show();
    }

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

    //获取事件ID
    private long eventId;


    public void showReTestSubmit(){
        final CleanHistoryDialog iosAlertDialog = new CleanHistoryDialog(this).builder();
        iosAlertDialog.setPositiveButton("重考", v -> {
            showReTestCalendar();
        }).setNegativeButton("再想想", v -> {

        }).setMsg("要进行重考吗？").setCanceledOnTouchOutside(false);
        iosAlertDialog.show();
    }



    public void showReTestCalendar(){
        final ReTestCalendaDialog iosAlertDialog = new ReTestCalendaDialog(this).builder();
        iosAlertDialog.setPositiveButton("帮我添加到日历", v -> {

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
        addCalendarEvent(this,"测试","猜测是超链接离开进口量",System.currentTimeMillis());
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
            if (count > 0) {//存在现有账户，取第一个账户的id返回
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
    public  void addCalendarEvent(Context context,String title, String description, long beginTime){
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

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(beginTime);//设置开始时间
        long start = mCalendar.getTime().getTime();
        mCalendar.setTimeInMillis(start + ONE_HOUR);//设置终止时间
        long end = mCalendar.getTime().getTime();

        event.put(CalendarContract.Events.DTSTART, start);
        event.put(CalendarContract.Events.DTEND, end);
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
        TimeZone tz = TimeZone.getDefault(); // 获取默认时区
        event.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());  //这个是时区，必须有，
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

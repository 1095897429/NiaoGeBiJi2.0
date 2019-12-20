package com.qmkj.niaogebiji.module.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.qmkj.niaogebiji.DaoMaster;
import com.qmkj.niaogebiji.DaoSession;
import com.qmkj.niaogebiji.HistoryDao;
import com.qmkj.niaogebiji.common.BaseApp;
import com.qmkj.niaogebiji.module.bean.History;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-07-18
 * 描述:greenDao数据库管理类
 */
public class DBManager {

    //数据库名称
    private String dbName = "ngbj_db";
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper mOpenHelper;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private Context mContext;

    private DBManager(Context context){
        this.mContext = context;
        //初始化数据库
        mOpenHelper = new DaoMaster.DevOpenHelper(mContext,dbName,null);
        daoMaster = new DaoMaster(getWritableDatabase());
        daoSession = daoMaster.newSession();
    }

    //获取单例
    public static DBManager getInstance(){
        if (null == mInstance) {
            synchronized (DBManager.class){
                if(null == mInstance){
                    mInstance = new DBManager(BaseApp.getApplication());
                }
            }
        }
        return mInstance;
    }


    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (null == mOpenHelper) {
            mOpenHelper = new DaoMaster.DevOpenHelper(mContext, dbName, null);
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        return db;
    }


    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (null == mOpenHelper) {
            mOpenHelper = new DaoMaster.DevOpenHelper(mContext, dbName, null);
        }
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        return db;
    }


    /** -------------  用户 开始部分  ------------   */
    /**
     * 插入一位用户
     *
     * @param UserBean
     */
//    public void insertUserInfo(UserBean UserBean) {
//        UserBeanDao userDao = daoSession.getUserBeanDao();
//        userDao.insert(UserBean);
//    }
//
//    /**
//     * 删除一位用户
//     */
//    public void deleteUserInfo() {
//        UserBeanDao UserBeanDao = daoSession.getUserBeanDao();
//        UserBeanDao.deleteAll();
//    }
//
//    /**
//     * 更新一位用户
//     *
//     * @param UserBean
//     */
//    public void updateUserInfo(UserBean UserBean) {
//        UserBeanDao userDao = daoSession.getUserBeanDao();
//        userDao.update(UserBean);
//    }
//
//    /**
//     * 查询一位用户
//     */
//    public List<UserBean> queryUserInfo() {
//        UserBeanDao userDao = daoSession.getUserBeanDao();
//        QueryBuilder<UserBean> qb = userDao.queryBuilder();
//        List<UserBean> list = qb.list();
//        return list;
//    }


    /** -------------  用户 结束部分  ------------   */

    /** -------------  历史 开始部分  ------------   */

    public void insertHistory(History history) {
        HistoryDao historyDao = daoSession.getHistoryDao();
        historyDao.insert(history);
    }

    public void deleteHistory() {
        HistoryDao historyDao = daoSession.getHistoryDao();
        historyDao.deleteAll();
    }

    public void updateHistory(History history) {
        HistoryDao historyDao = daoSession.getHistoryDao();
        historyDao.update(history);
    }


    //descend 降序
    public List<History> queryHistory() {
        HistoryDao historyDao = daoSession.getHistoryDao();
        QueryBuilder<History> qb = historyDao.queryBuilder().orderDesc(HistoryDao.Properties.Time);
        List<History> list = qb.list();
        return list;
    }

    public History queryHistory(String name){
        HistoryDao historyDao = daoSession.getHistoryDao();
        QueryBuilder<History> qb = historyDao.queryBuilder().where(HistoryDao.Properties.Name.eq(name));
        List<History> list = qb.list();
        if(null != list && !list.isEmpty()){
            return list.get(0);
        }
        return null;
    }


    /** -------------  历史 结束部分  ------------   */


}

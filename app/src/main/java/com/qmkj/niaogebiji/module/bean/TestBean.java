package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-19
 * 描述:测试bean
 */
public class TestBean extends BaseBean {

    private String answer;
    private boolean isSelect;
    private boolean isError;
    private boolean isClick;

    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}

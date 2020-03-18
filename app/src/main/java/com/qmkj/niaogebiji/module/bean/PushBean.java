package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2020-03-18
 * 描述:
 */
public class PushBean extends BaseBean {

    private long msg_id;
    private String rom_type;
    private String n_content;
    private String n_title;
    private JPushBean n_extras;

    public JPushBean getN_extras() {
        return n_extras;
    }

    public void setN_extras(JPushBean n_extras) {
        this.n_extras = n_extras;
    }

    public long getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(long msg_id) {
        this.msg_id = msg_id;
    }

    public String getRom_type() {
        return rom_type;
    }

    public void setRom_type(String rom_type) {
        this.rom_type = rom_type;
    }

    public String getN_content() {
        return n_content;
    }

    public void setN_content(String n_content) {
        this.n_content = n_content;
    }

    public String getN_title() {
        return n_title;
    }

    public void setN_title(String n_title) {
        this.n_title = n_title;
    }


//    其他地方定义了，这里就不用定义了
//    private static class JPushBean extends BaseBean{
//        private String jump_type;
//        private String jump_info;
//
//        public String getJump_type() {
//            return jump_type;
//        }
//
//        public void setJump_type(String jump_type) {
//            this.jump_type = jump_type;
//        }
//
//        public String getJump_info() {
//            return jump_info;
//        }
//
//        public void setJump_info(String jump_info) {
//            this.jump_info = jump_info;
//        }
//    }
}

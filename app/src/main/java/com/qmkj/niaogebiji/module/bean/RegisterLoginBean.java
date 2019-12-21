package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.io.Serializable;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-11-11
 * 描述:用户信息
 */
public class RegisterLoginBean extends BaseBean {



    private UserInfo data;

    public void setData(UserInfo data) {
        this.data = data;
    }
    public UserInfo getData() {
        return data;
    }


    //注册实体
    public static class UserInfo implements Serializable {
        private String uid;
        private String nickname;
        private String name;
        private String mobile;
        private String email;
        private String opentype;
        private String profession;
        private String industry;
        private String company;
        private String product;
        private String city;
        private String graduated_at;
        private String sign;
        private String avatar;
        private String ivtoken;
        private String from_uid;
        //1 男 2 女
        private String gender;
        private String birthday;
        private String company_name;
        private String appname;
        private String level;
        private String type;
        //注册类型：1-鸟哥笔记，2-渠道点评，3-鸟哥ASO
        private String reg_type;
        private String is_aso_target;
        private String chn_code;
        private String company_abbr;
        private String link;
        private String company_contact;
        private String position;
        private String card;
        private String point;
        private String add_point;
        private String invite_num;
        private String is_pro;
        private String pro_summary;
        private String auth_email_status;
        private String auth_card_status;
        //企业认证状态：1-正常，2-未提交，3-审核中，4-未通过
        private String auth_com_status;
        private String ban_memo;
        private String ban_time;
        private String reg_time;
        private String status;
        private String act_state;
        private String created_at;
        private String is_first_applogin;
        private String ssid;
        private int is_wechat_bind;
        private String access_token;
        private String signed_today;
        //个人中心消息通知：1-有新消息，0-无新消息
        private String is_red;
        //微信登录
        private String wechat_token;

        //邀请信息
        private String invite_url;


        private String per_profession;
        private String per_career;
        private String vip_start_date;
        private String vip_end_date;
        //vip_last_time
        private String vip_last_time;


        //关注别人状态
        private int follow_status;


        public String getVip_last_time() {
            return vip_last_time;
        }

        public void setVip_last_time(String vip_last_time) {
            this.vip_last_time = vip_last_time;
        }

        public int getFollow_status() {
            return follow_status;
        }

        public void setFollow_status(int follow_status) {
            this.follow_status = follow_status;
        }

        public String getIs_aso_target() {
            return is_aso_target;
        }

        public void setIs_aso_target(String is_aso_target) {
            this.is_aso_target = is_aso_target;
        }

        public String getAdd_point() {
            return add_point;
        }

        public void setAdd_point(String add_point) {
            this.add_point = add_point;
        }

        public String getInvite_num() {
            return invite_num;
        }

        public void setInvite_num(String invite_num) {
            this.invite_num = invite_num;
        }

        public String getAct_state() {
            return act_state;
        }

        public void setAct_state(String act_state) {
            this.act_state = act_state;
        }

        public String getIs_first_applogin() {
            return is_first_applogin;
        }

        public void setIs_first_applogin(String is_first_applogin) {
            this.is_first_applogin = is_first_applogin;
        }

        public String getSsid() {
            return ssid;
        }

        public void setSsid(String ssid) {
            this.ssid = ssid;
        }

        public String getPer_profession() {
            return per_profession;
        }

        public void setPer_profession(String per_profession) {
            this.per_profession = per_profession;
        }

        public String getPer_career() {
            return per_career;
        }

        public void setPer_career(String per_career) {
            this.per_career = per_career;
        }

        public String getVip_start_date() {
            return vip_start_date;
        }

        public void setVip_start_date(String vip_start_date) {
            this.vip_start_date = vip_start_date;
        }

        public String getVip_end_date() {
            return vip_end_date;
        }

        public void setVip_end_date(String vip_end_date) {
            this.vip_end_date = vip_end_date;
        }

        public String getInvite_url() {
            return invite_url;
        }

        public void setInvite_url(String invite_url) {
            this.invite_url = invite_url;
        }

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getWechat_token() {
            return wechat_token;
        }

        public void setWechat_token(String wechat_token) {
            this.wechat_token = wechat_token;
        }

        public String getIs_red() {
            return is_red;
        }

        public void setIs_red(String is_red) {
            this.is_red = is_red;
        }

        public String getSigned_today() {
            return signed_today;
        }

        public void setSigned_today(String signed_today) {
            this.signed_today = signed_today;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
        public String getNickname() {
            return nickname;
        }

        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }
        public String getMobile() {
            return mobile;
        }

        public void setEmail(String email) {
            this.email = email;
        }
        public String getEmail() {
            return email;
        }

        public void setOpentype(String opentype) {
            this.opentype = opentype;
        }
        public String getOpentype() {
            return opentype;
        }

        public void setProfession(String profession) {
            this.profession = profession;
        }
        public String getProfession() {
            return profession;
        }

        public void setIndustry(String industry) {
            this.industry = industry;
        }
        public String getIndustry() {
            return industry;
        }

        public void setCompany(String company) {
            this.company = company;
        }
        public String getCompany() {
            return company;
        }

        public void setProduct(String product) {
            this.product = product;
        }
        public String getProduct() {
            return product;
        }

        public void setCity(String city) {
            this.city = city;
        }
        public String getCity() {
            return city;
        }

        public void setGraduated_at(String graduated_at) {
            this.graduated_at = graduated_at;
        }
        public String getGraduated_at() {
            return graduated_at;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
        public String getSign() {
            return sign;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
        public String getAvatar() {
            return avatar;
        }

        public void setIvtoken(String ivtoken) {
            this.ivtoken = ivtoken;
        }
        public String getIvtoken() {
            return ivtoken;
        }

        public void setFrom_uid(String from_uid) {
            this.from_uid = from_uid;
        }
        public String getFrom_uid() {
            return from_uid;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }
        public String getGender() {
            return gender;
        }

        public void setCompany_name(String company_name) {
            this.company_name = company_name;
        }
        public String getCompany_name() {
            return company_name;
        }

        public void setAppname(String appname) {
            this.appname = appname;
        }
        public String getAppname() {
            return appname;
        }

        public void setLevel(String level) {
            this.level = level;
        }
        public String getLevel() {
            return level;
        }

        public void setType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }

        public void setReg_type(String reg_type) {
            this.reg_type = reg_type;
        }
        public String getReg_type() {
            return reg_type;
        }

        public void setChn_code(String chn_code) {
            this.chn_code = chn_code;
        }
        public String getChn_code() {
            return chn_code;
        }

        public void setCompany_abbr(String company_abbr) {
            this.company_abbr = company_abbr;
        }
        public String getCompany_abbr() {
            return company_abbr;
        }

        public void setLink(String link) {
            this.link = link;
        }
        public String getLink() {
            return link;
        }

        public void setCompany_contact(String company_contact) {
            this.company_contact = company_contact;
        }
        public String getCompany_contact() {
            return company_contact;
        }

        public void setPosition(String position) {
            this.position = position;
        }
        public String getPosition() {
            return position;
        }

        public void setCard(String card) {
            this.card = card;
        }
        public String getCard() {
            return card;
        }

        public void setPoint(String point) {
            this.point = point;
        }
        public String getPoint() {
            return point;
        }

        public void setIs_pro(String is_pro) {
            this.is_pro = is_pro;
        }
        public String getIs_pro() {
            return is_pro;
        }

        public void setPro_summary(String pro_summary) {
            this.pro_summary = pro_summary;
        }
        public String getPro_summary() {
            return pro_summary;
        }

        public void setAuth_email_status(String auth_email_status) {
            this.auth_email_status = auth_email_status;
        }
        public String getAuth_email_status() {
            return auth_email_status;
        }

        public void setAuth_card_status(String auth_card_status) {
            this.auth_card_status = auth_card_status;
        }
        public String getAuth_card_status() {
            return auth_card_status;
        }

        public void setAuth_com_status(String auth_com_status) {
            this.auth_com_status = auth_com_status;
        }
        public String getAuth_com_status() {
            return auth_com_status;
        }

        public void setBan_memo(String ban_memo) {
            this.ban_memo = ban_memo;
        }
        public String getBan_memo() {
            return ban_memo;
        }

        public void setBan_time(String ban_time) {
            this.ban_time = ban_time;
        }
        public String getBan_time() {
            return ban_time;
        }

        public void setReg_time(String reg_time) {
            this.reg_time = reg_time;
        }
        public String getReg_time() {
            return reg_time;
        }

        public void setStatus(String status) {
            this.status = status;
        }
        public String getStatus() {
            return status;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
        public String getCreated_at() {
            return created_at;
        }

        public void setIs_wechat_bind(int is_wechat_bind) {
            this.is_wechat_bind = is_wechat_bind;
        }
        public int getIs_wechat_bind() {
            return is_wechat_bind;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }
        public String getAccess_token() {
            return access_token;
        }

    }
}

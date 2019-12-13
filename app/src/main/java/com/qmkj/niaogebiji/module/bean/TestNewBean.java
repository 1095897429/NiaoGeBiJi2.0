package com.qmkj.niaogebiji.module.bean;

import com.qmkj.niaogebiji.common.base.BaseBean;

import java.util.List;

/**
 * @author zhouliang
 * 版本 1.0
 * 创建时间 2019-12-13
 * 描述:测试新实体
 */
public class TestNewBean extends BaseBean {

        private String id;
        private String question;
        private int answer;
        private List<String> option;
        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }

        public void setQuestion(String question) {
            this.question = question;
        }
        public String getQuestion() {
            return question;
        }

        public void setAnswer(int answer) {
            this.answer = answer;
        }
        public int getAnswer() {
            return answer;
        }

        public void setOption(List<String> option) {
            this.option = option;
        }
        public List<String> getOption() {
            return option;
        }


}

package com.ucas.jomoo.model;

import java.util.List;

/**
 * Created by Rrtoyewx on 16/2/21.
 */
public class IFlyTalkVoiceModel {
    private int sn;
    private boolean ls;
    private int bg;
    private int ed;
    private List<Ws> ws;

    public void setSn(int sn) {
        this.sn = sn;
    }

    public int getSn() {
        return sn;
    }

    public void setLs(boolean ls) {
        this.ls = ls;
    }

    public boolean getLs() {
        return ls;
    }

    public void setBg(int bg) {
        this.bg = bg;
    }

    public int getBg() {
        return bg;
    }

    public void setEd(int ed) {
        this.ed = ed;
    }

    public int getEd() {
        return ed;
    }

    public void setWs(List<Ws> ws) {
        this.ws = ws;
    }

    public List<Ws> getWs() {
        return ws;
    }

    public class Cw {

        private int sc;
        private String w;

        public void setSc(int sc) {
            this.sc = sc;
        }

        public int getSc() {
            return sc;
        }

        public void setW(String w) {
            this.w = w;
        }

        public String getW() {
            return w;
        }

    }

    public class Ws {

        private int bg;
        private List<Cw> cw;

        public void setBg(int bg) {
            this.bg = bg;
        }

        public int getBg() {
            return bg;
        }

        public void setCw(List<Cw> cw) {
            this.cw = cw;
        }

        public List<Cw> getCw() {
            return cw;
        }

    }

}

package com.zjl.criminalintent.domain;

import java.util.Date;
import java.util.UUID;

/**
 * Created by lenovo on 2017/8/8.
 */
public class Crime {
    //唯一标识符
    private UUID mId;
    //标题
    private String mTitle;
    //日期
    private Date mDate;
    //是否已经解决
    private boolean mSolved;
    //是否需要警方介入
    private boolean mRequiresPolice;

    private String mSuspect;

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public Crime(){
        //随机获取一个唯一标识符
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(UUID id){
        mId = id;
        mDate = new Date();
    }

    public String getPhotoFileName(){
        return "IMG_" + getId().toString() + ".jpg";
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public boolean isRequiresPolice() {
        return mRequiresPolice;
    }

    public void setRequiresPolice(boolean requiresPolice) {
        mRequiresPolice = requiresPolice;
    }
}

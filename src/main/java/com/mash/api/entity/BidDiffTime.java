package com.mash.api.entity;

public class BidDiffTime {

    /**
     * 距离开始时间
     */
    private long startDiffTime;

    /**
     * 开始后距离结束时间
     */
    private long endDiffTime;

    /**
     * 0 未开始
     * 1 已开始
     * 2 已结束
     */
    private int type;

    public long getStartDiffTime() {
        return startDiffTime;
    }

    public void setStartDiffTime(long startDiffTime) {
        this.startDiffTime = startDiffTime;
    }

    public long getEndDiffTime() {
        return endDiffTime;
    }

    public void setEndDiffTime(long endDiffTime) {
        this.endDiffTime = endDiffTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

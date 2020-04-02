package com.foxconn.iisd.rcadsvc.msg;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;



public class CasePlotNoteExportMsg {

    private int id;

    private Long seqNo;

    private String note_title;

    private String note_text;

    private String note_remark;

    private String createTime;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Long seqNo) {
        this.seqNo = seqNo;
    }

    public String getNote_title() {
        return note_title;
    }

    public void setNote_title(String note_title) {
        this.note_title = note_title;
    }

    public String getNote_text() {
        return note_text;
    }

    public void setNote_text(String note_text) {
        this.note_text = note_text;
    }

    public String getNote_Remark() {
        return note_remark;
    }

    public void setNote_Remark(String note_remark) {
        this.note_remark = note_remark;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}

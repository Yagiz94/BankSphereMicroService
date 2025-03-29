package com.example.user.dto;

public class AdminDto {

    private Long requestUserID;

    private Long targetUserID;

    public Long getRequestUserID() {
        return requestUserID;
    }

    public void setRequestUserID(Long requestUserID) {
        this.requestUserID = requestUserID;
    }

    public Long getTargetUserID() {
        return targetUserID;
    }

    public void setTargetUserID(Long targetUserID) {
        this.targetUserID = targetUserID;
    }
}

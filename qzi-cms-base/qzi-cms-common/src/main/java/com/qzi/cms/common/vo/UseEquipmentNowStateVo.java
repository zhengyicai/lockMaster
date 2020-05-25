package com.qzi.cms.common.vo;

import java.util.Date;

/**
 * Created by Administrator on 2019/3/25.
 */
public class UseEquipmentNowStateVo {
    private String equipmentNo;

    private String state;
    private String cmd;
    private String result;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getEquipmentNo() {
        return equipmentNo;
    }

    public void setEquipmentNo(String equipmentNo) {
        this.equipmentNo = equipmentNo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
}

package com.qzi.cms.common.po;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 用户开锁记录udp
 * Created by Administrator on 2018/12/29.
 */


@Table(name="use_lock_udp")
public class UseLockUdpPo {
    /**
     * 主键编号
     */
    @Id
    private String id;

    private String communityId;

    private Date createTime;

    private String state;

    private String equipmentId;

    private String roomNumber;
    private String phone;


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
}

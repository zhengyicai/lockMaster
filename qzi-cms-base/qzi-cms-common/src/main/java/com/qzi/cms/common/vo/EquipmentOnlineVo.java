package com.qzi.cms.common.vo;

import java.util.Date;

/**
 * 小程序设备列表
 */
public class EquipmentOnlineVo {

    private String id;
    /**
     * 设备编号
     */
    private String equipmentId;




    /**
     * 设备名称
     */
    private String equipmentName;
    /**
     * 设备类型
     */
    private String equipmentType;
    /**
     * 小区主键编号
     */
    private String communityId;
    /**
     * 楼宇主键编号
     */
    private String buildingId;
    /**
     * 单元名称
     */
    private Integer unitName;
    /**
     * 云之讯clientNumber
     */
    private String clientNumber;
    /**
     * 云之讯密码
     */
    private String clientPwd;
    /**
     * 云之讯token
     */
    private String loginToken;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 状态
     */
    private String state;

    /**
     * 当前请求时间
     */
    private Date nowDate;

    /**
     * 当前门磁状态
     */
    private String nowState;


    /**
     * 设备识别码
     */
    private String equCode;
    /**
     * 序列号
     */
    private String  equId;

    private String ip;
    private String gate;
    private String mask;
    private String ename;

    private String communityName;
    private String status;

    private String residentId;

    private Date onlineCreateTime;

    public Date getOnlineCreateTime() {
        return onlineCreateTime;
    }

    public void setOnlineCreateTime(Date onlineCreateTime) {
        this.onlineCreateTime = onlineCreateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(String equipmentType) {
        this.equipmentType = equipmentType;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public Integer getUnitName() {
        return unitName;
    }

    public void setUnitName(Integer unitName) {
        this.unitName = unitName;
    }

    public String getClientNumber() {
        return clientNumber;
    }

    public void setClientNumber(String clientNumber) {
        this.clientNumber = clientNumber;
    }

    public String getClientPwd() {
        return clientPwd;
    }

    public void setClientPwd(String clientPwd) {
        this.clientPwd = clientPwd;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getNowDate() {
        return nowDate;
    }

    public void setNowDate(Date nowDate) {
        this.nowDate = nowDate;
    }

    public String getNowState() {
        return nowState;
    }

    public void setNowState(String nowState) {
        this.nowState = nowState;
    }

    public String getEquCode() {
        return equCode;
    }

    public void setEquCode(String equCode) {
        this.equCode = equCode;
    }

    public String getEquId() {
        return equId;
    }

    public void setEquId(String equId) {
        this.equId = equId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getGate() {
        return gate;
    }

    public void setGate(String gate) {
        this.gate = gate;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResidentId() {
        return residentId;
    }

    public void setResidentId(String residentId) {
        this.residentId = residentId;
    }
}

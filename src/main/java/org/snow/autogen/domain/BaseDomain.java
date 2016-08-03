package org.snow.autogen.domain;

/**
 * Copyright @ 2016QIANLONG.
 * All right reserved.
 * Class Name : org.snow.database.domain
 * Description :
 * Author : gaoguoxiang
 * Date : 2016/8/3
 */

public class BaseDomain {

    private String columName;
    private String columType;
    private String remark;

    public String getColumName() {
        return columName;
    }

    public void setColumName(String columName) {
        this.columName = columName;
    }

    public String getColumType() {
        return columType;
    }

    public void setColumType(String columType) {
        this.columType = columType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "BaseDomain{" +
                "columName='" + columName + '\'' +
                ", columType='" + columType + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}

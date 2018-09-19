package org.snow.autogen.domain;

/**  用于存放数据库解析实体类的数据
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2016/8/3
 * Time: 20:48
 */

public class BaseDomain {

    private String columName; //列名称（字段/属性名称）
    private String columType; //列类型（字段/属性类型）
    private String remark; //注释

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

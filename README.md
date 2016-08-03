# generate-entity
该generate根据basic-core框架设计，可以反向生成和数据相关的entity、dao、service、impl、mapper  

对表的要求为xxx_yyy_zzz (t_sys_use_info) 系统会将xxx_yyy_zzz (t_sys_user_info) 表转换成实体类 YyyZzz.java (SysUserInfo.java)
即默认去除第一个下划线之前的内容


#配置文件（jdbc.properties）中 root.dir  entityPackageName不可为空   daoPackageName servicePackageName implPackageName可以为空
#若 daoPackageName servicePackageName implPackageName 为空 则系统默认赋值为下面注释的值   若不为空则为设置的值
#daoPackageName = org.snow.test.dao
#servicePackageName = org.snow.test.service
#implPackageName = org.snow.test.service.impl

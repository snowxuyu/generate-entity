package ${implPackageName};

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import ${daoPackageName}.${entityName}Dao;
import ${dtoPackageName}.${entityName}Dto;
import ${entityPackageName}.${entityName};
import ${serPackageName}.${entityName}Service;
import org.framework.basic.service.impl.BaseServiceImpl;
import org.framework.basic.system.ResponseEntity;
import org.framework.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;


@Service
public class ${entityName}ServiceImpl extends BaseServiceImpl<${entityName}> implements ${entityName}Service {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ${entityName}Dao ${low_entityName}Dao;


    /**
    * 添加
    * @param ${low_entityName}Dto
    * @return
    * @throws BaseException
    */
    public void create${entityName}(${entityName}Dto ${low_entityName}Dto) throws BaseException {
        ${entityName} ${low_entityName} = new ${entityName}();
${setPropertiesField}
        ${low_entityName}Dao.insert(${low_entityName});
    }

    /**
    * 修改
    * @param ${low_entityName}Dto
    * @return
    * @throws BaseException
    */
    public void update${entityName}(${entityName}Dto ${low_entityName}Dto) throws BaseException {
        if (null == ${low_entityName}Dto.getId()) {
            throw new BaseException("id不能为空");
        }

        ${entityName} ${low_entityName} = ${low_entityName}Dao.getById(${low_entityName}Dto.getId());
${setPropertiesField}
        ${low_entityName}Dao.update(${low_entityName});
    }

}

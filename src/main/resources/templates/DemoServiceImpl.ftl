package ${implPackageName};

import ${daoPackageName}.${entityName}Dao;
import ${entityPackageName}.${entityName};
import ${serPackageName}.${entityName}Service;
import org.framework.basic.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;


@Service
public class ${entityName}ServiceImpl extends BaseServiceImpl<${entityName}> implements ${entityName}Service {

    @Resource
    private ${entityName}Dao ${low_entityName}Dao;

}

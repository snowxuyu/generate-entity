package ${serPackageName};

import ${dtoPackageName}.${entityName}Dto;
import ${entityPackageName}.${entityName};
import com.mobanker.framework.service.BaseService;
import com.mobanker.framework.dto.ResponseEntity;
import com.mobanker.framework.exception.BaseException;


public interface ${entityName}Service extends BaseService<${entityName}> {

    void create${entityName}(${entityName}Dto ${low_entityName}Dto) throws BaseException;

    void update${entityName}(${entityName}Dto ${low_entityName}Dto) throws BaseException;
}

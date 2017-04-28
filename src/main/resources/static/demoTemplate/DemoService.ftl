package ${serPackageName};

import ${dtoPackageName}.${entityName}Dto;
import ${entityPackageName}.${entityName};
import org.framework.basic.service.BaseService;
import org.framework.basic.system.ResponseEntity;
import org.framework.exception.BaseException;


public interface ${entityName}Service extends BaseService<${entityName}> {

    ResponseEntity create${entityName}(${entityName}Dto ${low_entityName}Dto) throws BaseException;

    ResponseEntity update${entityName}(${entityName}Dto ${low_entityName}Dto) throws BaseException;
}

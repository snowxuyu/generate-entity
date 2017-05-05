package ${controllerPackageName};

import com.alibaba.fastjson.JSONObject;
import ${dtoPackageName}.${entityName}Dto;
import ${entityPackageName}.${entityName};
import ${serPackageName}.${entityName}Service;
import org.framework.basic.system.BaseResponse;
import org.framework.basic.system.ResponseEntity;
import org.framework.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/${low_entityName}")
public class ${entityName}Controller {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ${entityName}Service ${low_entityName}Service;

    /**
     * 添加
     *
     * @param ${low_entityName}Dto
     * @return
     */
    @RequestMapping(value = "/create${entityName}", method = RequestMethod.POST)
    public ResponseEntity create${entityName}(${entityName}Dto ${low_entityName}Dto) {
        logger.debug("${low_entityName}Controller create request info:{}", JSONObject.toJSONString(${low_entityName}Dto));
        try {
            ${low_entityName}Service.create${entityName}(${low_entityName}Dto);
            return BaseResponse.buildSuccess("添加成功");
        } catch (BaseException e) {
            logger.error("添加失败: " + e);
            return BaseResponse.buildError("[添加失败] " + e.getMessage());
        }

    }


    /**
     * 修改
     *
     * @param ${low_entityName}Dto
     * @return
     */
    @RequestMapping(value = "/update${entityName}", method = RequestMethod.POST)
    public ResponseEntity update${entityName}(${entityName}Dto ${low_entityName}Dto) {
        logger.debug("${low_entityName}Controller update request info:{}", JSONObject.toJSONString(${low_entityName}Dto));
        try {
            ${low_entityName}Service.update${entityName}(${low_entityName}Dto);
            return BaseResponse.buildSuccess("修改成功");
        } catch (BaseException e) {
            logger.error("修改失败: " + e);
            return BaseResponse.buildError("[修改失败] " + e.getMessage());
        }

    }


    /**
     * 删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete${entityName}ById", method = RequestMethod.POST)
    public ResponseEntity delete${entityName}ById(Long id) {
        logger.debug("userController deleteById request info:{}", id);
        try {
            ${low_entityName}Service.deleteById(id);
            return BaseResponse.buildSuccess("删除成功");
        } catch (BaseException e) {
            logger.error("删除失败: " + e);
            return BaseResponse.buildError("[删除失败] " + e.getMessage());
        }

    }


    /**
     * 查询
     *
     * @param ${low_entityName}
     * @return
     */
    @RequestMapping(value = "/query${entityName}ByParams", method = RequestMethod.POST)
    public ResponseEntity query${entityName}ByParams(${entityName} ${low_entityName}) {
        logger.debug("${low_entityName}Controller query${entityName}ByParams request info:{}", JSONObject.toJSONString(${low_entityName}));
        try {
            List<${entityName}> ${low_entityName}List = ${low_entityName}Service.getByObj(${low_entityName});
            return BaseResponse.buildSuccess(${low_entityName}List, "查询成功");
        } catch (BaseException e) {
            logger.error("查询失败: " + e);
            return BaseResponse.buildError("[查询失败] " + e.getMessage());
        }

    }


}

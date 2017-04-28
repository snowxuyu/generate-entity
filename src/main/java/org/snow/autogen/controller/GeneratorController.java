package org.snow.autogen.controller;

import org.snow.autogen.dto.RequestDto;
import org.snow.autogen.filegenerate.GenerateFile;
import org.snow.autogen.system.BaseResponse;
import org.snow.autogen.system.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-04-27
 * Time: 21:43
 */
@RestController
@RequestMapping("/generate")
public class GeneratorController {

    @Resource
    private GenerateFile generateFile;

    @RequestMapping(value = "/instance", method = RequestMethod.POST)
    public ResponseEntity instance(RequestDto requestDto) {
        try {
            String path = generateFile.autoGenerator(requestDto);
            return BaseResponse.buildSuccess("生成成功, 文件所在路径为: " + path);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return BaseResponse.buildError(e.getMessage());
        }
    }
}

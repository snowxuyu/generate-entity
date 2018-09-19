package org.snow.autogen.filegenerate;

import org.snow.autogen.dto.RequestDto;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-04-28
 * Time: 21:37
 */
public interface GenerateFile {

    String autoGenerator(RequestDto requestDto) throws RuntimeException, IllegalAccessException;
}

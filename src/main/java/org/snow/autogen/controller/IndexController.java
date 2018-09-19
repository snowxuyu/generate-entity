package org.snow.autogen.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created with IntelliJ IDEA.
 * User: snowxuyu
 * Date: 2017-04-27
 * Time: 20:53
 */
@Controller
public class IndexController {
    @RequestMapping("/index")
    public String index(Model model) {
        return "index";
    }
}

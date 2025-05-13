package cz.pm2k.swaply.controller

import io.swagger.v3.oas.annotations.Hidden
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

@Hidden
@Controller
class FaviconController {

    @GetMapping("favicon.ico")
    @ResponseBody
    fun returnNoFavicon() { /* favicon is no longer included in spring boot */ }

}

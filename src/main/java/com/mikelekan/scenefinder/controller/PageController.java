package com.mikelekan.scenefinder.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController
{
    @GetMapping("/")
    public String index()
    {
        return "index";
    }

    @GetMapping("/map")
    public String map()
    {
        return "map";
    }
}

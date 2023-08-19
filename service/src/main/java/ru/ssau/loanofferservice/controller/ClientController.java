package ru.ssau.loanofferservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
public class ClientController {
        @RequestMapping(value = "/")
        public String index() {
            return "index";
        }
}

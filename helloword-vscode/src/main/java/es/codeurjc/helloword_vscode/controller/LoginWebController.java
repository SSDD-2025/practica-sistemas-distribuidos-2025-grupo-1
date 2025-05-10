package es.codeurjc.helloword_vscode.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginWebController {
    /* Login page */ 
    @GetMapping("/login")
    public String login() {
        return "login";
    }


    /* Login error page */ 
    @GetMapping("/loginerror")
    public String loginerror() {
        return "loginerror";
    }
}

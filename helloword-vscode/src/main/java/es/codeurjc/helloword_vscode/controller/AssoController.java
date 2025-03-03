package es.codeurjc.helloword_vscode.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

public class AssoController {
    @GetMapping("/")
	public String getPosts(Model model){
		return "index";
	}
}

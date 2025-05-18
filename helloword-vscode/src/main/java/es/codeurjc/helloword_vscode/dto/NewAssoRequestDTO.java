package es.codeurjc.helloword_vscode.dto;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public record NewAssoRequestDTO (
        String name,
        MultipartFile imageField
){}

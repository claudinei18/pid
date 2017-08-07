package com.example;

import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@Scope("prototype")
@RequestMapping("/rest")
public class ImagemController {

    @RequestMapping(value = "/imagem", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity<Void> inserirImagem(@RequestParam(name = "arquivo", required = false) MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        try {
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get("/app/" + file.getOriginalFilename());
            Files.write(path, bytes);
            System.out.println("Criou");
            System.out.println(System.getProperty("user.dir") + "/src/main/resources/static/imagens/" + file.getOriginalFilename());
            System.out.println(Files.exists(Paths.get(System.getProperty("user.dir") + "/src/main/resources/static/imagens/" + file.getOriginalFilename() + "/" + file.getOriginalFilename())));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().build();

    }

}
package com.example;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.json.JSONException;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    ResponseEntity<?> inserirImagem(@RequestParam(name = "arquivo", required = true) MultipartFile file,
                                       @RequestParam(name = "codigo",  required = true) String codigo) {

        String root = "/home/claudinei/Documentos/pidImagens/";

        File folderImage = new File(root + codigo);
        if(folderImage.exists()){
            folderImage.delete();
        }

        folderImage.mkdir();


        System.out.println(codigo);

        if (file.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        File imageFile = new File(folderImage.getPath() + "/" + file.getOriginalFilename());
        Path path = null;
        try {
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            path = Paths.get(folderImage.getPath() + "/" + file.getOriginalFilename());
            Files.write(path, bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject json = new JSONObject();
        try {
            json.put("path", path.toString());
            json.put("nomeImagem", file.getOriginalFilename());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(json.toString());

    }

}
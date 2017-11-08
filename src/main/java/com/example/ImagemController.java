package com.example;

import com.example.pseimage.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@Scope("prototype")
@RequestMapping("/rest")
public class ImagemController {

    @RequestMapping(value = "/imagem", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity<?> inserirImagem(@RequestParam(name = "arquivo", required = true) MultipartFile file,
                                    @RequestParam(name = "codigo", required = true) String codigo) {

        String root = "/home/claudinei/Documentos/pidImagens/";

        File folderImage = new File(root + codigo);
        if (folderImage.exists()) {
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

    @RequestMapping(value = "/funcoes", method = RequestMethod.POST)
    public
    @ResponseBody
    ResponseEntity<?> inserirImagem(@RequestBody Map<String, Object> map) {
        String root = "/home/claudinei/Documentos/pidImagens/";
        String codeImagemOriginal = (String) map.get("codeImagemOriginal");
        String nomeImagem = (String) map.get("nomeImagem");
        String imageFile = root + codeImagemOriginal + "/" + nomeImagem;
        System.out.println(map.toString());


        JSONArray jsonArray3 = new JSONArray((ArrayList)map.get("funcoes"));
        System.out.println(jsonArray3.length());
        File folderImage = new File(root + codeImagemOriginal);

        String lastUsed = "";
        System.out.println("Array " + jsonArray3);

        List<String> params = null;

        for(int i = 0; i < jsonArray3.length(); i++){
            System.out.println("ENTROU 1 " + jsonArray3.length());
            try {
                Object o = jsonArray3.get(i);
                if(!o.equals("")){
                    JSONObject object = (JSONObject) o;
                    System.out.println("Object " + object);
                    String nome = object.getString("nome");
                    String[] args2 = {imageFile+lastUsed, "3", "3", "box", "1", "1", "1", "1", "1", "1", "1", "1", "1"};
                    params = new ArrayList<>();
                    for(int j=0; j<args2.length; j++){
                        params.add(args2[j]);
                    }
                    System.out.println(imageFile+lastUsed);
                    if(nome.equals("Negativo")){
                        System.out.println("Negativo");
                        new DigitalNegative().filter(params);
                        lastUsed += "_negative";
                    }
                    else if(nome.equals("Equalização de Histograma")){
                        System.out.println("Equalização");
                        new HistogramEqualization().filter(params);
                        lastUsed += "_histogram";
                    }
                    else if(nome.equals("Laplace")){
                        System.out.println("Laplace");
                        new Laplace().filter(params);
                        lastUsed += "_laplace_mask " + params.get(1)+"x"+params.get(2);
                    }
                    else if(nome.equals("Logarítmica")){
                        System.out.println("Logarítmica");
                        params.set(1, object.getString("c"));
                        new Logarithmic().filter(params);
                        lastUsed += "_logarithmic";
                    }
                    else if(nome.equals("Potência")){
                        System.out.println("Potencia");
                        new Potency().filter(params);
                        lastUsed += "_potency";
                    }
                    else if(nome.equals("Mediana")){
                        System.out.println("Mediana");
                        new Median().filter(params);
                        lastUsed += "_median";
                    }
                    else if(nome.equals("MIN")){
                        System.out.println("MIN");
                        new Min().filter(params);
                        lastUsed += "_min";
                    }
                    else if(nome.equals("MAX")){
                        System.out.println("MAX");
                        new Max().filter(params);
                        lastUsed += "_max";
                    }


//                    new GenericMask().filter(params);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONArray jsonArray = new JSONArray();

        int[] h = Filter.getHistogram(imageFile);
        JSONObject jsonHist = new JSONObject();
        try {
            jsonHist.put("histogramaDaImagemOriginal", Arrays.toString(h));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        jsonArray.put(jsonHist);

        File[] listOfFiles = folderImage.listFiles();

        if (folderImage.exists()) {
            try {
                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        String fileName = listOfFiles[i].getName();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", fileName);
                        jsonObject.put("url" , "http://localhost:5000/imagens/" + codeImagemOriginal + "/" + listOfFiles[i].getName());
                        if(fileName.endsWith("negative")){
                            jsonObject.put("nomeTransformacao", "Negativo Digital");
                            jsonObject.put("descricaoTransformacao", "O negativo digital ...");
                        }else if(fileName.endsWith("grayscale")){
                            jsonObject.put("nomeTransformacao", "Tons de Cinza");
                            jsonObject.put("descricaoTransformacao", "A imagem em tons de cinza ...");
                        }else if(fileName.endsWith("histogram")){
                            jsonObject.put("nomeTransformacao", "Equalização de Histrograma");
                            jsonObject.put("descricaoTransformacao", "O histograma equalizado ...");
                        }else if(fileName.endsWith("_laplace_mask " + params.get(1)+"x"+params.get(2))){
                            jsonObject.put("nomeTransformacao", "Laplace");
                            jsonObject.put("descricaoTransformacao", "A transformação de Laplace ...");
                        }else if(fileName.endsWith("logarithmic")){
                            jsonObject.put("nomeTransformacao", "Logarítmica");
                            jsonObject.put("descricaoTransformacao", "A transformação Logarítmica ...");
                        }else if(fileName.endsWith("potency")){
                            jsonObject.put("nomeTransformacao", "Potência");
                            jsonObject.put("descricaoTransformacao", "A transformação de Potência ...");
                        }else if(fileName.endsWith("median")){
                            jsonObject.put("nomeTransformacao", "Mediana");
                            jsonObject.put("descricaoTransformacao", "A transformação de Mediana ...");
                        }else if(fileName.endsWith("min")){
                            jsonObject.put("nomeTransformacao", "MIN");
                            jsonObject.put("descricaoTransformacao", "A transformação de MIN ...");
                        }else if(fileName.endsWith("max")){
                            jsonObject.put("nomeTransformacao", "MAX");
                            jsonObject.put("descricaoTransformacao", "A transformação de MAX ...");
                        }else {
                            jsonObject.put("nomeTransformacao", "Imagem Original");
                            jsonObject.put("descricaoTransformacao", "A imagem original ...");
                        }
                        jsonArray.put(jsonObject);
                    } else if (listOfFiles[i].isDirectory()) {
                        System.out.println("Directory " + listOfFiles[i].getName());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return ResponseEntity.ok(jsonArray.toString());

    }

}
package com.example;

import com.example.pseimage.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
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
    @Value("${ip}")
    private String ip;

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

        Filter.toGray(imageFile.getAbsolutePath());

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

        File folderImage = new File(root + codeImagemOriginal);

        File[] listOfFiles = folderImage.listFiles();

        JSONArray jsonArray3 = new JSONArray((ArrayList)map.get("funcoes"));
        if (folderImage.exists()) {
            String imagem2 = "";
            for (int i = 0; i < jsonArray3.length(); i++) {
                try {
                    Object o = jsonArray3.get(i);
                    if (!o.equals("")) {
                        JSONObject object = (JSONObject) o;
                        if(object.getString("imagem2") != null){
                            imagem2 = object.getString("imagem2");
                            System.out.println("imagem2" + imagem2);
                            break;
                        }
                    }
                } catch (JSONException e) {
                }
            }
            try {
                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile() && !listOfFiles[i].getName().equals(nomeImagem)
                            && !listOfFiles[i].getAbsolutePath().equals(imagem2)
                            && !listOfFiles[i].getName().equals(nomeImagem+"_grayscale")) {
                        listOfFiles[i].delete();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        System.out.println(jsonArray3.length());

        String lastUsed = "_grayscale";
        System.out.println("Array " + jsonArray3);

        List<String> params = null;

        JSONArray jsonArray = new JSONArray();

        for(int i = 0; i < jsonArray3.length(); i++){
            System.out.println("ENTROU 1 " + jsonArray3.length());
            try {
                Object o = jsonArray3.get(i);
                if(!o.equals("")){
                    JSONObject object = (JSONObject) o;
                    System.out.println("Object " + object);
                    String nome = object.getString("nome");
                    String[] args2 = {imageFile+lastUsed, "3", "3", "lowpass", "1", "1", "1", "1", "1", "1", "1", "1", "1"};
                    params = new ArrayList<>();
                    for(int j=0; j<args2.length; j++){
                        params.add(args2[j]);
                    }
                    System.out.println(imageFile+lastUsed);
                    if(nome.equals("Negativo")){
                        System.out.println("Negativo");
                        System.out.println(params);
                        new DigitalNegative().filter(params);
                        lastUsed += "_negative";

                        File file = new File(imageFile+lastUsed);

                        int[] h = Filter.getHistogram(file.getAbsolutePath());

                        Filter.plotHistogram(h, file.getAbsolutePath());
                        Filter.plotFDP(h, file.getAbsolutePath());

                        double mse = Filter.MSE(params.get(0), file.getAbsolutePath());
                        System.out.println("Erro médio quadrático = "+mse);

                        //double mseH = Filter.MSE(Filter.getHistogram(params.get(0)), Filter.getHistogram(params.get(1)));
                        //System.out.println("Erro médio quadrático H = "+mseH);

                        double psnr = Filter.PSNR(params.get(0), file.getAbsolutePath());
                        System.out.println("Pico Relação Sinal Ruído = "+psnr);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , ip + "/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("urlHistograma" , ip + "/imagens/" + codeImagemOriginal + "/" + file.getName() + "_histogram");
                        jsonObject.put("urlFdp" , ip + "/imagens/" + codeImagemOriginal + "/" + file.getName() + "_fdp");
                        jsonObject.put("mse" , mse);
                        jsonObject.put("psnr" , psnr);
                        jsonObject.put("nomeTransformacao", "Negativo Digital");
                        jsonObject.put("descricaoTransformacao", "O negativo digital ...");

                        jsonArray.put(jsonObject);
                    }
                    else if(nome.equals("Equalização de Histograma")){
                        System.out.println("Equalização");
                        new HistogramEqualization().filter(params);
                        lastUsed += "_histogram";

                        File file = new File(imageFile+lastUsed);

                        int[] h = Filter.getHistogram(file.getAbsolutePath());

                        Filter.plotFDP(h, file.getAbsolutePath());

                        double mse = Filter.MSE(params.get(0), file.getAbsolutePath());
                        System.out.println("Erro médio quadrático = "+mse);

                        //double mseH = Filter.MSE(Filter.getHistogram(params.get(0)), Filter.getHistogram(params.get(1)));
                        //System.out.println("Erro médio quadrático H = "+mseH);

                        double psnr = Filter.PSNR(params.get(0), file.getAbsolutePath());
                        System.out.println("Pico Relação Sinal Ruído = "+psnr);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , ip + "/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "Equalização de Histrograma");
                        jsonObject.put("descricaoTransformacao", "O histograma equalizado ...");

                        h = Filter.getHistogram(imageFile + lastUsed);
                        try {
                            jsonObject.put("histogramaEqualizado", Arrays.toString(h));
                            jsonArray.put(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        jsonArray.put(jsonObject);
                    }
                    else if(nome.equals("Laplace")){
                        System.out.println("Laplace");
                        new Laplace().filter(params);
                        lastUsed += "_laplace_mask " + params.get(1)+"x"+params.get(2);

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , ip + "/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "Laplace");
                        jsonObject.put("descricaoTransformacao", "A transformação de Laplace ...");
                        jsonArray.put(jsonObject);
                    }
                    else if(nome.equals("Logarítmica")){
                        System.out.println("Logarítmica");
                        params.set(1, object.getString("c"));
                        new Logarithmic().filter(params);
                        lastUsed += "_logarithmic";

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , ip + "/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "Logarítmica");
                        jsonObject.put("descricaoTransformacao", "A transformação Logarítmica ...");
                        jsonArray.put(jsonObject);
                    }
                    else if(nome.equals("Potência")){
                        System.out.println("Potencia");
                        params.set(1, object.getString("c"));
                        params.set(2, object.getString("gama"));
                        new Potency().filter(params);
                        lastUsed += "_potency";

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , ip + "/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "Potência");
                        jsonObject.put("descricaoTransformacao", "A transformação de Potência ...");
                        jsonArray.put(jsonObject);
                    }
                    else if(nome.equals("Mediana")){
                        System.out.println("Mediana");
                        new Median().filter(params);
                        lastUsed += "_median";

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , ip + "/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "Mediana");
                        jsonObject.put("descricaoTransformacao", "A transformação de Mediana ...");
                        jsonArray.put(jsonObject);
                    }
                    else if(nome.equals("MIN")){
                        System.out.println("MIN");
                        new Min().filter(params);
                        lastUsed += "_min";

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , ip + "/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "MIN");
                        jsonObject.put("descricaoTransformacao", "A transformação de MIN ...");
                        jsonArray.put(jsonObject);
                    }
                    else if(nome.equals("MAX")){
                        System.out.println("MAX");
                        new Max().filter(params);
                        lastUsed += "_max";

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , ip + "/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "MAX");
                        jsonObject.put("descricaoTransformacao", "A transformação de MAX ...");
                        jsonArray.put(jsonObject);
                    }else if(nome.equals("Soma")){
                        System.out.println("Soma");

                        String image1 = object.getString("imagem2");
                        Filter.toGray(image1);

                        String image2 = imageFile+lastUsed;
                        params.set(0, object.getString("imagem2") + "_grayscale");
                        params.set(1, imageFile+lastUsed);
                        System.out.println(params);
                        new SumImage().filter(params);
                        String[] aux = params.get(0).split("/");
                        String nomeAux = aux[aux.length - 1];
                        lastUsed += "_sum " + nomeAux;

                        File file = new File(imageFile+lastUsed);
                        File fileimage1 = new File(image1);
                        File fileimage2 = new File(image2);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("urlImagem1" , ip + "/imagens/" + codeImagemOriginal + "/" + fileimage1.getName());
                        jsonObject.put("urlImagem2" , ip + "/imagens/" + codeImagemOriginal + "/" + fileimage2.getName());
                        jsonObject.put("url" , ip + "/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "Soma");
                        jsonObject.put("descricaoTransformacao", "A transformação de Soma ...");
                        jsonArray.put(jsonObject);
                    }else if(nome.equals("Subtração")){
                        System.out.println("Subtração");
                        String image1 = object.getString("imagem2");
                        Filter.toGray(image1);

                        String image2 = imageFile+lastUsed;
                        params.set(0, object.getString("imagem2") + "_grayscale");
                        params.set(1, imageFile+lastUsed);
                        System.out.println(params);
                        new SubtractImage().filter(params);
                        String[] aux = params.get(0).split("/");
                        String nomeAux = aux[aux.length - 1];
                        lastUsed += "_subtract "+ nomeAux;

                        File file = new File(imageFile+lastUsed);
                        File fileimage1 = new File(image1);
                        File fileimage2 = new File(image2);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("urlImagem1" , ip + "/imagens/" + codeImagemOriginal + "/" + fileimage1.getName());
                        jsonObject.put("urlImagem2" , ip + "/imagens/" + codeImagemOriginal + "/" + fileimage2.getName());
                        jsonObject.put("url" , ip + "/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "Subtração");
                        jsonObject.put("descricaoTransformacao", "A transformação de Subtração ...");
                        jsonArray.put(jsonObject);
                    }else if(nome.equals("Tresholding")){
                        System.out.println("Tresholding");
                        params.set(1, object.getString("limiar"));
                        new Tresholding().filter(params);
                        lastUsed += "_tresholding";

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , ip + "/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "Tresholding");
                        jsonObject.put("descricaoTransformacao", "A transformação de Tresholding ...");
                        jsonArray.put(jsonObject);
                    }else if(nome.equals("Gaussiano")){
                        System.out.println("Gaussiano");
                        params.set(1, object.getString("gauss"));
                        new Gaussian().filter(params);
                        int tam = Integer.parseInt(object.getString("gauss"));
                        tam++;
                        lastUsed += "_gauss_mask " + tam+ "x" + tam;

                        File file = new File(imageFile+lastUsed);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , ip + "/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("nomeTransformacao", "Gaussiano");
                        jsonObject.put("descricaoTransformacao", "A transformação Gaussiana ...");
                        jsonArray.put(jsonObject);
                    }else if(nome.equals("Máscara Genérica")){
                        System.out.println("Máscara Genérica");
//                        String[] args2 = {imageFile+lastUsed, "3", "3", "lowpass", "1", "1", "1", "1", "1", "1", "1", "1", "1"};

                        List<String> params2 = new ArrayList<String >();
                        params2.add(0, imageFile+"_grayscale");



                        int tam = object.getInt("dimensoesDaMascara");
                        System.out.println(object.get("valoresDaMascara"));
                        params2.add(1, ""+tam);
                        params2.add(2, ""+tam);
                        params2.add(3, "lowpass");

                        String s = object.get("valoresDaMascara").toString();
                        s=s.replace("[","");//replacing all [ to ""
                        s=s.substring(0,s.length()-2);//ignoring last two ]]
                        String s1[]=s.split("],");//separating all by "],"

                        String my_matrics[][] = new String[s1.length][s1.length];//declaring two dimensional matrix for input

                        int pos = 4;
                        boolean temNegativo = false;
                        for(int j=0;j<s1.length;j++){
                            s1[j]=s1[j].trim();//ignoring all extra space if the string s1[i] has
                            String single_int[]=s1[j].split(",");//separating integers by ", "

                            for(int k=0;k<single_int.length;k++){
                                my_matrics[j][k]=single_int[k];//adding single values
                                if(temNegativo == false ){
                                    if(Integer.parseInt(my_matrics[j][k]) < 0){
                                        temNegativo = true;
                                    }
                                }
                                params2.add(pos++, ""+my_matrics[j][k]);
                            }
                        }

                        String type = "lowpass";
                        if(temNegativo == true){
                            params2.remove(3);
                            params2.add(3, "highpass");
                            type = "highpass";
                        }


                        System.out.println(params2);
                        new GenericMask().filter(params2);

                        lastUsed += "_" + type + "_mask " + tam+ "x" + tam;

                        File file = new File(imageFile+lastUsed);

                        int[] h = Filter.getHistogram(file.getAbsolutePath());

                        Filter.plotHistogram(h, file.getAbsolutePath());
                        Filter.plotFDP(h, file.getAbsolutePath());

                        double mse = Filter.MSE(params.get(0), file.getAbsolutePath());
                        System.out.println("Erro médio quadrático = "+mse);

                        //double mseH = Filter.MSE(Filter.getHistogram(params.get(0)), Filter.getHistogram(params.get(1)));
                        //System.out.println("Erro médio quadrático H = "+mseH);

                        double psnr = Filter.PSNR(params.get(0), file.getAbsolutePath());
                        System.out.println("Pico Relação Sinal Ruído = "+psnr);

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id" , i);
                        jsonObject.put("fileName", file.getName());
                        jsonObject.put("url" , ip + "/imagens/" + codeImagemOriginal + "/" + file.getName());
                        jsonObject.put("urlHistograma" , ip + "/imagens/" + codeImagemOriginal + "/" + file.getName() + "_histogram");
                        jsonObject.put("mse" , mse);
                        jsonObject.put("psnr" , psnr);
                        jsonObject.put("nomeTransformacao", "Máscara Genérica");
                        jsonObject.put("descricaoTransformacao", "A Máscara Genérica...");
                        jsonArray.put(jsonObject);
                    }


//                    new GenericMask().filter(params);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        listOfFiles = folderImage.listFiles();

        if (folderImage.exists()) {
            try {
                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile()) {
                        String fileName = listOfFiles[i].getName();
                        if(fileName.equals(nomeImagem + "_grayscale")){
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("id" , i);
                            jsonObject.put("fileName", fileName);
                            jsonObject.put("url" , ip + "/imagens/" + codeImagemOriginal + "/" + listOfFiles[i].getName());
                            jsonObject.put("nomeTransformacao", "Tons de Cinza");
                            jsonObject.put("descricaoTransformacao", "A imagem em tons de cinza ...");
                            jsonArray.put(jsonObject);
                        }
                        else if(fileName.equals(nomeImagem)){
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("id" , i);
                            jsonObject.put("fileName", fileName);
                            jsonObject.put("url" , ip + "/imagens/" + codeImagemOriginal + "/" + listOfFiles[i].getName());
                            jsonObject.put("nomeTransformacao", "Imagem Original");
                            jsonObject.put("descricaoTransformacao", "A imagem original ...");
                            jsonArray.put(jsonObject);
                        }
                    } else if (listOfFiles[i].isDirectory()) {
                        System.out.println("Directory " + listOfFiles[i].getName());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            int[] h = Filter.getHistogram(imageFile);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("histogramaImagemOriginal", Arrays.toString(h));
            jsonArray.put(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(jsonArray.toString());

    }

}

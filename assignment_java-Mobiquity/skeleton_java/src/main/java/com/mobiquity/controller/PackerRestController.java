package com.mobiquity.controller;


import com.mobiquity.exception.APIException;
import com.mobiquity.packer.Packer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;


@RestController
@RequestMapping("/api")
public class PackerRestController {

    public static int theweightLimit = 0;
    @Value("${upload.directory}")
    private String uploadDirectory;

    //api to set weightLimit of which the package to be sent
    @PostMapping("/packages")
    public String getWeightLimit(@RequestParam int weightLimit){
        theweightLimit = weightLimit;
        return "Weight Limit submitted successfully which is:"+weightLimit;
    }

    //api to get package with different things and given constraints
    @PostMapping(value="/call-static-api")
    public ResponseEntity<String> callStaticApi(@RequestPart("file") MultipartFile file) throws APIException {

        try {
            String fileName = file.getOriginalFilename();
            Path filePath = Path.of(uploadDirectory, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            String fileLoc = filePath.toAbsolutePath().toString();
            //static api method call
            String res = Packer.pack(fileLoc);
            return ResponseEntity.ok(res);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}


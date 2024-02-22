package hei.school.sarisary.endpoint.rest.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;


import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import hei.school.sarisary.PojaGenerated;
import hei.school.sarisary.file.BucketComponent;
import lombok.AllArgsConstructor;

@PojaGenerated
@RestController
@AllArgsConstructor
public class SaryController {
    
    BucketComponent bucketComponent;

    @PutMapping(value = "/black-and-white/{id}")
    public BodyBuilder transformImage(
        @PathVariable String id,
        @RequestPart("file") MultipartFile file
    ) throws IOException {
        File fileToUpload = convert(file);
        bucketComponent.upload(fileToUpload, id);

        File fileDowloaded = bucketComponent.download(id);
        return ResponseEntity.ok();
    }

    @GetMapping(value = "/black-and-white/{id}")
    public String getOriginalAndTransformImageUrl(
        @PathVariable String id
    ) {
        return "List";
    }

    private File convert(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
        return file;
    }
}

package hei.school.sarisary.endpoint.rest.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.time.Duration;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import hei.school.sarisary.PojaGenerated;
import hei.school.sarisary.file.BucketComponent;
import lombok.AllArgsConstructor;

@PojaGenerated
@RestController
@AllArgsConstructor
public class SaryController {
    
    BucketComponent bucketComponent;

    private static final String SARI_KEY = "sarisary/";

    @PutMapping(value = "/black-and-white/{id}")
    public ResponseEntity<?> transformImage(
        @PathVariable String id,
        @RequestBody byte[] imageData
    ) {
        try {
            File fileToUpload = convertToTempFile(imageData);

            bucketComponent.upload(fileToUpload, SARI_KEY + id + ".png");
            
            // Convert image to black anf white
            File fileTransformed = fileToUpload;
            bucketComponent.upload(fileTransformed, SARI_KEY + "B&N/" + id + ".png");
            
            return ResponseEntity.ok().body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du traitement de l'image: " + e.getMessage());
        }
    }

    @GetMapping(value = "/black-and-white/{id}")
    public ListUrl getOriginalAndTransformImageUrl(
        @PathVariable String id
    ) {
        ListUrl listUrl = new ListUrl(
            bucketComponent.presign(SARI_KEY + id + ".png", Duration.ofMinutes(4)).toString(),
            bucketComponent.presign(SARI_KEY + "B&N/" + id + ".png", Duration.ofMinutes(4)).toString());
        return listUrl;
    }

    private File convertToTempFile(byte[] imageData) throws IOException {
        File tempFile = File.createTempFile("temp-image", ".png");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(imageData);
        }
        return tempFile;
    }
}

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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

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
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            File fileToUpload = convertToTempFile(imageData);
            bucketComponent.upload(fileToUpload, id);

            BufferedImage bwImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            bwImage.getGraphics().drawImage(image, 0, 0, null);
            
            File output = new File(SARI_KEY + id + ".png");
            ImageIO.write(bwImage, "png", output);
            bucketComponent.upload(output, id);
            
            return ResponseEntity.ok().body(null);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors du traitement de l'image: " + e.getMessage());
        }
    }

    @GetMapping(value = "/black-and-white/{id}")
    public String getOriginalAndTransformImageUrl(
        @PathVariable String id
    ) {
        ;
        return bucketComponent.presign(id, Duration.ofMinutes(4)).toString();
    }

    private File convertToTempFile(byte[] imageData) throws IOException {
        File tempFile = File.createTempFile("temp-image", ".png");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(imageData);
        }
        return tempFile;
    }
}

package hei.school.sarisary.endpoint.rest.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;
import javax.imageio.ImageIO;
import java.awt.Color;

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

            BufferedImage originalImage = ImageIO.read(fileToUpload);
            
            // Convert image to black anf white
            BufferedImage blackAndWhiteImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
            for (int x = 0; x < originalImage.getWidth(); x++) {
                for (int y = 0; y < originalImage.getHeight(); y++) {
                    Color color = new Color(originalImage.getRGB(x, y));
                    int grayscale = (int) (0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue());
                    Color newColor = new Color(grayscale, grayscale, grayscale);
                    blackAndWhiteImage.setRGB(x, y, newColor.getRGB());
                }
            }

            File fileTransformed = new File(id);
            ImageIO.write(blackAndWhiteImage, "png", fileTransformed);
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

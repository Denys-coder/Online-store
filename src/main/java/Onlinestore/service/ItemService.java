package Onlinestore.service;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

@Service
public class ItemService {
    private final Environment environment;

    public ItemService(Environment environment) {
        this.environment = environment;
    }

    public String saveLogoToFolder(int id, MultipartFile logo) {
        String logoName = "logo" + id;

        String logosDirectory = environment.getProperty("item.logos.directory");
        File logoFile = new File(logosDirectory + logoName);
        try (OutputStream os = new FileOutputStream(logoFile)) {
            os.write(logo.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return logoName;
    }

    public Set<String> saveImagesToFolder(int id, MultipartFile[] images) {
        Set<String> imageNames = new HashSet<>();

        String imageTemplateName = "image" + id + ".";
        String itemsDirectory = environment.getProperty("item.images.directory");

        for (int i = 0; i < images.length; i++) {
            String localImageName = imageTemplateName + (i + 1);
            String fullImageName = itemsDirectory + localImageName;
            File imageFile = new File(fullImageName);
            try (OutputStream os = new FileOutputStream(imageFile)) {
                os.write(images[i].getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageNames.add(localImageName);
        }

        return imageNames;
    }

    public void deleteLogoFromFolder(int id) {
        String logoName = "logo" + id;
        String logosDirectory = environment.getProperty("item.logos.directory");
        File logoFile = new File(logosDirectory + logoName);
        logoFile.delete();
    }

    public void deleteImagesFromFolder(int id) {
        String imagesDirectory = environment.getProperty("item.images.directory");
        File[] files = new File(imagesDirectory).listFiles();

        for (File file : files) {
            if (file.getName().startsWith("image" + id)) {
                file.delete();
            }
        }
    }
}

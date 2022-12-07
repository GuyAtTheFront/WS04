package myapp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Cookie {
    private String BASE_DIR = "src/myapp/";
    private String fname = "cookie_file.txt";

    public Cookie(String fname) {
        this.fname = fname;
    }

    public String getRandomCookie() {
        setupDirectory();

        Path path = Paths.get(BASE_DIR).resolve(fname);
        if (!path.toFile().exists()) {
            System.out.println("File does not exist");
            return null;
        }

        List<String> contents = null;
        try {
            contents = Files.readAllLines(path);
        } catch (IOException e) {}
        int num = ThreadLocalRandom.current().nextInt(0, contents.size());
        return contents.get(num);

        // read file
        // random cookie
        // close file
    }

    private void setupDirectory() {
        Path path = Paths.get(BASE_DIR);
        if (!path.toFile().exists()) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {};
        }
    }


}
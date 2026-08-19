package com.example.imageuploader.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ImageUploadController {
    private final Path uploadDir = Paths.get("uploads");

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("image") MultipartFile image, Model model) {
        if (image.isEmpty()) {
            model.addAttribute("error", "Please select an image.");
            return "index";
        }

        String contentType = image.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            model.addAttribute("error", "Only image files are allowed.");
            return "index";
        }

        try {
            Files.createDirectories(uploadDir);
            String original = Paths.get(image.getOriginalFilename() == null ? "image" : image.getOriginalFilename())
                    .getFileName().toString();
            String filename = UUID.randomUUID() + "-" + original;
            Files.copy(image.getInputStream(), uploadDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            model.addAttribute("message", "Image uploaded successfully: " + filename);
        } catch (IOException e) {
            model.addAttribute("error", "Upload failed. Please try again.");
        }
        return "index";
    }
}

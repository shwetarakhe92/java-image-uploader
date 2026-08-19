# Java Image Uploader

A simple web-based image uploader built with Java, Spring Boot, Maven, Thymeleaf, HTML and CSS.

## Features

- Upload images from a browser
- Accepts image/* files only
- Generates a unique filename
- Stores images in the local `uploads/` directory
- Maximum upload size: 10 MB
- Simple success/error messages

## Requirements

- Java 17+
- Maven 3.9+

## Run locally

```bash
mvn spring-boot:run
```

Open http://localhost:8080 in your browser.

## Build

```bash
mvn clean package
java -jar target/image-uploader-0.0.1-SNAPSHOT.jar
```

Uploaded images are stored in `uploads/`. The directory is ignored by Git so uploaded files are not committed to the repository.

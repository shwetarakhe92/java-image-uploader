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

## Jenkins CI/CD to AWS EC2

The repository contains a `Jenkinsfile` that performs:

1. Checkout from GitHub
2. Maven build
3. JAR artifact archiving
4. SSH deployment to EC2
5. Creation/update of a `systemd` service
6. Service restart and HTTP health check on port 8080

### Jenkins requirements

Install/configure a JDK 17 installation and Maven on the Jenkins agent. Jenkins also needs the SSH Agent plugin and an SSH private-key credential for the EC2 instance. The pipeline expects the credential ID `ec2-ssh-key` by default.

Create a Pipeline job using **Pipeline script from SCM**, select Git, and use:

```text
https://github.com/shwetarakhe92/java-image-uploader.git
```

Set the branch to `main` and the script path to `Jenkinsfile`.

When starting the job, provide the EC2 public IPv4 address/DNS name in `EC2_HOST` and keep `EC2_USER=ec2-user` for a standard Amazon Linux EC2 instance.

### EC2 preparation

For Amazon Linux 2023, Java 17 is provided through Amazon Corretto. Install the JDK with:

```bash
sudo yum install -y java-17-amazon-corretto-devel
java -version
```

The Jenkins deployment creates and manages `/etc/systemd/system/image-uploader.service` and runs the application as the SSH user.

### Security group

Allow inbound TCP 22 from your Jenkins host/network for SSH deployment, and inbound TCP 8080 from the clients that need to access the application. Do not expose SSH to the entire internet in production.

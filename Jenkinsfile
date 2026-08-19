pipeline {
    agent any

    parameters {
        string(name: 'EC2_HOST', defaultValue: '', description: 'Private IPv4 address or DNS name of the application EC2 instance')
        string(name: 'EC2_USER', defaultValue: 'ec2-user', description: 'SSH user for the application EC2 instance')
        string(name: 'SSH_CREDENTIAL_ID', defaultValue: 'ec2-ssh-key', description: 'Jenkins SSH private-key credential ID')
    }

    environment {
        APP_NAME = 'image-uploader'
        DEPLOY_DIR = '/opt/image-uploader'
        JAR_NAME = 'image-uploader.jar'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }

        stage('Archive Artifact') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy to EC2') {
            when {
                expression { params.EC2_HOST?.trim() }
            }
            steps {
                sshagent(credentials: [params.SSH_CREDENTIAL_ID]) {
                    sh '''
                        set -eu
                        JAR=$(find target -maxdepth 1 -type f -name '*.jar' ! -name '*-plain.jar' | head -n 1)
                        test -n "$JAR"

                        mkdir -p ~/.ssh
                        chmod 700 ~/.ssh
                        ssh-keyscan -H "$EC2_HOST" >> ~/.ssh/known_hosts 2>/dev/null || true

                        ssh -o ConnectTimeout=15 "$EC2_USER@$EC2_HOST" \
                          "sudo mkdir -p $DEPLOY_DIR && sudo chown -R $EC2_USER:$EC2_USER $DEPLOY_DIR"

                        scp "$JAR" "$EC2_USER@$EC2_HOST:$DEPLOY_DIR/$JAR_NAME"

                        ssh "$EC2_USER@$EC2_HOST" \
                          "sudo systemctl restart $APP_NAME && \
                           echo 'Waiting for application to become ready...' && \
                           for i in \$(seq 1 30); do \
                               if curl -fsS http://localhost:8080/ > /dev/null 2>&1; then \
                                   echo 'Application is ready.'; \
                                   exit 0; \
                               fi; \
                               sleep 2; \
                           done; \
                           echo 'Application did not become ready within 60 seconds.'; \
                           sudo systemctl --no-pager --full status $APP_NAME || true; \
                           sudo journalctl -u $APP_NAME -n 80 --no-pager || true; \
                           exit 1"

                        echo 'Deployment verification passed.'
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'CI/CD pipeline completed successfully.'
        }
        failure {
            echo 'Pipeline failed. Review the failed stage in Jenkins Console Output.'
        }
        always {
            sh 'rm -f ~/.ssh/known_hosts 2>/dev/null || true'
        }
    }
}

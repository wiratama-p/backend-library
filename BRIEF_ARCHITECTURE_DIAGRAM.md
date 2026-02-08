# Architecture & Deployment Plan

## Current State

1. Developer pushes code to GitHub.
2. GitHub Actions triggers the CI workflow.
   - On any branch: runs tests.
   - On main / release/* branches: runs tests, then builds and pushes Docker image to GHCR.
3. Docker image is stored in GitHub Container Registry (ghcr.io), tagged with `latest` and the commit SHA.

## Cloud Deployment Plan

### Request Flow

1. Client (browser / mobile) sends a request.
2. Cloud load balancer receives the request and terminates HTTPS.
3. Load balancer forwards the request to a backend-library container (port 8080).
4. The container queries a managed PostgreSQL database.
5. Response is returned to the client.

### Components

1. **Container Registry**
   - GitHub Container Registry (ghcr.io) stores the Docker images.
   - Images are tagged with `latest` and the commit SHA for traceability.

2. **Container Runtime (pick one based on cloud provider)**
   - AWS / Azure / GCP
   - The runtime pulls the image from GHCR and runs it.
   - Scales horizontally as needed (configure HPA etc)

3. **Database**
   - Replace H2 (in-memory) with a managed PostgreSQL instance.
   - Use environment variables to inject the database URL, username, and password.

4. **Load Balancer**
   - Provided by the cloud platform (usually bundled with the container runtime).
   - Handles HTTPS termination and distributes traffic across container instances.

5. **Environment Configuration**
   - Database credentials are stored in the cloud provider's secret manager.
   - Injected as environment variables into the container at runtime.
   - Spring Boot reads them via `application-prod.properties` or environment variable overrides.

### CI/CD Flow (end to end)

1. Developer pushes code to a feature branch.
2. GitHub Actions runs tests.
3. Developer creates a PR to main.
4. PR is reviewed and merged to main.
5. GitHub Actions runs tests, then builds Docker image.
6. Image is pushed to GHCR (tagged latest + commit SHA).
7. Cloud runtime pulls the new image and deploys (either via GitHub Actions deploy step or cloud-native CD).

### What Needs to Change Before Production

1. **Database**
   - Switch from H2 to PostgreSQL.
   - Add `application-prod.properties` with database URL, username, password injected via environment variables.
   - Set `spring.jpa.hibernate.ddl-auto=validate`.

2. **Database Migrations**
   - Add Flyway or Liquibase to manage schema changes instead of relying on `ddl-auto`.

3. **CI/CD Deploy Step**
   - Add a deploy job in the GitHub Actions workflow that triggers after the Docker image is pushed.
   - This depends on the chosen cloud provider.

4. **Health Checks**
   - Add Spring Boot Actuator for `/actuator/health` endpoint.
   - The cloud runtime uses this to determine if the container is ready to receive traffic.

5. **Logging**
   - Configure structured JSON logging for production.
   - Cloud logging services (CloudWatch, Cloud Logging, etc.) can parse and index JSON logs.


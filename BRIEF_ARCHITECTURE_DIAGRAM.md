# Architecture & Deployment Plan

## Current State (Local Development)

### Frontend (UI)
1. Angular application running on `http://localhost:4200`.
2. Makes API calls to the backend.

### Backend
1. Spring Boot application running on `http://localhost:8080`.
2. CORS configured to allow requests from `http://localhost:4200`.
3. Uses H2 in-memory database.

### CI/CD Pipeline
1. Developer pushes code to GitHub.
2. GitHub Actions triggers the CI workflow.
   - On any branch: runs tests.
   - On main / release/* branches: runs tests, then builds and pushes Docker image to GHCR.
3. Docker image is stored in GitHub Container Registry (ghcr.io), tagged with `latest` and the commit SHA.

## Cloud Deployment Plan

### Request Flow

1. User opens browser and navigates to the frontend URL.
2. Cloud CDN / Static hosting serves the frontend (HTML, CSS, JS).
3. Frontend JavaScript makes API calls to the backend API.
4. Cloud load balancer receives the API request and terminates HTTPS.
5. Load balancer forwards the request to a backend-library container (port 8080).
6. The container queries a managed PostgreSQL database.
7. Response is returned through the chain back to the user.

### Components

1. **Frontend Hosting**
   - Static files (HTML, CSS, JS) are hosted on:
     - AWS: S3 + CloudFront
     - Azure: Azure Storage + CDN
     - GCP: Cloud Storage + Cloud CDN
   - Deployed automatically via GitHub Actions on merge to main.

2. **Backend Container Registry**
   - GitHub Container Registry (ghcr.io) stores the Docker images.
   - Images are tagged with `latest` and the commit SHA for traceability.

3. **Backend Container Runtime (pick one based on cloud provider)**
   - Google Cloud: Cloud Run
   - AWS: ECS Fargate or EKS
   - Azure: Azure Container Apps or AKS
   - The runtime pulls the image from GHCR and runs it.
   - Scales horizontally as needed, use HPA if needed

4. **Database**
   - Replace H2 (in-memory) with a managed PostgreSQL instance.
   - Google Cloud: Cloud SQL
   - AWS: RDS
   - Azure: Azure Database for PostgreSQL
   - Use environment variables to inject the database URL, username, and password.

5. **Load Balancer / API Gateway**
   - Provided by the cloud platform (usually bundled with the container runtime).
   - Handles HTTPS termination and distributes traffic across backend container instances.
   - Frontend calls this endpoint.

6. **CORS Configuration**
   - Update `WebConfig.java` to allow the production frontend domain instead of `localhost:4200`.
   - Example: `.allowedOrigins("https://yourdomain.com")`

7. **Environment Configuration**
   - Database credentials, API keys, and secrets are stored in the cloud provider's secret manager.
   - Injected as environment variables into the container at runtime.
   - Spring Boot reads them via `application-prod.properties` or environment variable overrides.

### CI/CD Flow (end to end)

**Backend:**
1. Developer pushes backend code to a feature branch.
2. GitHub Actions runs tests.
3. Developer creates a PR to main.
4. PR is reviewed and merged to main.
5. GitHub Actions runs tests, then builds Docker image.
6. Image is pushed to GHCR (tagged latest + commit SHA).
7. Cloud runtime pulls the new image and deploys.

**Frontend:**
1. Developer pushes frontend code to a feature branch.
2. GitHub Actions runs build and lint checks.
3. Developer creates a PR to main.
4. PR is reviewed and merged to main.
5. GitHub Actions builds the frontend (e.g., `ng build --prod`).
6. Static files are uploaded to cloud storage / CDN.

### What Needs to Change Before Production

**Backend:**
1. **Database**
   - Switch from H2 to PostgreSQL.
   - Add `application-prod.properties` with database URL, username, password injected via environment variables.
   - Set `spring.jpa.hibernate.ddl-auto=validate`.

2. **Database Migrations**
   - Add Flyway or Liquibase to manage schema changes instead of relying on `ddl-auto`.

3. **CORS Configuration**
   - Update `WebConfig.java` to allow the production frontend domain.

4. **CI/CD Deploy Step**
   - Add a deploy job in the GitHub Actions workflow that triggers after the Docker image is pushed.

5. **Health Checks**
   - Add Spring Boot Actuator for `/actuator/health` endpoint.

6. **Logging**
   - Configure structured JSON logging for production.

**Frontend:**
1. **Environment Configuration**
   - Create `environment.prod.ts` with the production backend API URL.

2. **CI/CD Deploy Step**
   - Add GitHub Actions workflow to build and deploy static files to cloud storage / CDN.

3. **Custom Domain**
   - Configure a custom domain and SSL certificate for the frontend.
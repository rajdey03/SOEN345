# SOEN345

## Prerequisites

### 1. Java 17 (JDK)

Download and install from: https://adoptium.net/temurin/releases/?version=17

After installing, verify:

```
java -version
```

### 2. Apache Maven

Download from: https://maven.apache.org/download.cgi

**Windows Setup:**

1. Download the **Binary zip archive** (e.g. `apache-maven-3.9.9-bin.zip`)
2. Extract it to `C:\apache-maven-3.9.9`
3. Add Maven to your system PATH:
   - Open **Settings** → search **"Environment Variables"**
   - Under **System variables**, select `Path` → click **Edit**
   - Click **New** → add `C:\apache-maven-3.9.9\bin`
   - Click **OK** on all dialogs
4. **Close and reopen** any terminal / VS Code
5. Verify:

```
mvn -version
```

> **Note:** If `mvn` works in Command Prompt but not in VS Code, either
> restart VS Code or switch the VS Code terminal to "Command Prompt"
> instead of PowerShell (click the dropdown arrow next to the `+` icon).

### 3. Spring Boot

No separate install needed — Maven downloads Spring Boot automatically
when you run the project. It's declared as a dependency in `pom.xml`.

---

## Repository Structure

```
SOEN345-main/
├── .github/
│   └── workflows/
│       └── ci-cd.yml              ← GitHub Actions CI/CD pipeline
├── backend/                       ← Spring Boot backend
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/com/example/ticketreservation/
│           │   ├── TicketReservationApplication.java
│           │   ├── config/
│           │   │   └── SecurityConfig.java
│           │   ├── controller/
│           │   │   └── RegistrationController.java
│           │   ├── dto/
│           │   │   ├── LoginRequest.java
│           │   │   ├── LoginResponse.java
│           │   │   ├── RegistrationRequest.java
│           │   │   └── RegistrationResponse.java
│           │   ├── exception/
│           │   │   ├── GlobalExceptionHandler.java
│           │   │   ├── InvalidRegistrationException.java
│           │   │   └── UserAlreadyExistsException.java
│           │   ├── model/
│           │   │   ├── User.java
│           │   │   └── UserRole.java
│           │   ├── repository/
│           │   │   └── UserRepository.java
│           │   └── service/
│           │       ├── NotificationService.java
│           │       ├── NotificationServiceImpl.java
│           │       └── RegistrationService.java
│           └── resources/
│               └── application.properties
├── app/                           ← existing Android app
├── build.gradle.kts
├── gradlew
└── settings.gradle.kts
```

---
## How to Run the Frontend

Open the project folder in **Android Studio**, wait for the initial Gradle sync to complete, and start the app:

```text
1. Open the Device Manager and launch an Android Emulator.
2. Click the green "Run 'app'" button in the top toolbar (or Shift + F10).
```
## How to Run the Backend

```bash
cd backend
mvn spring-boot:run
```

The first run will take a minute or two as Maven downloads all
dependencies. After that you'll see:

```
Started TicketReservationApplication in X seconds
```

The server is now running on **http://localhost:8080**

### Email Confirmation Setup

Registration confirmation emails are supported, but real delivery only
works if the machine running the backend has a valid SendGrid API key.

Before starting the backend, set:

```bash
export SENDGRID_API_KEY=your_real_sendgrid_api_key
```

Then run:

```bash
cd backend
mvn spring-boot:run
```

Important notes:

- If `SENDGRID_API_KEY` is missing, registration can still succeed, but
  email delivery will fail at runtime.
- The configured sender in
  `backend/src/main/resources/application.properties` must be a verified
  sender in SendGrid.
- Team members can all use this feature, but only if they run the
  backend with access to a valid SendGrid API key.
- GitHub Actions and local tests do **not** send real emails. Tests use
  mocked mail dependencies so CI can pass without SMTP credentials.

### Team Readiness

The implementation is 
only ready for real email delivery if the team also shares a valid
runtime setup:

- Java 17 and Maven installed
- The latest merged code pulled locally
- A valid `SENDGRID_API_KEY` configured in the environment
- A verified SendGrid sender address matching `spring.mail.from`

Without those, teammates can still register and log in locally, but they
should not expect a real confirmation email to arrive.

---

## Verify It Works

Open your browser and go to:

```
http://localhost:8080/api/health
```

You should see: **OK**

---

## API Endpoints

| Method | URL           | Description            |
| ------ | ------------- | ---------------------- |
| POST   | /api/register | Register a new user    |
| POST   | /api/login    | Login with email/phone |
| GET    | /api/health   | Health check           |

### Example Registration (curl)

```bash
curl -X POST http://localhost:8080/api/register -H "Content-Type: application/json" -d "{\"first_name\":\"John\",\"last_name\":\"Doe\",\"email\":\"john@example.com\",\"password\":\"test123\"}"
```

If mail is configured correctly, the registered email address should
receive a confirmation email after this request succeeds.

### Example Login (curl)

```bash
curl -X POST http://localhost:8080/api/login -H "Content-Type: application/json" -d "{\"user_id\":\"john@example.com\",\"password\":\"test123\"}"
```

---

## H2 Database Console

You can view stored data in the browser at:

```
http://localhost:8080/h2-console
```

- **JDBC URL:** `jdbc:h2:mem:ticketreservationdb`
- **Username:** `sa`
- **Password:** _(leave blank)_

---

## How It Connects to the Android App

The Android app sends requests to `http://10.0.2.2:8080`, which is the
Android emulator's alias for `localhost`. So when the backend runs on
your machine on port 8080, the emulator reaches it automatically.

| Android App Sends   | Backend Receives                |
| ------------------- | ------------------------------- |
| `"first_name"`      | `@JsonProperty("first_name")`   |
| `"last_name"`       | `@JsonProperty("last_name")`    |
| `"email"`           | `@JsonProperty("email")`        |
| `"phone_number"`    | `@JsonProperty("phone_number")` |
| `"password"`        | `@JsonProperty("password")`     |
| `"user_id"` (login) | `@JsonProperty("user_id")`      |

---

## CI/CD Pipeline

The GitHub Actions workflow (`.github/workflows/ci-cd.yml`) triggers on
every push to `main` and on pull requests to `main`. It:

1. Builds the Spring Boot backend with Maven
2. Runs JUnit 5 tests (once added)
3. Builds the Android app (debug)
4. Runs Android unit tests
5. Uploads test results as artifacts

### Run Backend Tests Locally

From the `backend` folder:

```bash
mvn test
```

To run only the notification email unit test:

```bash
mvn test -Dtest=NotificationServiceImplTest
```

---

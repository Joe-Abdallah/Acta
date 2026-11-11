# JavaFX Client Application

A JavaFX desktop application that connects to the Spring Boot REST API for Acta

## Features

- **Communities Management**: Create, update, and view communities
- **Posts Management**: Create, update, delete, and view posts
- **Comments Management**: Create, update, delete, and view comments
- **Relationship Management**: Assign posts to communities
- **Real-time Updates**: Refresh data from the API

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Spring Boot backend running on `http://localhost:8080`

## Running the Application

### Option 1: Using Maven

```bash
cd javafx-client
mvn clean javafx:run
```

### Option 2: Using Java directly

First, compile the project:
```bash
cd javafx-client
mvn clean compile
```

Then run:
```bash
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp target/classes:target/dependency/* com.csis231.javafxclient.JavaFXClientApp
```

## Project Structure

```
javafx-client/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/csis231/javafxclient/
│       │       ├── controller/      # FXML controllers
│       │       ├── model/           # DTOs matching backend
│       │       ├── service/          # API client
│       │       └── JavaFXClientApp.java
│       └── resources/
│           └── fxml/                 # FXML view files
└── pom.xml
```

## Usage

1. **Start the Spring Boot backend** first (on port 8080)
2. **Launch the JavaFX client**
3. Use the **Communities** tab to manage communities
4. Use the **Posts** tab to manage posts
5. Use the **Comments** tab to manage comments
6. Select a post to see its comments
6. Assign posts to communities using the community dropdown in the posts tab

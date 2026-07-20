# Warwick+ Movie Database Application

A Java desktop application for storing, searching, and querying movie information using **custom-built data structures**. The application loads film metadata, cast, crew, and user ratings from external datasets, allowing users to efficiently explore movie records through a graphical user interface.

Unlike typical Java applications that rely on the `java.util` collection framework, this project deliberately implements its own dynamic arrays, nodes, and supporting data structures to demonstrate a strong understanding of data structures, algorithms, memory management, and object-oriented software design.

---

## Features

### 🎬 Movie Database

- Load and manage movie metadata from external datasets.
- Browse detailed information for films, cast members, crew, and ratings.
- Efficient searching and retrieval of movie records.
- Designed to support datasets containing thousands of records.

### 🏗 Custom Data Structures

Instead of using Java's built-in collection classes, the application implements custom data structures, including:

- Dynamic arrays
- Node-based structures
- Custom storage classes
- Linked object relationships

These structures form the foundation of the application's storage and retrieval system.

### 🖥 Graphical User Interface

- Lightweight Java desktop interface.
- Browse movie information interactively.
- View linked movie, cast, crew, and ratings data.
- Simple and responsive interface for exploring the dataset.

---

## Technologies

- Java 21
- Gradle
- JUnit
- Java Swing (GUI)

---

## Project Structure

```
Warwick_Plus_Movie_Database/
│
├── data/                      # Movie datasets
├── gradle/                    # Gradle wrapper files
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── interfaces/
│   │   │   ├── screen/
│   │   │   ├── stores/
│   │   │   └── structures/
│   │   └── resources/
│   └── test/
│
├── build.gradle
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
├── DSA_Design_Report.txt
├── README.md
└── .gitignore
```

---

## Application Architecture

The application follows a modular architecture separating the user interface, data storage, and underlying data structures.

```
Movie Dataset Files
         │
         ▼
 Data Loading Layer
         │
         ▼
Custom Data Structures
         │
         ▼
 Storage Classes
         │
         ▼
 Graphical User Interface
```

This separation of responsibilities improves maintainability while allowing each component to be developed and tested independently.

---

## Design Principles

### Custom Data Structures

A primary objective of this project was to implement fundamental data structures manually rather than relying on Java's standard library collections.

This demonstrates understanding of:

- Dynamic memory allocation
- Data organisation
- Object relationships
- Algorithm implementation
- Performance analysis

### Modular Design

The application separates responsibilities across multiple packages, improving readability and maintainability.

- **interfaces** — shared interfaces
- **screen** — graphical user interface
- **stores** — data storage and retrieval
- **structures** — custom data structure implementations

---

## Design Documentation

A detailed explanation of the project's design decisions is available in:

**DSA_Design_Report.txt**

The report discusses:

- Design rationale behind the custom data structures
- Implementation decisions
- Algorithm design
- Time complexity (Big-O) analysis
- Space complexity considerations
- Dataset organisation
- Storage and retrieval strategies

---

## Building and Running

### Requirements

- Java 21 or later

### Clone the Repository

```bash
git clone <repository-url>
cd Warwick_Plus_Movie_Database
```

### Run the Application

#### macOS / Linux

```bash
./gradlew run
```

#### Windows

```bash
gradlew.bat run
```

Gradle will automatically download any required dependencies before launching the application.

---

## Testing

Run the project's automated tests using:

### macOS / Linux

```bash
./gradlew test
```

### Windows

```bash
gradlew.bat test
```

---

## Learning Outcomes

This project demonstrates practical experience with:

- Object-oriented programming
- Java application development
- Custom data structure implementation
- Algorithm design
- Data modelling
- File parsing
- Modular software architecture
- Desktop GUI development
- Gradle build automation
- Unit testing with JUnit

---

## Future Improvements

Potential future enhancements include:

- Advanced filtering and sorting options
- Additional search functionality
- Improved graphical interface
- Larger dataset support
- Performance benchmarking
- Data export functionality

---

## Licence

This repository is provided for educational and portfolio purposes.

Please do not copy or submit this work as your own for academic assessment.

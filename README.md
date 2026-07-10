# Warwick+ Movie Database Application

Warwick+ is a Java application designed to store, search, and query film dataset records (including metadata, cast, crew, and user ratings) efficiently. 

To demonstrate a strong understanding of computer science fundamentals, this project implements **custom data structures** (such as dynamic arrays and nodes) instead of relying on Java's built-in `java.util` collection libraries.

## Key Features
- **Custom Structures:** Built custom memory-efficient collections to manage and link movies, ratings, and credits.
- **Search & Query:** Supports rapid searching and retrieval across 1,000 film entries (scalable to larger datasets).
- **Interactive UI:** Features a lightweight Java-based graphical interface to inspect and query the data.

## Design Documentation
For an in-depth analysis of the data structures and algorithms used in this project, please refer to the [System Design Report](docs/Data-Structures-Design-Report.pdf). 

This document covers:
- The design decisions behind choosing custom collections (such as dynamic arrays and nodes) over standard library collections.
- Time and space complexity (Big-O) analysis for major operations (insert, search, retrieve).
- Implementation strategies for linking and querying separate movies, ratings, and credits datasets.

## Technologies Used
- Java 21
- Gradle (Build Tool)
- JUnit (Unit Testing)

## How to Build and Run

### Requirements
- Java 21

### Running the Application
To launch the graphical user interface, run the following command in your terminal:
- **Linux/macOS:** `./gradlew run`
- **Windows:** `./gradlew.bat run`

### Running Tests
To run the automated test suite and verify data structure performance:
- **Linux/macOS:** `./gradlew test`
- **Windows:** `./gradlew.bat test`


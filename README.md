# Library Book Management System

A Java Swing desktop application developed for the Open-Ended Lab (OEL).

## Requirements
- JDK 17 or later (developed for Java 27)
- Visual Studio Code or another Java IDE

## Features
- Add books with ID, title, author, and status
- View all books in a table
- Update title, author, and status of an existing book
- Delete books by ID
- Status options: Available, Issued, Reserved
- Validation for empty ID/title/author and duplicate IDs

## Project structure
```text
LibraryManagementSystem/
├── src/
│   ├── Book.java
│   ├── Library.java
│   └── LibraryGUI.java
├── docs/
├── README.md
└── .gitignore
```

## Compile and run (Windows CMD / VS Code terminal)
From the project root:

```cmd
javac -d out src\Book.java src\Library.java src\LibraryGUI.java
java -cp out LibraryGUI
```

## Generate Javadoc
From the project root:

```cmd
javadoc -d docs -sourcepath src src\Book.java src\Library.java src\LibraryGUI.java
```

Open `docs\index.html` in a browser after generation.

## Important note
Books are stored in an ArrayList in memory. They remain available while the application is running, but are not saved after the application is closed. Persistent database/file storage was not specified in the supplied OEL requirements.

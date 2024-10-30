# File Upload and Encryption API

## Overview

This project provides a RESTful API for uploading files securely. 
The files are encrypted before being saved to the database, ensuring that sensitive information is protected. 
The API allows users to upload single or multiple files, retrieve files by email, and rename files as needed.

## Features
- Creation of user.
- Log in
- Upload a single file with encryption.
- Upload multiple files with encryption.
- Retrieve decrypted files using the JWT token files by user email.
- Rename existing files.

## Technologies Used

- Java
- Spring Boot
- JPA (Java Persistence API)
- Hibernate
- MultipartFile for file uploads

## Models
### File Model
| Attribute       | Type      | Column Name    | Description                                 |
|-----------------|-----------|----------------|---------------------------------------------|
| `id`            | `Long`    | `id`           | Primary key; auto-generated ID              |
| `fileName`      | `String`  | `file_name`    | Name of the file                            |
| `fileData`      | `byte[]`  | `file_data`    | Binary data of the file, stored as LONGBLOB |
| `email`         | `String`  | `email`        | Email associated with the file              |

### User Model
Here's an updated README to reflect that the `User` class implements `UserDetails`:

| Attribute   | Type      | Column Name | Description                       |
|-------------|-----------|-------------|-----------------------------------|
| `id`        | `Long`    | `id`        | Primary key; identity generation strategy |
| `email`     | `String`  | `email`     | User's email, must not be null     |
| `password`  | `String`  | `password`  | User's password, must not be null  |

### Class Details

- **Implements**: `UserDetails` — Allows integration with Spring Security, making the `User` class suitable for user authentication and authorization.

## API Endpoints

### 1. Sign up
- **Endpoint**: `/api/v1/user/create`

**Request Body**
```json
{
  "email": "user@example.com",
  "password": "userPassword"
}
```
**Response:**
```json
{
  "status": 200,
  "message": "user created",
  "data": "user@example.com"
}
```


### 2. Login 
- **Endpoint**: `/api/v1/auth/login`

 **Request Body**
```json
{
  "email": "user@example.com",
  "password": "userPassword"
}
```
**Response:**
```json
{
  "status": 200,
  "message": "logged in successfully",
  "data": TOKEN
}
```


### 3. Upload a Single File

**Endpoint:** `POST /api/v1/file/upload`

**Request Body:**
```plaintext
file: MultipartFile (the file to upload)
```

**Response:**
```json
{
  "status": 200,
  "message": "file saved",
  "data": "original_filename.enc"
}
```

### 4. Upload Multiple Files

**Endpoint:** `POST /api/v1/file/upload-multiple`

**Request Body:**
```plaintext
email: String (user's email)
files: List<MultipartFile> (the files to upload)
```

**Response:**
```json
{
  "status": 200,
  "message": "Files saved successfully",
  "data": ["original_filename1.enc", "original_filename2.enc"]
}
```

### 5. Get Files by Email

**Endpoint:** `GET /api/v1/file/user-files`

**Request Parameters:**
```plaintext
email: String (user's email)
```

**Response:**
```json
{
  "status": 200,
  "message": "Filenames retrieved successfully.",
  "data": ["file1.enc", "file2.enc"]
}
```

### 6. Rename a File

**Endpoint:** `PUT /api/v1/file/update-fileName/{id}`

**Request Body:**
```plaintext
fileName: String (new name for the file)
```

**Response:**
```json
{
  "status": 200,
  "message": "File name Updated!!!"
}
```

## File Encryption

Files uploaded through this API are encrypted using a custom encryption method. 
The encryption logic is applied to the file's input stream before saving it to the database, ensuring that the file contents remain confidential.

## Installation and Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   ```

2. Navigate to the project directory:
   ```bash
   cd <project-directory>
   ```

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

5. The API will be available at `http://localhost:1001/api/v1`.

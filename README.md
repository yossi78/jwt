# JWT User Service

A comprehensive Spring Boot application with React frontend that provides JWT-based authentication and CRUD operations for user management.

## Quick Start

### 1. Start MySQL Database

First, start the MySQL database using Docker Compose:

```bash
docker-compose up -d
```

This will:
- Start MySQL 8.0 on port 3306
- Create the database `jwt_user_db`
- Initialize with sample data
- Set up user credentials (username: `john.doe`, password: `password123`)

### 2. Run the Spring Boot Application

Navigate to the project root and run:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 3. Run the React Frontend

Navigate to the frontend directory and install dependencies:

```bash
cd frontend
npm install
```

Start the development server:

```bash
npm start
```

The frontend will be available at `http://localhost:3000`

## Database Connection

### Option 1: Using Docker (Recommended)

The easiest way to set up the database is using Docker Compose:

1. **Start the MySQL database:**
   ```bash
   docker-compose up -d
   ```

2. **Verify the database is running:**
   ```bash
   docker-compose ps
   ```

3. **Check database logs:**
   ```bash
   docker-compose logs mysql
   ```

**Database Details:**
- **Host**: localhost
- **Port**: 3306
- **Database Name**: jwt_user_db
- **Username**: root
- **Password**: password
- **Connection URL**: `jdbc:mysql://localhost:3306/jwt_user_db`

### Option 2: Using Local MySQL Installation

If you prefer to use a local MySQL installation:

1. **Install MySQL 8.0** on your system
2. **Create the database:**
   ```sql
   CREATE DATABASE jwt_user_db;
   ```
3. **Update application.properties** with your MySQL credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/jwt_user_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

### Database Initialization

The database will be automatically initialized with:
- **Tables**: `users` and `login`
- **Sample Users**: John Doe, Jane Smith, Mike Johnson
- **Default Login Credentials**:
  - Username: `john.doe`, Password: `password123`
  - Username: `jane.smith`, Password: `password123`
  - Username: `mike.johnson`, Password: `password123`

### Connecting to Database Manually

You can connect to the database using any MySQL client:

**Using MySQL Command Line:**
```bash
mysql -h localhost -P 3306 -u root -p
# Enter password: password
USE jwt_user_db;
```

**Using MySQL Workbench or other GUI tools:**
- Host: localhost
- Port: 3306
- Username: root
- Password: password
- Database: jwt_user_db

### Troubleshooting Database Connection

1. **Port 3306 already in use:**
   ```bash
   # Stop existing MySQL service
   sudo service mysql stop
   # Or change port in docker-compose.yaml
   ```

2. **Database connection refused:**
   - Ensure Docker is running
   - Check if MySQL container is healthy: `docker-compose ps`
   - Restart the container: `docker-compose restart mysql`

3. **Permission denied:**
   - Verify credentials in `application.properties`
   - Check if database exists: `SHOW DATABASES;`

4. **Reset database:**
   ```bash
   # Stop and remove containers with volumes
   docker-compose down -v
   # Start fresh
   docker-compose up -d
   ```

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose
- Node.js 16 or higher (for frontend)

## Features

- **JWT Authentication**: Secure authentication with access and refresh tokens
- **User Management**: Complete CRUD operations for users
- **Search Functionality**: Real-time search by first name or last name
- **React Frontend**: Modern UI with authentication and user management
- **MySQL Database**: Persistent data storage
- **Docker Support**: Easy containerized deployment
- **Comprehensive Logging**: SLF4J logging throughout the application

## Technology Stack

### Backend
- **Spring Boot 3.2.0**: Main framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data access layer
- **MySQL 8.0**: Database
- **JWT**: Token-based authentication
- **Maven**: Build tool

### Frontend
- **React**: User interface
- **Axios**: HTTP client
- **React Router**: Navigation
- **Bootstrap**: Styling

## API Endpoints

### Authentication
- `POST /api/auth/signup` - Register a new user
- `POST /api/auth/signin` - Login user
- `POST /api/auth/refresh` - Refresh access token

### Users
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/search?q={term}` - Search users
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `DELETE /api/users/batch` - Delete multiple users

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    age INT,
    birthday DATE
);
```

### Login Table
```sql
CREATE TABLE login (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## JWT Configuration

- **Access Token Expiration**: 15 minutes
- **Refresh Token Expiration**: 24 hours
- **Algorithm**: HS256

## Testing with Postman

1. Import the Postman collection from `src/main/resources/postman/JWT_User_Service.postman_collection.json`
2. Set the environment variable `baseUrl` to `http://localhost:8080`
3. Start with the "Sign In" request to get tokens
4. The collection includes automatic token extraction for subsequent requests

## Frontend Features

### Authentication
- Sign in/Sign up forms
- JWT token management
- Automatic token refresh
- Protected routes

### User Management
- View all users in a table
- Add new users
- Edit existing users
- Delete users (single or batch)
- Real-time search functionality
- Responsive design

## Development

### Backend Structure
```
src/main/java/com/example/jwtuserservice/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── entity/         # JPA entities
├── repository/     # Data access layer
├── security/       # JWT and security components
└── service/        # Business logic layer
```

### Frontend Structure
```
frontend/
├── src/
│   ├── components/ # React components
│   ├── services/   # API services
│   ├── utils/      # Utility functions
│   └── App.js      # Main application
└── package.json
```

## Configuration

### Application Properties
Key configuration in `src/main/resources/application.properties`:

- Database connection settings
- JWT secret and expiration times
- Server port (8080)
- Logging levels

### Environment Variables
You can override these settings using environment variables:
- `SPRING_DATASOURCE_URL`
- `JWT_SECRET`
- `JWT_EXPIRATION`

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Ensure MySQL container is running: `docker-compose ps`
   - Check if port 3306 is available
   - Verify database credentials in `application.properties`

2. **JWT Token Issues**
   - Check JWT secret configuration
   - Verify token expiration settings
   - Ensure proper Authorization header format

3. **Frontend Connection Issues**
   - Verify backend is running on port 8080
   - Check CORS configuration
   - Ensure proper API base URL

### Logs
Application logs are available in the console with detailed information about:
- API requests and responses
- Authentication events
- Database operations
- Error details

## Security Considerations

- Passwords are encrypted using BCrypt
- JWT tokens have short expiration times
- Refresh tokens for extended sessions
- CORS configured for frontend access
- Input validation on all endpoints

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License. 
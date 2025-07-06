# JWT User Service

A comprehensive Spring Boot application with React frontend that provides JWT-based authentication and CRUD operations for user management.

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

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Docker and Docker Compose
- Node.js 16 or higher (for frontend)

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
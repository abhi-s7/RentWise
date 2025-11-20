# RentWise - Property Management System

A full-stack microservices application for managing rental properties, tenants, and roommate requests. Built with Spring Boot microservices and React frontend.

## Quick Start with Docker (Recommended)

### Prerequisites
- Docker Desktop installed and running

### Run the Application

1. **Clone the repository:**
```bash
git clone <repository-url>
cd RentWise
```

2. **Start all services:**
```bash
docker-compose up --build
```

This will automatically:
- Build all microservices and frontend
- Start MySQL and create databases
- Start RabbitMQ
- Start all 6 microservices
- Start the React frontend

3. **Access the application:**
- **Frontend**: http://localhost:5173
- **Eureka Dashboard**: http://localhost:8761
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)
- **API Gateway**: http://localhost:8080

### Common Docker Commands

```bash
# Start in background
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Stop and remove all data (clean slate)
docker-compose down -v
```

### Default Login

After first startup, a default admin user is created:
- Username: `admin`
- Password: `admin123`

You can also register a new user from the login page.

## Manual Setup (Alternative)

If you prefer to run services manually without Docker:

### Prerequisites
- Java 17+
- Node.js 18+
- MySQL 8.0+
- RabbitMQ
- Maven 3.6+

### 1. Database Setup
```bash
mysql -u root -p < backend/database-setup.sql
```

Or manually create:
- `rentwise_user_db`
- `rentwise_property_db`
- `rentwise_tenant_db`

Default credentials: `root` / `password`

### 2. RabbitMQ Setup

**Using Docker:**
```bash
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

**Using Homebrew (macOS):**
```bash
brew install rabbitmq
brew services start rabbitmq
```

Access RabbitMQ UI: http://localhost:15672 (guest/guest)

### 3. Start Backend Services

Start in this order:

1. **Discovery Server** (Port 8761)
```bash
cd backend/rentwise-discovery-server
./mvnw spring-boot:run
```

2. **API Gateway** (Port 8080)
```bash
cd backend/rentwise-api-gateway
./mvnw spring-boot:run
```

3. **User Service** (Port 8081)
```bash
cd backend/rentwise-user-service
./mvnw spring-boot:run
```

4. **Property Service** (Port 8082)
```bash
cd backend/rentwise-property-service
./mvnw spring-boot:run
```

5. **Tenant Service** (Port 8083)
```bash
cd backend/rentwise-tenant-service
./mvnw spring-boot:run
```

6. **Dashboard Service** (Port 8084)
```bash
cd backend/rentwise-dashboard-service
./mvnw spring-boot:run
```

### 4. Start Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs on: `http://localhost:5173`

## Microservices Overview

| Service | Port | Function | Database |
|---------|------|----------|----------|
| **Discovery Server** | 8761 | Eureka service registry | None |
| **API Gateway** | 8080 | Single entry point, routes requests | None |
| **User Service** | 8081 | User authentication & management | rentwise_user_db |
| **Property Service** | 8082 | Property CRUD operations | rentwise_property_db |
| **Tenant Service** | 8083 | Tenant management & requests | rentwise_tenant_db |
| **Dashboard Service** | 8084 | Aggregates data, WebSocket notifications | None |

### Service URLs

- **Eureka Dashboard**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **User Service**: http://localhost:8081
- **Property Service**: http://localhost:8082
- **Tenant Service**: http://localhost:8083
- **Dashboard Service**: http://localhost:8084
- **React Frontend**: http://localhost:5173

## Features

### Authentication & Authorization
- User registration and login
- Role-based access (ADMIN/USER)
- Session management with Redux
- Profile editing (email, password)

### Property Management
- Add, edit, delete properties
- Property details (address, rent, type, status)
- Card-based grid layout
- Filter by status (Available, Rented, Maintenance)

### Tenant Management
- Create and manage tenant records
- Assign tenants to properties
- View tenant details and relationships
- Roommate assignment tracking

### Roommate Requests
- Users can request roommates
- Admin approval/rejection workflow
- Real-time updates via WebSocket
- Request status tracking (Pending, Approved, Rejected)

### Real-time Updates
- WebSocket integration for live notifications
- Instant updates when requests are created/approved/rejected
- No page refresh needed

### Dashboard Views
- **Admin Dashboard**: View all properties, tenants, and pending requests
- **User Dashboard**: View personal properties, roommates, and request status

## Tech Stack

### Backend
- Spring Boot 3.x
- Spring Cloud (Eureka, Gateway, OpenFeign)
- MySQL
- RabbitMQ
- WebSocket (STOMP)
- JPA/Hibernate

### Frontend
- React 19
- TypeScript
- Redux Toolkit
- React Router
- Axios
- Tailwind CSS
- Vite
- WebSocket (STOMP over SockJS)

## Notes

- All services register with Eureka for service discovery
- API Gateway handles CORS and routes all frontend requests
- Database tables are auto-created by Hibernate on first run
- Sample properties are inserted automatically on first database setup
- Default admin user is created automatically on first startup
- Logs are stored in `backend/logs/` directory
- Database data persists in Docker volumes

## Troubleshooting

**Docker services won't start:**
- Check if ports are already in use: `lsof -i :8080`
- Verify Docker Desktop is running
- Check service logs: `docker-compose logs [service-name]`
- Ensure MySQL container is healthy: `docker-compose ps mysql`

**Services won't start (manual setup):**
- Check if MySQL is running
- Verify RabbitMQ is running (for Tenant and Dashboard services)
- Ensure ports are not already in use

**Frontend can't connect:**
- Verify API Gateway is running on port 8080
- Check browser console for CORS errors
- Ensure all backend services are registered in Eureka

**WebSocket not working:**
- Check RabbitMQ is running
- Verify Dashboard Service is running
- Check browser console for connection errors

**Database connection issues (Docker):**
- Wait for MySQL container to be healthy before services start
- Check MySQL logs: `docker-compose logs mysql`
- Verify database credentials in docker-compose.yml


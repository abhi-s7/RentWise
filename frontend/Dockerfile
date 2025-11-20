# This Dockerfile builds and serves the React frontend application
# It uses multi-stage build: first stage builds, second stage serves with nginx

# Stage 1: Build the React application
FROM node:18-alpine AS builder

# Set working directory
WORKDIR /app

# Copy package files first (for better caching)
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy source code
COPY . .

# Build the application for production
# This creates optimized static files in dist/ directory
RUN npm run build

# Stage 2: Serve the application with nginx
FROM nginx:alpine

# Copy built files from builder stage to nginx html directory
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy nginx configuration file
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80 (nginx default port)
EXPOSE 80

# Start nginx
# -g "daemon off" runs nginx in foreground so Docker container stays alive
CMD ["nginx", "-g", "daemon off;"]


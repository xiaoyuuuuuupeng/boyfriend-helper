# Authentication Service Implementation Plan (Revised)

I will implement the authentication system using **MySQL** and **MyBatis-Plus** for persistent storage, and **Redis** for session management.

## 1. Project Dependencies & Environment
- **Modify `pom.xml`**: Add dependencies for:
  - `mybatis-plus-spring-boot3-starter` (instead of JPA)
  - `spring-boot-starter-data-redis`
  - `mysql-connector-j`
- **Create `docker-compose.yml`**: Set up MySQL (8.0) and Redis (latest) containers.

## 2. Configuration
- **Update `application.yml`**:
  - Configure MySQL connection.
  - Configure Redis connection.
  - Configure MyBatis-Plus (mapper locations, global config for logic delete).

## 3. Data Layer
- **Create Entity**: `src/main/java/com/boyfriend/helper/entity/User.java`
  - Fields: `id`, `phone`, `nickname`, `avatarUrl`, `createTime`, `updateTime`.
  - **Logical Delete**: Add `deleted` field with `@TableLogic` annotation.
- **Create Mapper**: `src/main/java/com/boyfriend/helper/mapper/UserMapper.java`
  - Extend `BaseMapper<User>`.

## 4. Service Layer
- **Create Service**: `src/main/java/com/boyfriend/helper/service/UserService.java` (interface) and `UserServiceImpl.java`
  - Extend `IService<User>` / `ServiceImpl`.
  - `sendVerificationCode(String phone)`: Mock implementation.
  - `loginOrRegister(String phone, String code)`:
    - Verify code.
    - Check if user exists (ignoring deleted ones automatically by MP); if not, create.
    - Generate UUID token.
    - Store `token -> userId` in Redis.
  - `kickUser(Long userId)`: Remove user's token from Redis.

## 5. Web Layer (Controller)
- **Create Controller**: `src/main/java/com/boyfriend/helper/controller/AuthController.java`
  - `POST /api/auth/code`: Send code.
  - `POST /api/auth/login`: Login/Register.
  - `GET /api/user/info`: Get info.
  - `POST /api/admin/kick`: Kick user.

## 6. Security
- **Create Interceptor**: `src/main/java/com/boyfriend/helper/interceptor/AuthenticationInterceptor.java`
  - Validate UUID token from header.
- **Configure WebMvc**: `src/main/java/com/boyfriend/helper/config/WebConfig.java`

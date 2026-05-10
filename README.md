# Taxi service (микросервисы)

Backend учебного сервиса заказа такси: три Spring Boot-приложения, отдельные БД PostgreSQL, Redis, оркестрация через Docker Compose.

## Сервисы и порты

| Сервис               | Порт | Назначение                                                              |
|----------------------|------|-------------------------------------------------------------------------|
| user-service         | 8081 | Пассажиры, водители, JWT `/auth/token`, внутренние API для trip-service |
| trip-service         | 8082 | Поездки, расчёт цены, вызов user-service и notification-service по HTTP |
| notification-service | 8083 | Очередь уведомлений в БД, пул воркеров                                  |

PostgreSQL: `5432` (user), `5433` (trip), `5434` (notification). Redis: `6379`.

## Запуск

Требуется Docker и Docker Compose.

```bash
docker compose up --build
```

Первый запуск может занять несколько минут (сборка JAR внутри образов).

## Стек

- Java 21, Spring Boot 3.2  
- Spring Data JPA, PostgreSQL  
- Spring Security, JWT  
- Redis (кэш списка свободных водителей, JSON-сериализация значений)  
- Связь **trip → notification** по HTTP (`POST /notifications` с заголовком `X-Internal-Api-Key`)

## Учётные данные (только для разработки)

В `docker-compose.yml` заданы тестовые пароли БД, `JWT_SECRET` и `INTERNAL_API_KEY: dev-internal-key`. Для продакшена не использовать.

## Структура репозитория

- `common` — общие DTO и перечисления  
- `user-service`, `trip-service`, `notification-service` — модули приложений  
- `docker-compose.yml` — инфраструктура и сервисы  

Проверка API удобна через Postman (регистрация → токен → создание поездки → смена статусов → список уведомлений).

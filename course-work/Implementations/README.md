# Menucraft

Пълнофункционална платформа за управление на рецепти. Планирайте хранения, следете наличните продукти и генерирайте списъци за пазаруване.

**Технологии:** Angular 19 · Spring Boot 3.2 · PostgreSQL · JWT удостоверяване

---

## Изготвили

| Факултетен номер | Име              |
|------------------|------------------|
| 2501322042       | Виктория Пенчева |
| 2501322043       | Йоан Павлов      |

---

## Функционалности

| Област | Описание |
|---|---|
| **Рецепти** | Разглеждане, търсене, филтриране по кухня/трудност/макронутриенти/хранителни тагове, създаване, редактиране и изтриване |
| **Съставки** | Централна библиотека със съставки, калории и макронутриенти |
| **Категории** | Групиране по тип кухня |
| **Планировчик на хранения** | Седмичен календар с drag-and-drop |
| **Килер** | Следене на наличности и рецепти, които могат да се приготвят |
| **Списък за пазаруване** | Автоматично генериране от плана за хранения |
| **Оценки** | Оценяване на рецепти с 1–5 звезди |
| **Администрация** | Управление на категории и съставки (само администратор) |

---

## Предварителни изисквания

| Инструмент | Версия |
|---|---|
| Java | 17+ |
| Maven | 3.8+ |
| Node.js | 18+ |
| npm | 9+ |
| PostgreSQL | 14+ |

---

## Настройка на базата данни

```sql
CREATE DATABASE recipesdb;
```

По подразбиране:
- Host: localhost:5432
- Database: recipesdb
- Username: postgres
- Password: postgres

---

## Backend

### Стартиране

```bash
cd backend
mvn spring-boot:run
```

Backend: http://localhost:8080

Swagger UI: http://localhost:8080/swagger-ui.html

---

## Frontend

### Стартиране

```bash
cd frontend
npm install
npm start
```

Frontend: http://localhost:4200

### Основни маршрути

- `/login` – Вход
- `/register` – Регистрация
- `/recipes` – Списък с рецепти
- `/recipes/new` – Създаване на рецепта
- `/categories` – Категории
- `/ingredients` – Съставки
- `/meal-plan` – План за хранения
- `/pantry` – Килер
- `/shopping-list` – Списък за пазаруване

---

## Акаунти по подразбиране

| Потребител | Парола    | Роля  |
|---|-----------|-------|
| admin | Admin123! | Admin |
| chef | Chef123!  | User  |

---
## Структура на проекта

```
dist apps/
├── backend/
│   └── src/main/java/com/recipes/
│       ├── auth/           # JWT filter, login rate limiter, AuthController
│       ├── config/         # SecurityConfig, DataSeeder
│       ├── controller/     # REST endpoints (recipes, ingredients, categories, upload…)
│       ├── dto/            # Request/response records
│       ├── entity/         # JPA entities (Recipe, Ingredient, Category, User…)
│       ├── exception/      # GlobalExceptionHandler, ResourceNotFoundException
│       ├── repository/     # Spring Data JPA interfaces
│       ├── service/        # Business logic
│       └── util/           # QuantityConverter (g/kg/pcs normalisation)
└── frontend/
    └── src/app/
        ├── auth/           # Login, Register (with async username/email validation)
        ├── categories/     # List, detail, form
        ├── ingredients/    # List, detail, form
        ├── meal-plan/      # Weekly drag-and-drop planner
        ├── pantry/         # Pantry tracker + recipe coverage tiers
        ├── recipes/        # List, detail, form, card, sidebar
        ├── shopping-list/  # Shopping list with meal-plan generation
        ├── shared/         # Navbar, star rating, pagination
        └── core/
            ├── guards/     # authGuard, adminGuard
            ├── interceptors/ # JWT interceptor (auto-logout on 401)
            ├── models/     # TypeScript interfaces
            └── services/   # API service layer
```

---
## API

Всички крайни точки освен `/auth/**` изискват:

```http
Authorization: Bearer <token>
```

Основни операции:
- Регистрация и вход
- Управление на рецепти
- Управление на категории
- Управление на съставки
- Килер
- Списък за пазаруване
- План за хранения
- Качване на изображения



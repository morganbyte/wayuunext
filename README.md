# WayuuNext

App web para aprender wayuunaiki y la cultura del pueblo wayuu mediante tests sobre lengua, saludos, comida, tradiciones, historia y artesanías.

**Stack:** Spring Boot 3 (Java 21) · PostgreSQL 17 · React · JWT

## Arrancar en local

Solo necesitas Docker y Node.

```bash
# 1. Configuración (solo la primera vez)
cp .env.example .env
#    rellena DB_PASSWORD y JWT_SECRET (genera uno con: openssl rand -base64 48)

# 2. Base de datos + backend (la primera vez carga script.sql)
docker compose up -d --build

# 3. Frontend
cd frontend
npm install
npm start
```

- Frontend: http://localhost:3000
- API: http://localhost:8080/api

Para apagarlo: `docker compose down` (los datos se conservan en un volumen; `docker compose down -v` los borra).

## Tests

```bash
./mvnw test
# sin Java instalado:
docker run --rm -v "$PWD":/app -w /app maven:3.9-eclipse-temurin-21 mvn test
```

Usan H2 en memoria, así que no necesitan PostgreSQL.

## Administradores

Los usuarios nuevos tienen el rol `USER`. Para dar permisos de administrador (necesarios para crear tests):

```bash
docker compose exec db psql -U morgan -d wayuu_db -c "UPDATE \"user\" SET role='ADMIN' WHERE username='tu_usuario';"
```

El usuario tiene que volver a iniciar sesión para que el rol entre en su token.

## API

| Método | Ruta | Acceso |
|---|---|---|
| POST | `/api/users/register` | público |
| POST | `/api/auth/login` | público |
| GET | `/api/tests` | público |
| GET | `/api/tests/{id}/preguntas` | público (sin respuestas) |
| POST | `/api/tests/{id}/evaluar` | usuario autenticado; actualiza su progreso |
| GET | `/api/progress` | usuario autenticado; su propio progreso |
| POST | `/api/tests/create` | `ADMIN` |

## Roadmap

Ver [ROADMAP.md](ROADMAP.md).

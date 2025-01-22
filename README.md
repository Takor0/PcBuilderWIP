# PC Builder

Aplikacja do tworzenia konfiguracji komputerowych.

## Wymagania

### Narzędzia
- Docker Desktop (z Docker Compose)
- Node.js 18+
- Yarn
- Java 17
- Maven

### Porty
Upewnij się, że następujące porty są dostępne:
- 3306 (MySQL)
- 8080 (Backend)
- 5173 (Frontend)

## Uruchomienie

1. Sklonuj repozytorium:
```bash
git clone [url-repozytorium]
cd pcbuilder
```

2. Skopiuj plik konfiguracyjny:
```bash
cp .env.example .env
cp backend/src/main/resources/application.properties.example backend/src/main/resources/application.properties
```

3. Skonfiguruj backend:
   - Domyślna konfiguracja nie wymaga zmian (puste hasło do bazy danych)
   - W razie potrzeby dostosuj ustawienia w `backend/src/main/resources/application.properties`

4. Uruchom aplikację:
```bash
docker-compose up --build
```

5. Zatrzymanie i wyczyszczenie:
```bash
docker-compose down -v
```

## Przydatne komendy

### Logi
- `docker logs pcbuilder-frontend` - logi frontendu
- `docker logs pcbuilder-backend` - logi backendu
- `docker logs pcbuilder-mysql` - logi bazy danych

### Status
- `docker ps` - lista uruchomionych kontenerów

## Dostęp

- Frontend: http://localhost:5173
- Backend API: http://localhost:8080
- MySQL: localhost:3306

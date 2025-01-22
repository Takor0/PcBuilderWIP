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

## API

### Generowanie konfiguracji

Endpoint służący do generowania konfiguracji komputerowej na podstawie preferencji użytkownika.

```
POST /api/builds/generate
```

#### Wymagane pola formularza

1. **Budget** (budżet)
   - Kwota w PLN
   - Przykład: 5000.0

2. **CPU Preference** (preferencja procesora)
   - Dostępne opcje: 
     - `AMD` - preferuj procesory AMD
     - `INTEL` - preferuj procesory Intel
     - `NO_PREFERENCE` - bez preferencji

3. **GPU Preference** (preferencja karty graficznej)
   - Dostępne opcje:
     - `AMD` - preferuj karty AMD
     - `NVIDIA` - preferuj karty NVIDIA
     - `NO_PREFERENCE` - bez preferencji

4. **Usage** (przeznaczenie)
   - Dostępne opcje:
     - `GAMING_AAA` - gry AAA
     - `GAMING_ESPORT` - gry e-sportowe
     - `WORKSTATION` - stacja robocza
     - `OFFICE` - komputer biurowy

5. **Priority** (priorytet)
   - Dostępne opcje:
     - `PERFORMANCE` - nacisk na wydajność
     - `BALANCED` - zbalansowany stosunek ceny do wydajności
     - `PRICE` - nacisk na cenę

#### Format żądania

```json
{
    "budget": 5000.0,
    "cpuPreference": "AMD",
    "gpuPreference": "NVIDIA",
    "usage": "GAMING_AAA",
    "priority": "BALANCED"
}
```

#### Format odpowiedzi

Odpowiedź zawiera szczegółowe informacje o komponentach oraz ich opisy generowane automatycznie na podstawie parametrów technicznych.

```json
{
    "cpu": {
        "name": "AMD Ryzen 7 5800X",
        "price": 1299.99,
        "socket": "AM4",
        "coreCount": 8,
        "coreClock": 3.8,
        "boostClock": 4.7,
        "tdp": 105
    },
    "gpu": {
        "name": "NVIDIA GeForce RTX 3070",
        "price": 2499.99,
        "memory": 8,
        "coreClock": 1.50,
        "boostClock": 1.73
    },
    "motherboard": {
        "name": "MSI MAG B550 TOMAHAWK",
        "price": 699.99,
        "socket": "AM4",
        "formFactor": "ATX"
    },
    "storage": [
        {
            "name": "Samsung 970 EVO Plus",
            "price": 399.99,
            "capacity": 1000,
            "type": "SSD"
        }
    ],
    "powerSupply": {
        "name": "Corsair RM750x",
        "price": 499.99,
        "wattage": 750,
        "efficiency": "80+ Gold"
    },
    "pcCase": {
        "name": "NZXT H510",
        "price": 349.99,
        "formFactor": "ATX"
    },
    "totalPrice": 4999.99,
    "componentDescriptions": {
        "cpu": "AMD Ryzen 7 5800X with 8 cores at 3.8GHz (boosts to 4.7GHz).",
        "gpu": "NVIDIA GeForce RTX 3070 with 8GB VRAM, core clock at 1.50GHz (boosts to 1.73GHz)",
        "motherboard": "MSI MAG B550 TOMAHAWK supporting up to 4800MHz memory speed.",
        "storage": "Samsung 970 EVO Plus 1000GB SSD drive",
        "powerSupply": "Corsair RM750x rated at 750W with 80+ Gold efficiency",
        "case": "NZXT H510 (ATX form factor)"
    }
}
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

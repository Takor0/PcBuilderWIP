# PC Builder

## Konfiguracja

1. Skopiuj plik konfiguracyjny:
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

2. Edytuj `application.properties` i ustaw:
- `spring.datasource.username` - nazwa użytkownika bazy danych
- `spring.datasource.password` - hasło do bazy danych

## Uruchomienie

1. Zbuduj projekt:
```bash
mvn clean install
```

2. Uruchom aplikację:
```bash
mvn spring-boot:run
```

Backend dostępny pod adresem: `http://localhost:8080` 
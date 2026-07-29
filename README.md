# CLOB

```
Project File Structure

clob-matching-engine/
├── .gitignore
├── README.md
├── pom.xml
└── src/
    ├── main/java/com/quant/clob/
    │   ├── ingress/        # Netty TCP Server & Custom FIX Framer/Parser
    │   ├── disruptor/      # Ring Buffer setup
    │   ├── engine/         # Core Matching Thread, Selph Order Book
    │   └── egress/         # Async PostgreSQL DB Logging & Flyway Migrations
    └── main/resources/
        ├── application.yml # Spring Boot & DB configurations
        └── db/migration/   # Flyway SQL scripts
```
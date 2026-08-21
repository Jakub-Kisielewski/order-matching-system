# Order matching system

## Trade lifecycle in this system
### High level
Client connects to Netty server and submits either a market/limit buy/sell order (over TCP using the FIX protocol) to the matching engine which will find a matching
buy/sell order to then execute the trade. Logging of trade activity done asynchronously.

### Lower level
```
                                                                                                  <- (Event objects)
Client (TCP Connection with FIX) -> Netty server -> Framer -> Custom FIX parser -> LMAX Disruptor -> Matching engine/CLOB -> Trade execution 
                                                                                                  ├─> Postgres thread

The LMAX Disruptor stores both inbound client trades and outbound informational event objects (trade execution info, orderbook snapshots) where both the matching
engine and postgres thread consume their respective data from. 

```
## Feature Set
Netty TCP Server (TODO)
FIX Framer (TODO)
Custom FIX Parser (TODO)
Disruptor (TODO)
Matching Engine 
Infomation Capture (TODO)

## Project File Structure
```
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

## Extensions
- Extend to multiple instruments
- Introduce "equalised access" problem for client connections
- More order types e.g. stop loss
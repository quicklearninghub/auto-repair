# auto-repair
Spring boot - Kafka project to demonstrate Reprocessing pattern - automatically attempts to repair failed messages without human intervention.

## Common modules
    - database
    - kafka

## Spring boot Applications to run:
    - main-consumer
        ### Endpoint:
            post: localhost:8181/main/publish
            body: {"accountId": "12345678" }
    - static-data-rest
        ### Endpoint:
            put: localhost:8282/12345678/risk/1
            body: none
    - auto-repair-service

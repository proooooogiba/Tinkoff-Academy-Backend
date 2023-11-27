Запустить postgres в Docker можно с помощью

`docker run --name news-aggregator -e POSTGRES_PASSWORD=password  -e POSTGRES_DB=pets-store -dp 5432:5432 postgres`

Интеграционные тесты (должен быть запущен Docker)

`sbt IntegrationTest/test`

либо из IDE

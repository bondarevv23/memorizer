# memorizer

Telegram bot for remembering information using cards.

The project is still is progress, but backend is completed.
I use such technologies as:
* documenting: [openapi](./src/main/resources/api.0.1.yaml), swagger and openapi-generator
* database: postgresql, migrations with liquibase, [jdbc](./src/main/java/com/github/bondarevv23/memorizer/repository/jdbc)
and [jpa](./src/main/java/com/github/bondarevv23/memorizer/repository/jpa) repositories
* testing: unit tests via Junit, Mockito and AssertJ; integration tests via TestContainers and migrations; 100% test coverage

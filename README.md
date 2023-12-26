# memorizer

Telegram bot for memorizing information using cards.

In server part:
* documenting: [openapi](./src/main/resources/api.0.1.yaml), swagger and openapi-generator
* database: postgresql, migrations with liquibase, [jdbc](./src/main/java/com/github/bondarevv23/memorizer/repository/jdbc)
and [jpa](./src/main/java/com/github/bondarevv23/memorizer/repository/jpa) repositories
* testing: unit tests via Junit, Mockito and AssertJ; integration tests via TestContainers and migrations; 100% test coverage

In telegram bot:
* kotlin and telegram bot api [library](https://github.com/vendelieu/telegram-bot)
* redis for conversations and saving user state

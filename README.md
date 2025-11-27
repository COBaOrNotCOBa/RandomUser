# RandomUser

Учебное Android-приложение на Kotlin с использованием API [https://randomuser.me](https://randomuser.me).

Позволяет:
- создавать случайных пользователей по полу и национальности;
- просматривать подробную информацию о пользователе;
- видеть список всех созданных пользователей;
- работать с уже созданными пользователями без интернета (данные кэшируются в Room).

---

## Стек технологий

- **Язык**: Kotlin
- **UI**: Jetpack Compose, Material 3
- **Навигация**: Navigation Compose (Single Activity)
- **DI**: Hilt (+ KSP)
- **Сеть**: Retrofit, OkHttp, Moshi
- **Хранение**: Room (SQLite)
- **Асинхронность**: Coroutines, Flow / StateFlow
- **Изображения**: Coil 3
- **Тесты**: JUnit4, `kotlinx-coroutines-test`, MockK

---

## Архитектура

Проект разделён на три слоя:

- **data**
  - `RandomUserApi`, DTO-модели ответа.
  - Room: `UserEntity`, `UserDao`, `AppDatabase`.
  - `UserRepositoryImpl` и мапперы `UserDto → UserEntity → User`.

- **domain**
  - Модель `User`.
  - Интерфейс `UserRepository`.
  - Use case-ы: `GetRandomUserUseCase`, `GetUsersUseCase`, `GetUserByIdUseCase`, `DeleteUserUseCase`.
  - Enum `Gender` с `apiValue` для работы с API.

- **presentation**
  - ViewModel-ы: `CreateUserViewModel`, `UserListViewModel`, `UserDetailsViewModel` (StateFlow + `SharingStarted.WhileSubscribed`).
  - Экраны: `CreateUserScreen`, `UserListScreen`, `UserDetailsScreen`.
  - Навигация: `RandomUserNavGraph`, sealed-класс `Screen`.
  - Общий `UiState<out T>`: `Idle / Loading / Success / Empty / Error`.
  - Общие компоненты: `RandomUserTopBar`, `BackIconButton`, `UserListItem`.
  - Тема и стили: `RandomUserTheme`, `AppColors`, `AppTextStyles`.
  - `StringProvider` / `AndroidStringProvider` для работы со строками в ViewModel без прямой зависимости от Android-контекста.

---

## Экраны

### Экран списка пользователей (главный)

- Показывает всех сохранённых пользователей из Room.
- Если список пуст — `UiState.Empty` и текст о том, что пользователей ещё нет.
- Для каждого пользователя отображаются:
  - аватар;
  - полное имя;
  - телефон;
  - код национальности и флаг (через `nationalityToFlagEmoji`).
- Через меню **Delete** можно удалить пользователя.

### Экран создания пользователя

- Заголовок: `Generate User` со стрелкой назад.
- Выпадающие списки:
  - пол: `Any / Male / Female` (в домене хранится как `Gender`);
  - национальность: набор значений `nat`, поддерживаемых API (`AU`, `BR`, `CA`, …, `US`).
- Кнопка **GENERATE**:
  - показывает прогресс;
  - вызывает `GetRandomUserUseCase`;
  - при успехе сохраняет пользователя в Room и переходит на экран детализации;
  - при ошибке показывает сообщение (строка берётся через `StringProvider`).

### Экран детализации пользователя

- Градиентная шапка с цветом, зависящим от пола (мужской/женский градиент).
- Аватар располагается на границе шапки и основного фона.
- Карточка с табами:
  - **Общие данные** (имя, пол, возраст, дата рождения `dateOfBirth`).
  - **Телефоны** (phone, cell).
  - **Email**.
  - **Адрес** (страна, город, улица, полный адрес).

Все подписи/лейблы вынесены в `strings.xml` и используются через `stringResource`.

---

## Кэширование и офлайн-режим

- После успешного запроса пользователь сохраняется в Room.
- Список и детализация работают только с локальными данными из Room.
- При отсутствии интернета можно продолжать работать с уже сохранёнными пользователями.
- Ошибки сети и сервера обрабатываются через `UiState.Error` и показываются пользователю текстом.

---

## Тестирование

Покрыты юнит-тестами:

- **ViewModel-ы**:
  - `UserListViewModelTest` — пустой/непустой список, удаление пользователя.
  - `UserDetailsViewModelTest` — найденный/ненайденный пользователь, обработка ошибок.
  - `CreateUserViewModelTest` — начальное состояние, успешное/неуспешное создание пользователя.

- **Репозиторий**:
  - `UserRepositoryImplTest` — успешный сценарий, отсутствие данных, некорректные данные, обработка `HttpException` / `IOException` и других исключений, работа с Flow из Room.

- **Прочее**:
  - `UserMappersTest` — маппинг DTO ↔ Entity ↔ Domain.
  - `NationalityToFlagEmojiTest` — соответствие кода национальности эмодзи-флагу.

Используются `kotlinx-coroutines-test` и `MainDispatcherRule` для тестирования корутин, MockK — для моков зависимостей.

---

## Сборка и запуск

1. Клонировать репозиторий:
   ```bash
   git clone https://github.com/COBaOrNotCOBa/RandomUser.git
   cd RandomUser

2. Открыть проект в Android Studio (AGP 8.x, Kotlin 2.x).

3. Убедиться, что Gradle JDK ≥ Java 17.

4. Дождаться окончания синхронизации Gradle.

5. Запустить модуль app на эмуляторе или устройстве (minSdk 26).

# RandomUser

Учебное приложение на базе API [https://randomuser.me](https://randomuser.me), реализованное с опорой на современные практики Android-разработки (Kotlin, Jetpack Compose, Hilt, Clean Architecture).

Приложение позволяет:

- создавать случайных пользователей по полу и национальности;
- просматривать детальную информацию о пользователе;
- видеть список всех созданных пользователей;
- работать с ранее созданными пользователями без интернета (кэширование в базе данных).

---

## Ключевые особенности

- Полноценное разделение на слои **data / domain / presentation**.
- Строгое именование без сокращений (`nationality` вместо `nat`, `dateOfBirth` вместо `date` и т.п.).
- Все ViewModel используют **StateFlow + collectAsStateWithLifecycle**.
- Работа офлайн: список и детализация читают данные из **Room**, даже без сети.
- Единый тип состояний `UiState` для всех экранов.
- Строки для сообщений/ошибок в ViewModel не «жёстко зашиты» — используются через DI (`StringProvider`).

---

## Стек технологий

- **Язык**: Kotlin
- **UI**: Jetpack Compose, Material 3
- **Навигация**: Navigation Compose (Single Activity)
- **DI**: Hilt (+ KSP)
- **Сеть**: Retrofit, OkHttp, Moshi
- **Локальное хранилище**: Room (SQLite)
- **Асинхронность**: Kotlin Coroutines, Flow / StateFlow
- **Загрузка изображений**: Coil 3
- **Тестирование**:
  - JUnit4
  - `kotlinx-coroutines-test`
  - MockK

---

## Архитектура

Проект разделён на три слоя.

### data

- `RandomUserApi` – Retrofit-интерфейс для `https://randomuser.me/api/`.
- DTO-модели ответа: `RandomUserResponseDto`, `UserDto`, вложенные структуры.
- Room:
  - `UserEntity`
  - `UserDao`
  - `AppDatabase`
- Реализация репозитория: `UserRepositoryImpl`
- Мапперы:
  - `UserDto.toEntity()`
  - `UserEntity.toDomain()`

### domain

- Модель домена: `User`
- Контракт репозитория: `UserRepository`
- Use case-ы:
  - `GetRandomUserUseCase`
  - `GetUsersUseCase`
  - `GetUserByIdUseCase`
  - `DeleteUserUseCase`
- Дополнительные модели:
  - `Gender` – enum для пола с `apiValue` для RandomUser API.

### presentation

- ViewModel-ы:
  - `CreateUserViewModel`
  - `UserListViewModel`
  - `UserDetailsViewModel`
- Экраны (Jetpack Compose):
  - `CreateUserScreen`
  - `UserListScreen`
  - `UserDetailsScreen`
- Навигация:
  - `RandomUserNavGraph`
  - sealed-класс `Screen`
- Состояние UI:
  - `UiState<out T>`: `Idle / Loading / Success / Empty / Error`
- Общие UI-компоненты:
  - `RandomUserTopBar` + единый `BackIconButton`
  - `UserListItem`
- Тема/стили:
  - `RandomUserTheme`
  - `AppColors`
  - `AppTextStyles`
- Общие сервисы презентационного слоя:
  - `StringProvider` / `AndroidStringProvider` (через Hilt-модуль `PresentationModule`)

---

## Экраны

### 1. Список пользователей (главный экран)

- Показывает список всех созданных пользователей из Room.
- Если список пуст:
  - в UI отображается сообщение (через `UiState.Empty`);
  - дополнительно реализована возможность навигации на экран создания пользователя (чтобы пользователь не оставался в «пустом» состоянии).
- Элемент списка содержит:
  - фото пользователя;
  - полное имя;
  - телефон;
  - флаг и код национальности (`nationalityToFlagEmoji`);
  - меню с пунктом **Delete** для удаления пользователя из БД.
- Это главный экран — стрелка назад не отображается.

### 2. Экран создания пользователя

- Заголовок: `Generate User` (Material 3 top bar с кнопкой «назад»).
- Два выпадающих списка:
  - **Select Gender**:
    - Any / Male / Female
    - в домене хранится как `Gender`, в API маппится в `gender.apiValue`.
  - **Select Nationality**:
    - список из официально поддерживаемых API кодов (`AU`, `BR`, `CA`, `CH`, `DE`, …, `US`).
- Кнопка **GENERATE** внизу экрана.
- При нажатии:
  - `UiState` переходит в `Loading`;
  - через `GetRandomUserUseCase` вызывается `UserRepository.fetchAndSaveRandomUser`;
  - при успехе:
    - пользователь сохраняется в Room;
    - вызывается callback с `user.id`, и навигация переходит на экран детализации;
    - `UiState` → `Success(user)`;
  - при ошибке:
    - `UiState.Error` с человекочитаемым текстом (сообщение берётся из ресурсов через `StringProvider`).

Состояние экрана описывается `CreateUserUiState`, который содержит:
- `requestState: UiState<User>`
- `gender: Gender`
- `nationality: String?`

### 3. Экран детализации пользователя

- Верхний блок – градиентный фон (цвет зависит от пола пользователя):
  - для мужчин используется «синий» градиент;
  - для женщин – «розовый».
- В левом верхнем углу – кнопка «назад» в круглой плашке (общий `BackIconButton`).
- Аватар пользователя (Coil) центрирован и «лежит» на границе градиента и основной части экрана.
- Ниже – приветствие и имя пользователя.
- Основная информация показана в карточке с табами:
  - **Общая информация**:
    - First name / Last name (разбор `fullName`);
    - Gender;
    - Age;
    - Date of birth (`dateOfBirth.substringBefore("T")`).
  - **Phones**:
    - Phone;
    - Cell.
  - **Email**.
  - **Location**:
    - Country, City, Street;
    - Full address (конкатенация).

Все подписи/лейблы вынесены в `strings.xml` и используются через `stringResource`.

---

## Работа офлайн и кэширование

- При успешном создании пользователя данные сохраняются в Room.
- Экран списка (`UserListScreen`) и детализации (`UserDetailsScreen`) работают только с локальной базой данных.
- После первого успешного запроса приложение становится полностью работоспособным в офлайне:  
  список и детализация читают данные из Room без обращений к сети.
- Ошибки сети на экране создания пользователя отображаются в `UiState.Error`, но не блокируют работу с уже сохранёнными данными.

---

## Обработка ошибок и состояний

Используется единый тип:

```kotlin
sealed interface UiState<out T> {
    object Idle : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Empty(val message: String) : UiState<Nothing>
    data class Error(val message: String) : UiState<Nothing>
}
Loading – показывается CircularProgressIndicator.

Empty – текст о том, что данных нет (например, нет пользователей или пользователь не найден).

Error – текст ошибки (сеть, сервер и т.д.), строки берутся через StringProvider.

Success – контент экрана.

Idle – стартовое состояние в сценариях, где запрос ещё не запускался.

Современные практики, использованные в проекте
StateFlow во всех ViewModel для UI-состояний:

stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialValue)

В UI — collectAsStateWithLifecycle() (где это оправдано).

Отделение «сырого» состояния от UI:

для создания пользователя — явный CreateUserUiState вместо набора отдельных mutableStateOf.

Отсутствие сокращений в доменных моделях и public API:

nationality вместо nat;

dateOfBirth вместо абстрактного date.

Централизованный доступ к строкам в ViewModel:

StringProvider внедряется через Hilt;

упрощает тестирование и убирает завязку на Android context из ViewModel.

Тестируемый репозиторий:

UserRepositoryImplTest покрывает успешные и неуспешные сценарии работы с API и Room.

Использование WhileSubscribed(5_000) для SharingStarted — рекомендуемый режим для UI-состояний, позволяющий:

не держать горячий поток вечно;

переиспользовать старое состояние при возвращении на экран.

Тесты
Юнит-тесты покрывают:

ViewModel-ы:

UserListViewModelTest:

пустой список → UiState.Empty;

непустой список → UiState.Success;

удаление пользователя вызывает DeleteUserUseCase.

UserDetailsViewModelTest:

пользователь найден → UiState.Success;

пользователь не найден → UiState.Empty с корректным текстом;

ошибка → UiState.Error.

CreateUserViewModelTest:

начальное состояние → Idle, без выбранных пола и национальности;

успешный generateUser:

состояние Success;

callback навигации получает корректный user.id;

провалившийся generateUser → состояние Error.

Репозиторий:

UserRepositoryImplTest (MockK + мок DTO/DAO/мапперов):

успешный запрос и сохранение;

отсутствие пользователей в ответе;

некорректные данные (mapper возвращает null);

HttpException → сообщение "Server error: <code>";

IOException → сообщение "Network error. Check your connection.";

прочие исключения → "Unexpected error: …";

корректное отображение данных Flow из Room.

Мапперы и утилиты:

UserMappersTest – корректность преобразования DTO → Entity → Domain.

NationalityToFlagEmojiTest – сопоставление кода национальности и эмодзи-флага.

Для корутин-тестов используется MainDispatcherRule и runTest из kotlinx-coroutines-test.

Сборка и запуск
Клонировать репозиторий:

bash
Копировать код
git clone https://github.com/COBaOrNotCOBa/RandomUser.git
cd RandomUser
Открыть проект в Android Studio (версия с поддержкой Kotlin 2.x и AGP 8.x).

Убедиться, что Gradle JDK — не ниже Java 17.

Дождаться окончания синхронизации Gradle.

Запустить конфигурацию app на эмуляторе или физическом устройстве (minSdk 26).

Идеи для дальнейшего развития
Добавить полноценную поддержку локализаций (выделить все строковые ресурсы, в том числе для тестовых сообщений).

Использовать типизированные даты (LocalDate, Instant) и форматирование через java.time + DateTimeFormatter.

Добавить UI-тесты на Compose (Navigation + состояние экранов).

Реализовать поиск и сортировку пользователей (по имени, возрасту, стране).

Добавить сохранение выбранных фильтров (пол / национальность) между сессиями.

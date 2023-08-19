# loan-offer-service

## Настройка БД

1. **Создать БД**

```
create database loan_offer;
```

2. **Создать схему в БД loan_offer**

```
   create schema data;
```

3. **Настроить файл пропертей**

**application.yml** предоставляется вместе с jar файлом.
Для работы сервиса необходимо настроит:

1. Доступ к PostgreSQL.


4. **Собрать проект с помощью Maven**

5. **Запустить сервис**

Версия java

```
% java -version                                                                                          
openjdk version "20.0.1" 2023-04-18
OpenJDK Runtime Environment (build 20.0.1+10)
OpenJDK 64-Bit Server VM (build 20.0.1+10, mixed mode, sharing)
```

При запуске нужно указать расположение **application.yml**.

Пример запуска:

```
java -jar loan-offer-service-0.0.1-SNAPSHOT.jar --spring.config.location=/service/src/main/resources/application.yml 
```

5. **Открыть приложение в браузере**
   Перейти по ссылке в браузере:

```
   localhost:8080/index.html
```
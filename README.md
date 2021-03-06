# data-stream-testing-app
Тестовый сервис по дата-стримингу

Сервис построен с использование Spring Boot, в частности был использован стратер Spring Boot Starter Web.
JDK - 1.8
Maven - 3.6.3
TestNG - 7.1.0 

В качестве дополнения к исходной спецификации был реализован класс DataTransaction, имеющий состояние для данной транзакции.

В конце данного readme перечислен список возможных доработок (на усмотрение заказчика). 

Основные сервисы: 

1. DataAdapter - релизует интерфейс TransportInterface для работы с файлами данных. Все созданные файлы хранит в директории data.
Внутри cервиса переопределены 3 метода интерфейса:

а) initLoad - генерирует уникальный id транзакции, инициирует начальное состояние транзакции, добавляет транзакцию в общий мап с транзакциями. 

b) loadResult - принимает уникальный id транзакции и класс LoadResult, содержащий список контейнеров с данными (DataContainerDto). Метод проверяет текущее состояние транзакции - если состояние корректное то идет обход списка контейнеров и вызывает по каждому метод process. Метод process в свою очередь определяет какую операцию нужно проделать с контейнером и вызывает соответвующие приватные методы. После обработки контейнеров идет сбор кодов ResultCodeEnum и идеть проверка на наличие ResultCodeEnum.ERROR. Если такой присутствует в списке то весь результат обработки помечается как ResultCodeEnum.ERROR (тут я руководствовался принципом транзакций - либо выполняем все либо ничего. Да, это реализовано не полностью, об этом подробнее в списке возможных доработок). 

с) finish - принимает id транзакции, проверяет ее состояние, в случае корректного состояния - переводит его в финальную стадию и возвращает объект LoadResult с добавленным в него ResultCodeEnum.

Также внутри сервиса есть ряд приватных методов для обработки контейнеров. 

1. FileServiceImpl - реализует интерфейс FileService. 
Данный сервис нужен для отделения логики работы с файлами и директориями (создание, запись, удаление от логики обработки контейнеров.

Другие объекты:
1. ByteConverter - кастомный конвертер который можно использовать для сериализации/десериализации массивов байт. Может пригодиться если добавлять контроллеры и создавать модели для них. 

2. Пакет model - содержит основные модели необходимые для работы сервисов. В пакете exception лежат кастомные исключения для уточнения ошибки с точки зрения бизнес-логики. 


Тесты: 
В тестах используется TestNG из-за удобства работы с дата-провайдерами. В общей сложности 28 тестов (включая дата провайдеры) на весь функционал сервиса DataAdapter + негативные кейсы. В качестве хелпера при работе с файлам используется класс DataAdapterTestUtils. После каждого теста директория data очищается. У каждого теста есть свое состояние, они независимы друг от друга. Подготовка состояния происходит в самих тестах. 

Список возможных доработок:

1. Добавление контроллеров использующих сервис DataAdapter и написание тестов уже на WebLayer. 
2. Реализация паттернов State и Memento. Это позволит более гибко управлять состоянием транзакций а также осуществлять откат транзакции в случае возникновения ошибки. 
3. Реализация паттерна Command для храния истории обработки транзакций и как вариант реализация асинхронной обработки контейнеров за счет реализации очередей. 

Команды для запуска: 
Чтобы запустить тесты нужно выполнить коммунды mvn clean test-compile surefire:test
Это запустит все тесты.

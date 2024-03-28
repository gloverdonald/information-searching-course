# information-searching-course

- Столповский Михаил(гр. 11-002)
- Райманова Милана(гр. 11-002)

В проекте два main класса:
- LinkGeneratorMain - собирает ссылки с habr.com и сохраняет их в файл
- CrawlerMain - собирает html из списка ссылкой в отдельные файлы

---
#### Для сборки проекта используется [gradle](https://gradle.org/).

## Задание №1
#### Чтобы сгенерировать файл с ссылками нужно выполнить команду:
```
gradle linkGenerate
```

#### Чтобы сгенерировать html файлы нужно выполнить команду:
```
gradle crawle
```

## Задание №2
#### Чтобы сгенерировать токены и леммы нужно выполнить команду:
```
gradle tokensAndLemmas
```

## Задание №3
#### Чтобы сгенерировать инвертированный список нужно выполнить команду:
```
gradle сreateIndex
```

#### Чтобы запустить поиск выполнить команду:
```
gradle search
```
чтобы завершить поиск нужно написать команду `!exit`

##### Демонстрация работы:
![search.gif](search.gif)

## Задание №4
#### Чтобы сгенерировать tf-idf по токенам и леммам нужно выполнить команду:
```
gradle createTfIdf
```

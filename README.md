[По-русски](readme_files%2FREADME_rus.md)

# Filmorate

---

#### *_The backend for a service that works with films and user ratings, and also returns the top 5 films recommended for viewing_*

There are too many films and their number is increasing every day!
This application can help everyone make the good choise
to watch a movie based on user likes.
![Difficult choice.gif](readme_files%2FDifficult%20choice.gif)

---

This application provides:

1. Ability to store basic information about users and movies;
2. Ability to interactions between users:
    - adding/deleting to/from friend list;
    - getting a list with all the user's friends;
    - getting a list with all mutual friends between two users.
3. Ability to work with information about films within the application:
    - adding/removing/updating a film information in the database;    
    - getting a list with all saved movies;
    - adding/removing a like to/from a movie;
    - getting a list of the most popular films of a given size.
4. Information about the directors:
    - display of all films of the director, sorted by the number of likes;
    - display of all films of the director, sorted by year;
    - display a list of all directors;
    - getting director by id;
    - creating data about the director;
    - changing information about the director.
5. Working with user reviews:
    - adding a new review;
    - editing an existing review;
    - deleting an existing review;
    - receiving feedback by identifier;
    - receiving all reviews by movie id (if not specified, then all). If the quantity is not specified, then 10;
    - the ability to like/dislike a review.
6. Added the ability to view the latest events on the platform - adding as a friend, deleting from friends, likes and reviews left by the user’s friends.
7. A simple recommendation system for movies has been implemented (based on movie likes/ratings).
8. Implemented the display of movies shared with a friend, sorted by their popularity.
9. The ability to display the top N films by the number of likes with filtering according to two parameters has been implemented:
    - by genre;
    - for the specified year.
10. Implemented a search by movie title and director by substring.
---
The application is based on the Spring Boot framework v. 2.7.9;

Build system - Apache Maven;

Database - h2;

Accessing the database and mapping entities - spring-boot-starter-data-jdbc;

Testing - JUnit;

---
Instructions for running the application locally:
1. Required software
- Git (installation guide option - https://learn.microsoft.com/ru-ru/devops/develop/git/install-and-set-up-git);
- JDK (java SE11+, version of the installation guide - https://blog.sf.education/ustanovka-jdk-poshagovaya-instrukciya-dlya-novichkov/);
- Apache Maven (installation guide option on Windows - https://byanr.com/installation-guides/maven-windows-11/).
2. Launch terminal/command line/PowerShell, execute the commands one by one, waiting for each one to complete:
```
cd {целевая директория для загрузки проекта}

git clone git@github.com:RuslanYapparov/java-filmorate.git

mvn package

cd target

java -jar filmorate-0.0.1-SNAPSHOT.jar

```
3. Once launched, the application will accept http requests according to the API definition (see below) on port 8080 (http://localhost:8080/).
4. To run a test script, you can use the test collection (see below).
---

API description (OpenAPI): 

[filmorate_api(openapi).json](readme_files%2Ffilmorate_api%28openapi%29.json)

to view you need to copy and open the content in Swagger editor

---

Postman Test Collection: 

[filmorate.postman_collection.json](readme_files%2Ffilmorate.postman_collection.json)

import a collection by copying the contents into the field as Raw text

---

The database of the program has an ER-diagram (created with dbdiagram.io):

![filmorate_er_diagram.jpg](readme_files%2Ffilmorate_er_diagram.jpg)

---
The following people took part in the development of the application:
- SergeevViktor
- Sergeyvot
- DenchicK-64
- Rass00032
---

This application made with Java. Code example:
```java
public class Filmorate {
    public static void main(String[] args) {
        System.out.println("Let's start liking films!");
    }
}
```
![GoodWork.jpg](readme_files%2FGoodWork.jpg)

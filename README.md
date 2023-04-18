# java-filmorate
---

#### This is repository for homework project **_Filmorate_**

There are too many films and their number is increasing every day!
This application can help everyone make the good choise
to watch a movie based on user likes.

---

This application provides:

1. Ability to store basic information about users and movies;
2. Ability to interaction between users:
    - adding/deleting to/from friend list;
    - getting a list with all the user's friends;
    - getting a list with all mutual friends between two users.
3. Ability to work with information about films within the application:
    - adding/removing/updating a film information in the database;    
    - getting a list with all saved movies;
    - adding/removing a like to/from a movie;
    - getting a list of the most popular films of a given size.

---

The application can run on http-server with application programming interface:


---

The database of the program has an ER-diagram (created with dbdiagram.io):

![](/src/main/resources/filmorate_er_diagram.jpg)

Here are some examples of SQL-queries to the database:

1) Getting a list of user's friends by '*userId from request*':

```SQL
SELECT *
FROM users 
WHERE user_id IN (SELECT friend_id 
                  FROM friendship 
                  WHERE user_id = <userId from request> AND
                        confirmed = true
                  UNION
                  SELECT user_id
                  FROM friendship
                  WHERE friend_id = <userId from request> AND
                        confirmed = true);
```

2) Getting a list of mutual friends of two users by '*userId from request*' and '*friendId from request*':

```SQL
SELECT *
FROM users 
WHERE user_id IN (SELECT fs1.friend_id 
                  FROM friendship AS fs1
                  WHERE fs1.user_id = <userId from request> AND
                        fs1.confirmed = true
                  INNER JOIN friendship AS fs2 ON 
                             fs2.user_id = <friendId from request> AND
                             fs2.confirmed = true
                  UNION
                  SELECT fs1.user_id 
                  FROM friendship AS fs1
                  WHERE fs1.friend_id = <userId from request> AND
                        fs1.confirmed = true
                  INNER JOIN friendship AS fs2 ON 
                             fs2.friend_id = <friendId from request> AND
                             fs2.confirmed = true);
```

3) Getting a list of users who liked a movie using '*filmId from request*':

```SQL
SELECT *
FROM users 
WHERE user_id IN (SELECT user_id 
                  FROM likes
                  WHERE film_id = <filmId from request>);
```

4) Getting a list of the most liked movies with '*size-parameter from request*':
```SQL
SELECT *
FROM films
WHERE film_id IN(SELECT film_id
                 FROM likes
                 GROUP BY film_id
                 ORDER BY COUNT(user_id) DESC
                 LIMIT <size-parameter from request>);
```


---

This application made with Java. Code example:
```java
public class Filmorate {
    public static void main(String[] args) {
        System.out.println("Let's start liking films!");
    }
}
```
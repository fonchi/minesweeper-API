# Minesweeper API - Deviget Challenge 

This is an implementation of minesweeper classic game to **[Deviget interview challenge](CHALLENGE.md)**.

It's development is based in REST API using Spring Boot Framework and Java 1.8.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

What things you need to install project

    - GIT
    - Maven 3

*(depends on the chosen profile, to run project you also need)*

For Local Environment:

    - Java 1.8
    - MongoDB Community Edition 4.4.3

For Docker Environment:

    - Docker Desktop

For IDE Setup:

    - Lombok Plugin

### To Install

    git clone https://github.com/fonchi/minesweeper-API.git
    cd minesweeper-API
    mvn clean install

### To Run Manual

    mvn spring-boot:run -Dspring-boot.run.profiles=local

### To Run using Docker

    ./mvnw package
    docker build -t minesweeper-api-image .
    docker-compose up
    
## API Resources

- [POST /users](#create-user)
- [POST /users/{username}/boards](#create-board)
- [POST /users/{username}/boards/reveals](#reveal-cell)
- [POST /users/{username}/boards/flags](#flag-cell)
- [GET /users/{username}/boards/{board_id}](#get-board)

### Create User

*Endpoint to create new game users.*

Example: POST https://avallone-minesweeper-api.herokuapp.com/users

Headers:

    Content-Type: application/json

Request Body:

    {
        "username": "test",
        "email": "user@test.com",
        "password": "1234"
    }
    
Response Body:

    {
        "id": "20c06884f2fc436c8b90ee8ac4fb1725",
        "username": "test",
        "email": "user@test.com",
        "creation_datetime": "2021-02-21T04:55:29.787Z"
    }
    
Response Status: 

    HTTP 200-OK

CURL:

    curl --location --request POST 'https://avallone-minesweeper-api.herokuapp.com/users' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "username": "test",
        "email": "user@test.com",
        "password": "1234"
    }'

### Create Board

*Endpoint to create new game boards based on the parameters: number of rows, columns and mines.*

Example: POST https://avallone-minesweeper-api.herokuapp.com/users/test/boards

Headers:

    Content-Type: application/json

Request Body:

    {
        "row_size": 3,
        "col_size": 3,
        "mines_amount": 2
    }
    
Response Body:

    {
        "board_id": "6ee81774efe04486b67a9f0d8e6b1fed",
        "row_size": 2,
        "col_size": 2,
        "mines_amount": 1,
        "status": "new",
        "creation_datetime": "2021-02-22T02:51:06.113Z",
        "board_cells": [
            {
                "row": 0,
                "col": 0,
                "status": "hidden",
                "minesAround": 1,
                "mined": false
            },
            {
                "row": 0,
                "col": 1,
                "status": "hidden",
                "minesAround": 0,
                "mined": true
            },
            {
                "row": 1,
                "col": 0,
                "status": "hidden",
                "minesAround": 1,
                "mined": false
            },
            {
                "row": 1,
                "col": 1,
                "status": "hidden",
                "minesAround": 1,
                "mined": false
            }
        ]
    }
    
Response Status: 

    HTTP 200-OK

CURL:

    curl --location --request POST 'https://avallone-minesweeper-api.herokuapp.com/users/test/boards' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "row_size": 2,
        "col_size": 2,
        "mines_amount": 1
    }'

### Reveal Cell

*Endpoint to reveal cell through coordinates: row and column selected.*

Example: POST https://avallone-minesweeper-api.herokuapp.com/users/test/boards/6ee81774efe04486b67a9f0d8e6b1fed/reveals

Headers:

    Content-Type: application/json

Request Body:

    {
        "selected_row_num": 1,
    	"selected_col_num": 1
    }
    
Response Body:

    {
        "board_id": "6ee81774efe04486b67a9f0d8e6b1fed",
        "selected_row_num": 2,
        "selected_col_num": 2,
        "status": "playing",
        "creation_datetime": "2021-02-22T02:51:06.113Z",
        "started_datetime": "2021-02-22T02:53:51.718Z",
        "finished_datetime": null,
        "seconds_elapsed": 0,
        "board_cells": [
            {
                "row": 0,
                "col": 0,
                "status": "hidden",
                "minesAround": 1,
                "mined": false
            },
            {
                "row": 0,
                "col": 1,
                "status": "hidden",
                "minesAround": 0,
                "mined": true
            },
            {
                "row": 1,
                "col": 0,
                "status": "hidden",
                "minesAround": 1,
                "mined": false
            },
            {
                "row": 1,
                "col": 1,
                "status": "visible",
                "minesAround": 1,
                "mined": false
            }
        ]
    }
    
Response Status: 

    HTTP 200-OK

CURL:

    curl --location --request POST 'https://avallone-minesweeper-api.herokuapp.com/users/test/boards/6ee81774efe04486b67a9f0d8e6b1fed/reveals' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "selected_row_num": 1,
    	"selected_col_num": 1
    }'

### Flag Cell

*Endpoint to add flag on cell through coordinates: row and column selected.*

Example: POST https://avallone-minesweeper-api.herokuapp.com/users/test/boards/6ee81774efe04486b67a9f0d8e6b1fed/flags

Headers:

    Content-Type: application/json

Request Body:

    {
        "selected_row_num": 0,
        "selected_col_num": 0
    }
    
Response Body:

    {
        "board_id": "6ee81774efe04486b67a9f0d8e6b1fed",
        "selected_row_num": 2,
        "selected_col_num": 2,
        "status": "playing",
        "creation_datetime": "2021-02-22T02:51:06.113Z",
        "started_datetime": "2021-02-22T02:53:51.718Z",
        "finished_datetime": null,
        "seconds_elapsed": 178,
        "board_cells": [
            {
                "row": 0,
                "col": 0,
                "status": "flagged",
                "minesAround": 1,
                "mined": false
            },
            {
                "row": 0,
                "col": 1,
                "status": "hidden",
                "minesAround": 0,
                "mined": true
            },
            {
                "row": 1,
                "col": 0,
                "status": "hidden",
                "minesAround": 1,
                "mined": false
            },
            {
                "row": 1,
                "col": 1,
                "status": "visible",
                "minesAround": 1,
                "mined": false
            }
        ]
    }
    
Response Status: 

    HTTP 200-OK

CURL:

    curl --location --request POST 'https://avallone-minesweeper-api.herokuapp.com/users/test/boards/6ee81774efe04486b67a9f0d8e6b1fed/flags' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "selected_row_num": 0,
    	"selected_col_num": 0
    }'

### Get Board

*Endpoint to get user game board*

Example: GET https://avallone-minesweeper-api.herokuapp.com/users/test/boards/6ee81774efe04486b67a9f0d8e6b1fed
    
Response Body:

    {
        "board_id": "6ee81774efe04486b67a9f0d8e6b1fed",
        "row_size": 2,
        "col_size": 2,
        "mines_amount": 1,
        "status": "playing",
        "creation_datetime": "2021-02-22T02:51:06.113Z",
        "board_cells": [
            {
                "row": 0,
                "col": 0,
                "status": "flagged",
                "minesAround": 1,
                "mined": false
            },
            {
                "row": 0,
                "col": 1,
                "status": "hidden",
                "minesAround": 0,
                "mined": true
            },
            {
                "row": 1,
                "col": 0,
                "status": "hidden",
                "minesAround": 1,
                "mined": false
            },
            {
                "row": 1,
                "col": 1,
                "status": "visible",
                "minesAround": 1,
                "mined": false
            }
        ]
    }
    
Response Status: 

    HTTP 200-OK

CURL:

    curl --location --request GET 'https://avallone-minesweeper-api.herokuapp.com/users/test/boards/6ee81774efe04486b67a9f0d8e6b1fed'

## API Documentation

https://avallone-minesweeper-api.herokuapp.com/swagger-ui/

## Decisions Taken

### Framework & Programming Lenguage

It was decided to use Java because it's required by the job position applied and Spring Boot like a development framework since it is a very powerful tool for building APIs and is very efficient for the development process.

Instead of Spring, the api could have been built using a lighter framework like Spark which is more optimal to scalable solutions, making use libraries like Google Guice for dependency injection and
Google Gson for serialization and deserialization of Java objects, among others. But its implies a more complex development process and it is not justified for the challenge.

### Persistence Technology

It was decided to use a NoSQL database instead of a relational database, making use of MongoDB, because they are more conducive to this type of use where it is required to store arrays of objects, especially due to its key-value storage scheme based on documents with JSON format, which is appropriate in the use of maps and they are also faster for reading and writing of this type of structures and they are more scalable.

### Deployment Tools

It was used Heroku like cloud application platform for production deploy because it's straightforward and allows a free plan to non-commercial apps. It have a disadvantage for free apps that the dynos to pause and keep idling when app hasn't traffic. 

It was used Docker for local deploy because it's a standard container platform (and to avoid “It works in my machine”).

### Lombok Library

It was used Lombok to avoid repetitive code and make a cleaner code
  
## Final Comments

### Heroku Idling

When you will test endpoints in heroku production deployment, maybe it has a delay on response time on first call. It's caused by dynos pausing and keep idling after app stop receiving traffic.

One approach to solve it could be an adding ping endpoint and new relic addon to invoke it when monitor detects non-traffic.

### Possible Additional Features

- Endpoint GET User Boards (paged)
- Endpoint DELETE User
- Endpoint DELETE Board
- Endpoint GET User Board Stats
- User Authentication (using JWT and Spring Security)
- Games User Limitation
- Metrics and Dashboards (using DataDog or New Relic)

[![CircleCI](https://circleci.com/gh/mironouz/remote-school-api.svg?style=svg)](https://circleci.com/gh/mironouz/remote-school-api)

## Remote school rest api

COVID-19 pandemic has shown that many schools and universities were not well-prepared for remote education.
Many students send their homework to different places: every kind of messengers, social networks etc. because
of lack of dedicated place. The heads of teachers are going to explode.

The goal of this project is to provide simple and convenient API for building such applications. 

## Benefits are:
 - Any custom frontend (mobile, web) can be written on top of this API which will be better for your use case.
 You get full control and can use helpful API methods as lego bricks. 
 - You will not depend on any external service which can be modified eventually and become paid. 
 - Your information stays private.
 
Api can be checked online at `https://mironouz.xyz` or locally at `localhost:8080` after starting 
the application with `./gradlew bootRun`

## Available APIs:

Insecure:

- Register the user

  POST `{server}/api/register`

  ```json
  {
    "name": "some_name",
    "surname": "some_surname",
    "grade": "FIFTH",
    "email": "some_email",
    "password": "some_password"
  }
  ```
- Check particular user exists

  POST `{server}/api/checkUser`
  
  ```json
  {
    "email": "some_email",
    "password": "some_password"
  }
  ```
  
Secure:

Every secure api request should be queried with basic authentication (email as the username).

- Send the message to the chat

  POST `{server}/api/message`

  ```json
  {
    "text": "some_text",
    "timestamp": "timestamp"
  }
  ```
  **timestamp field is deprecated and will be removed soon**
  
- Connect to the chat SSE stream. Hot stream is used, so you will not observe old messages.

  GET `{server}/api/messages`
  
- Publish the exercise

  POST `{server}/api/exercise`  
  
  ```json
  {
    "title": "some_title",
    "description": "some_description"
  }
  ```
  
- List all available exercises

  GET `{server}/api/exercises` 
  
- List particular exercise

  GET `{server}/api/exercise/{id}`
  
Demo react frontend can be checked in this [repository](https://github.com/mironouz/remote-school-react) or online at
https://mironouz.xyz 
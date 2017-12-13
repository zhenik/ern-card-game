### Branches
DEVELOP  
[![Build Status](https://travis-ci.com/NikitaZhevnitskiy/ern-card-game.svg?token=6FYqXrfAk2ZHo34Tq8Gp&branch=develop)](https://travis-ci.com/NikitaZhevnitskiy/ern-card-game)  
MASTER  
[![Build Status](https://travis-ci.com/NikitaZhevnitskiy/ern-card-game.svg?token=6FYqXrfAk2ZHo34Tq8Gp&branch=master)](https://travis-ci.com/NikitaZhevnitskiy/ern-card-game)  



### SWAGGER
HOST:PORT/${path}/swagger-ui.html

### Gateway
GET & POST  
`localhost:8080/entities`  
Swagger UI  
`http://localhost:8080/game/api/swagger-ui.html`  

## How to test the application

1. Run `mvn package`
2. Run Docker-compose in main folder(Be sure to also read Docker section further down this document)
3. Use the included Postman collection for testing, or manually test the endpoints.

### Documentation for API's (SWAGGER)
First make sure to start docker-compose:

- All API's should be available under: localhost:10000/api/v1/
- For getting the Swagger's auto-generated documentation, go to localhost:10000/api/v1/swagger-ui.html

It is also possible to manually start some endpoints by running them from IntelliJ. 
In that case all endpoints will have different ports, which is configured inside application.yml for each
API.
 


## Different emails inside the git log:
During the project, one of the members(Eirik) made a mistake when commiting. He forgot to properly set the Git config
on different computers and inside IntelliJ. Therefore it sometimes looks like more than 3 people have worked on the project.

However, all the commits were pushed from one Github user: [EirikSkogstad](https://github.com/EirikSkogstad)
All the different emails from this user are listed below, and is also registered on this Github account.
 - ze9ix@live.no
 - Skogeir15@student.westerdals.no
 - eirikskogstad13@gmail.com
 
## Docker

### Problems with Docker/Docker-compose
Due to the large amount of nodes we have in the projects, we sometimes encountered problems.
When we upped everything in Docker-compose, sometimes a few of the nodes/images would crash. 
After inspecting the logs and testing, we fixed the problem by upping the amount of memory Docker was allowed to use.
Having so many images packaged as FatJAR's will of course have a huge memory fotprint, so keep this in mind.

If some of the nodes crash, try to increase memory if you're using a MAC.
For Nikita 4GB on his MAC was too low, so we suggest increasing to higher than this. (8 GB perhaps)
 
### Remove all images 
docker rmi $(docker images -a -q)
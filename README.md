### Branches
DEVELOP  
[![Build Status](https://travis-ci.com/NikitaZhevnitskiy/ern-card-game.svg?token=6FYqXrfAk2ZHo34Tq8Gp&branch=develop)](https://travis-ci.com/NikitaZhevnitskiy/ern-card-game)  
MASTER  
[![Build Status](https://travis-ci.com/NikitaZhevnitskiy/ern-card-game.svg?token=6FYqXrfAk2ZHo34Tq8Gp&branch=master)](https://travis-ci.com/NikitaZhevnitskiy/ern-card-game)  




### Minimum requirements for grade C are fulfilled

### SWAGER
HOST:PORT/${path}/swagger-ui.html

### Gateway
GET & POST  
`localhost:8080/entities`  
Jaeger UI  
`localhost:16686`  
Swagger UI  
`http://localhost:8080/game/api/swagger-ui.html`  

### Tracing with [Opentracing](http://opentracing.io/) & [Jaeger](http://jaeger.readthedocs.io/en/latest/) 
`docker run -d -p5775:5775/udp -p6831:6831/udp -p6832:6832/udp   -p5778:5778 -p16686:16686 -p14268:14268 jaegertracing/all-in-one:latest`

### How to up
0. `mvn package`
1. run tracing docker img
2. run spring app


### Test coverage
`mvn clean install -Pcov-cobertura`


## Different emails inside the git log:
During the project, one of the members(Eirik) made a mistake when commiting. He forgot to properly set the Git config
on different computers and inside IntelliJ. Therefore it sometimes looks like more than 3 people have worked on the project.

However, all the commits were pushed from one Github user: [EirikSkogstad](https://github.com/EirikSkogstad)
All the different emails from this user are listed below, and is also registered on this Github account.
 - ze9ix@live.no
 - Skogeir15@student.westerdals.no
 - eirikskogstad13@gmail.com
 
### Remove all images 
docker rmi $(docker images -a -q)
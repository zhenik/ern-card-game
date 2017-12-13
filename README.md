### Branches
DEVELOP  
[![Build Status](https://travis-ci.com/NikitaZhevnitskiy/ern-card-game.svg?token=6FYqXrfAk2ZHo34Tq8Gp&branch=develop)](https://travis-ci.com/NikitaZhevnitskiy/ern-card-game)  
MASTER  
[![Build Status](https://travis-ci.com/NikitaZhevnitskiy/ern-card-game.svg?token=6FYqXrfAk2ZHo34Tq8Gp&branch=master)](https://travis-ci.com/NikitaZhevnitskiy/ern-card-game)  






### SWAGER
HOST:PORT/${path}/swagger-ui.html

### Gateway
GET & POST  
`localhost:8080/entities`  
Jaeger UI  
`localhost:16686`

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
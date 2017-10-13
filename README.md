### Gateway
GET & POST  
`localhost:8080/game/api/entities`  
Jaeger UI  
`localhost:16686`

### Tracing with [Opentracing](http://opentracing.io/) & [Jaeger](http://jaeger.readthedocs.io/en/latest/) 
`docker run -d -p5775:5775/udp -p6831:6831/udp -p6832:6832/udp   -p5778:5778 -p16686:16686 -p14268:14268 jaegertracing/all-in-one:latest`

### How to up
0. `mvn package`
1. run tracing docker img
2. run spring app
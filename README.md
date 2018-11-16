# Static Namer Plugin
This is [linkerd](https://linkerd.io/) namer plugin which provides supporting for static service discovery via raw configuration in
linkerd.yaml. 

## Building
This plugin is built with sbt. Run next sbt commands from the plugins directory:

```sbtshell
sbt assembly
```
This will produce the plugin fat-jar at /target/scala-2.12/.

## Installing
To install this plugin with linkerd, simply move the plugin jar into linkerd's plugin directory ($L5D_HOME/plugins). 
Don't forgive to define L5D_HOME environment variable. Then add a namers block in your linkerd config:

```yaml
namers:
- kind: io.static
  experimental: true
  services:
  - api:127.0.0.1 8080 * 2, 127.0.0.1 8081
  - web:service.com 6080
  
routers:
- protocol: http
  identifier:
    kind: io.l5d.path
    segments: 1
    consume: true
  dtab: |
    /svc/api => /#/io.static/api;
    /svc/web => /#/io.static/web;

  httpAccessLog: logs/access.log
  label: int
  servers:
  - port: 4140
    ip: 0.0.0.0
```

After starting linkerd, each request to 127.0.0.1:4140/api/* will be proxy to **api** service 127.0.0.1:8080/* or 127.0.0.1:8081/*.
It should be note that linkerd will proxy requests to 127.0.0.1:8080 two times often, than to 127.0.0.1:8081

## Static Namer Plugin

This namer provides support for static service discovery via linkerd.yaml.

# Building
This plugin is built with sbt. Run sbt from the plugins directory.

```sbtshell
sbt assembly
```
This will produce the plugin fat-jar at /target/scala-2.12/.

# Installing
To install this plugin with linkerd, simply move the plugin jar into linkerd's plugin directory ($L5D_HOME/plugins). 
Then add a namers block in your linkerd config:

```yaml
namers:
- kind: io.static
  experimental: true
  services:
  - service1:127.0.0.1 8080 * 2, 127.0.0.1 8081
  - service2:service.com 6080
  
routers:
- protocol: http
  identifier:
    kind: io.l5d.path
    segments: 1
    consume: false
  dtab: |
    /svc/japi => /#/io.static/japi;
    /svc/web => /#/io.static/web;

  httpAccessLog: logs/access.log
  label: int
  servers:
  - port: 4140
    ip: 0.0.0.0
```
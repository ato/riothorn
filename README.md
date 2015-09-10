# Riothorn

**STATUS:** Incomplete and experimental.

Renders [Riot](http://riotjs.com/) tags on the JVM using the Nashorn or Rhino JavaScript engines.
 
## Usage

```java
Riothorn riothorn = new Riothorn();
Tag tag = riothorn.compile("<hello><h1>Hello {opts.name}</h1></hello>");
String html = tag.renderJson("{\"name\": \"world\"}");
```

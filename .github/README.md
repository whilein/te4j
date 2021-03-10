## About the project

Te4j (Template Engine For Java) - simple template engine written on Java

There are no any dependencies in the jar, works really fast (`//todo benchmarks`), simple syntax

## Examples

```html
<p>Message: ^^ message ^^</p>
```

```java
class Pojo {
    String getMessage() {
        return "Hello world!";
    }
}

Pojo pojo = new Pojo();

Template<Pojo> template = Te4j.load(Pojo.class, "index.html");
String result = template.renderAsString(pojo);
// result = <p>Message: Hello world!</p>
```

Also, you are able to create custom template context

```java
TemplateContext ctx = Te4j.custom()
        // deletes repeating spaces, tabs, cr and lf from output
        .replace(Te4j.DEL_ALL)
        // you can choose which output type will be used
        // 
        // if you want to choose multiple output types
        // you can use | operator
        //
        // BYTES - renderAsBytes and renderTo will be optimized
        // STRING - renderAsString will be optimized
        .outputTypes(Te4j.BYTES | Te4j.STRING)
        .build();

Template<Pojo> template = ctx.load(Pojo.class, "index.html");
```

## Full Docs

[Click me](https://github.com/lero4ka16/te4j/wiki)

## Add as dependency

### Maven

```xml
<!-- WIP -->
```

### Gradle

```groovy
// WIP
```

## Build the project
1. Execute `./gradlew build`
2. Output file located at `output/te4j.jar`

## Working on

1. Docs
2. Publish to maven central

## Contact
[Vkontakte](https://vk.com/id623151994),
[Telegram](https://t.me/lero4ka85)

### Post Scriptum
I will be very glad if someone can help me with development.
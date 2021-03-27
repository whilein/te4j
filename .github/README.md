<!-- @formatter:on  -->

# Te4j

<div align="center">
  <a href="https://github.com/lero4ka16/te4j/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/lero4ka16/te4j">
  </a>

  <a href="https://discord.gg/ANEHruraCc">
    <img src="https://img.shields.io/discord/819859288049844224?logo=discord">
  </a>

  <a href="https://github.com/lero4ka16/te4j/issues">
    <img src="https://img.shields.io/github/issues/lero4ka16/te4j">
  </a>

  <a href="https://github.com/lero4ka16/te4j/pulls">
    <img src="https://img.shields.io/github/issues-pr/lero4ka16/te4j">
  </a>

  <a href="https://search.maven.org/artifact/com.github.lero4ka16/te4j">
    <img src="https://img.shields.io/maven-central/v/com.github.lero4ka16/te4j">
  </a>

  <!-- <a href="https://s01.oss.sonatype.org/content/repositories/snapshots/com/github/lero4ka16/te4j">
    <img src="https://img.shields.io/nexus/s/com.github.lero4ka16/te4j?server=https%3A%2F%2Fs01.oss.sonatype.org">
  </a> -->
</div>

## About the project

Te4j (Template Engine For Java) - Fastest and easy template engine

### Pros
- Extremely fast (`127k` renders per second on 4790K)
- Easy and simple syntax
- No dependencies

### Cons (`temporary`)

- No community :(
- Sometimes really bad code
- No javadocs
- Poor documentation

## Benchmarks

![](https://github.com/lero4ka16/template-benchmark/raw/master/results.png)

[Click me](https://github.com/lero4ka16/template-benchmark)

## Example

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
        // if you want to choose multiple output types,
        // you can use | operator
        //
        // BYTES - renderAsBytes and renderTo will be optimized
        // STRING - renderAsString will be optimized
        .outputTypes(Te4j.BYTES | Te4j.STRING)
        // btw you can enable hot reloading
        //
        // it does not impact performance,
        // but I recommend disabling it in production
        // for max. performance
        .enableHotReloading()
        .build();

Template<Pojo> template = ctx.load(Pojo.class, "index.html");
```

More examples in docs
## Full Docs
[Click me](https://github.com/lero4ka16/te4j/wiki)

## Add as dependency
<div>
  <a href="https://search.maven.org/artifact/com.github.lero4ka16/te4j">
    <img src="https://img.shields.io/maven-central/v/com.github.lero4ka16/te4j">
  </a>

  <!-- <a href="https://s01.oss.sonatype.org/content/repositories/snapshots/com/github/lero4ka16/te4j">
    <img src="https://img.shields.io/nexus/s/com.github.lero4ka16/te4j?server=https%3A%2F%2Fs01.oss.sonatype.org">
  </a> -->
</div>

### Maven
```xml
<dependencies>
    <dependency>
        <groupId>com.github.lero4ka16</groupId>
        <artifactId>te4j</artifactId>
        <version>1.1.1</version>
    </dependency>
</dependencies>
```

### Gradle
```groovy
dependencies {
    implementation 'com.github.lero4ka16:te4j:1.1.1'
}
```

## Build the project

1. Execute `./gradlew build`
2. Output file located at `build/libs/te4j.jar`

## Contact

[Vkontakte](https://vk.com/id623151994),
[Telegram](https://t.me/lero4ka85)

### Post Scriptum

I will be very glad if someone can help me with development.

<!-- @formatter:off  -->
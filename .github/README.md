<!-- @formatter:off  -->

# Te4j

<div align="center">
  <a href="https://github.com/whilein/te4j/blob/master/LICENSE">
    <img src="https://img.shields.io/github/license/whilein/te4j">
  </a>

  <a href="https://discord.gg/ANEHruraCc">
    <img src="https://img.shields.io/discord/819859288049844224?logo=discord">
  </a>

  <a href="https://github.com/whilein/te4j/issues">
    <img src="https://img.shields.io/github/issues/whilein/te4j">
  </a>

  <a href="https://github.com/whilein/te4j/pulls">
    <img src="https://img.shields.io/github/issues-pr/whilein/te4j">
  </a>

  <a href="https://search.maven.org/artifact/com.github.whilein/te4j">
    <img src="https://img.shields.io/maven-central/v/com.github.whilein/te4j">
  </a>

  <!-- <a href="https://s01.oss.sonatype.org/content/repositories/snapshots/com/github/whilein/te4j">
    <img src="https://img.shields.io/nexus/s/io.github.whilein/te4j?server=https%3A%2F%2Fs01.oss.sonatype.org">
  </a> -->
</div>

## About the project

Te4j (Template Engine For Java) - Fastest and easy template engine

### Pros
- Extremely fast (`132k` renders per second on 4790K)
- Easy and simple syntax
- No dependencies

### Cons (`temporary`)

- No community :(
- Sometimes really bad code
- No javadocs
- Poor documentation

## Benchmarks

![](https://github.com/whilein/template-benchmark/raw/master/results.png)

[Click me](https://github.com/whilein/template-benchmark)

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

Template<Pojo> template = Te4j.load(Pojo.class).from("index.html");
String result = template.renderAsString(pojo);
// result = <p>Message: Hello world!</p>
```

Also, you are able to create custom template context

```java
TemplateContext ctx = Te4j.custom()
        // deletes repeating spaces, tabs, cr and lf from output
        .minify(Minify.DEL_LF, Minify.DEL_LF, Minify.DEL_TABS, Minify.DEL_REPEATING_SPACES)
        // .minify(Minify.getValues())
        // .minifyAll()

        // you can choose which output type will be used
        //
        // BYTES - renderAsBytes and renderTo will be optimized
        // STRING - renderAsString will be optimized
        .output(Output.BYTES, Output.STRING)
        // .output(Output.getValues())
        // .outputAll()

        // btw you can enable hot reloading
        //
        // it does not impact performance,
        // but I recommend disabling it in production
        // for max. performance
        .enableAutoReloading()
        .build();

Template<Pojo> template = ctx.load(Pojo.class).from("index.html");
```

More examples in docs
## Full Docs

[Click me](https://github.com/whilein/te4j/wiki)

## Add as dependency

<div>
  <a href="https://search.maven.org/artifact/com.github.whilein/te4j">
    <img src="https://img.shields.io/maven-central/v/com.github.whilein/te4j">
  </a>

  <!-- <a href="https://s01.oss.sonatype.org/content/repositories/snapshots/com/github/whilein/te4j">
    <img src="https://img.shields.io/nexus/s/com.github.whilein/te4j?server=https%3A%2F%2Fs01.oss.sonatype.org">
  </a> -->
</div>

### Maven
```xml
<dependencies>
    <dependency>
        <groupId>io.github.whilein</groupId>
        <artifactId>te4j</artifactId>
        <version>1.0</version>
    </dependency>
</dependencies>
```

### Gradle
```groovy
dependencies {
    implementation 'io.github.whilein:te4j:1.0'
}
```

## Build the project

1. Execute `./gradlew build`
2. Output file located at `build/libs/te4j.jar`

## Contact

[Vkontakte](https://vk.com/id623151994),
[Telegram](https://t.me/whilein)

### Post Scriptum

I will be very glad if someone can help me with development.

<!-- @formatter:on  -->
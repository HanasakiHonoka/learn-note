# Java 中的 SPI 机制

> SPI（Service Provider Interface，服务提供者接口）用于让框架或应用定义扩展契约，由外部提供实现，再按约定发现和使用实现。本文以 JDK `ServiceLoader` 为主，示例使用 Java 17；同时区分 Spring、Dubbo 等框架自己的扩展机制。

## 一、SPI 解决什么问题

假设一个文本处理框架允许接入不同的格式化策略。如果框架直接写 `new UpperCaseFormatter()`，每增加一种实现，都需要修改框架代码或维护实现类清单。

采用 SPI 后，框架只依赖 `TextFormatter` 接口，扩展方实现接口并发布注册信息。应用装配相应 JAR 后，框架通过发现机制得到实现，再按业务规则选择使用。

```text
编译期依赖：框架 ──> SPI 接口 <── 实现方
运行时装配：应用提供实现 JAR ──> 读取注册声明 ──> 加载实现 ──> 按规则使用
```

这里的“服务”通常是同一 JVM 内的一种能力，如驱动、编解码器、存储适配器，不代表远程微服务。广义 SPI 是扩展契约与发现思想，JDK `ServiceLoader` 是一种具体实现，不是所有称为 SPI 的框架机制都调用它。

### 1. 三个角色

| 角色 | 职责 | 示例 |
| --- | --- | --- |
| 服务契约 | 定义宿主需要的扩展能力，通常为接口或抽象类 | `TextFormatter` |
| 服务提供者（Provider） | 实现能力并声明注册信息 | 大写、小写格式化器 |
| 服务使用者 / 宿主 | 发现提供者，决定选择、调用与生命周期 | 文本处理框架或应用 |

SPI 的收益是把实现替换从“修改宿主源码”转为“装配兼容的实现并配置选择规则”。运行时仍然需要实现及其依赖，不能把“编译时不依赖具体实现”理解为“部署时不用带实现”。

### 2. SPI 与 API、DI、反射的区别

| 概念 | 核心问题 | 与 SPI 的关系 |
| --- | --- | --- |
| API | 使用者怎样调用已有能力？ | 例如业务调用 `format()`；同一个接口既可以供业务调用，也可以作为扩展契约 |
| SPI | 外部怎样为宿主提供、注册和替换能力？ | 重点是扩展方实现契约，由宿主发现并调用 |
| 依赖注入（DI） | 对象依赖如何由外部装配？ | SPI 可以参与装配，但 `ServiceLoader` 不负责完整的依赖图、字段注入与 Bean 生命周期 |
| 反射 | 运行时怎样检查类型、创建对象和调用方法？ | 是底层实现手段；手动 `Class.forName()` 不自动具备服务注册与发现协议 |
| 工厂 / 策略模式 | 怎样创建对象，或封装可替换算法？ | 可与 SPI 配合；工厂负责创建，策略负责算法，SPI 帮助发现扩展实现 |
| 注册中心 | 远程服务实例的地址与健康状态怎样发现？ | Nacos、ZooKeeper 等解决分布式服务发现，与 JDK 类路径内的提供者发现范围不同 |

API 与 SPI 的区别主要在使用角色和扩展方向，不在于“一个是接口、一个是实现类”。SPI 也不意味着实现自动成为 Spring Bean，或自动支持 `@Autowired`。

## 二、JDK SPI 的基本约定

### 1. Classpath 下的五个步骤

1. 定义服务接口或抽象类。
2. 编写服务提供者。在本文的 classpath 用法中，提供者是可实例化的 `public` 类，实现该接口并具有 `public` 无参构造器。
3. 在资源目录中创建 `META-INF/services/<服务类型的完整二进制名称>` 文件。顶层接口通常就是完整类名，如 `demo.spi.api.TextFormatter`。
4. 文件内每行写一个提供者的完整二进制名称，如 `demo.spi.provider.UpperCaseFormatter`。
5. 使用 `ServiceLoader.load(TextFormatter.class)` 获取加载器，通过迭代或流操作发现和使用实现。

普通 Maven / Gradle Java 项目通常将资源放在 `src/main/resources`，但 JDK 最终读取的是**运行时类路径中的资源**。只在源码目录创建文件，却没有将其复制进输出目录或 JAR，运行时仍然发现不了实现。

### 2. 配置文件的格式

文件名为 `META-INF/services/demo.spi.api.TextFormatter`，没有 `.txt`、`.properties` 等后缀。内容使用 **UTF-8**：

```text
# 一行一个实现类，不写 key=value
 demo.spi.provider.UpperCaseFormatter
 demo.spi.provider.LowerCaseFormatter
```

空行、首尾空格与制表符会被忽略，`#` 后是注释。同一个实现类在同一文件或多个资源文件中重复声明时会去重；**不同实现类返回相同业务名称并不会自动去重或自动确定优先级**。

多个 JAR 可以分别提供同名的服务配置资源，加载器会查找可见的声明；它不会扫描整个 classpath 中所有类来判断谁实现了接口。未注册的实现类通常不会凭借 `implements` 自动被发现。

## 三、完整示例：可插拔文本格式化器

为方便运行，下面把 API、两个提供者与宿主放在一个小项目中；实际框架通常将它们拆为 API JAR、提供者 JAR 和应用，宿主编译时仅依赖 API。

### 1. 目录结构

```text
spi-demo/
├── src/main/java/
│   └── demo/spi/
│       ├── api/TextFormatter.java
│       ├── provider/UpperCaseFormatter.java
│       ├── provider/LowerCaseFormatter.java
│       └── app/Main.java
└── src/main/resources/
    └── META-INF/services/demo.spi.api.TextFormatter
```

### 2. 定义服务契约

```java
// src/main/java/demo/spi/api/TextFormatter.java
package demo.spi.api;

public interface TextFormatter {
    String name();
    String format(String input);
}
```

`name()` 是本示例约定的业务标识，不是 JDK 内置的 SPI 名称功能。真实扩展点还应定义输入约束、异常语义、线程安全要求和兼容范围。

### 3. 提供两个实现

```java
// src/main/java/demo/spi/provider/UpperCaseFormatter.java
package demo.spi.provider;

import demo.spi.api.TextFormatter;
import java.util.Locale;

public final class UpperCaseFormatter implements TextFormatter {
    public UpperCaseFormatter() {}

    @Override
    public String name() {
        return "upper";
    }

    @Override
    public String format(String input) {
        return input.toUpperCase(Locale.ROOT);
    }
}
```

```java
// src/main/java/demo/spi/provider/LowerCaseFormatter.java
package demo.spi.provider;

import demo.spi.api.TextFormatter;
import java.util.Locale;

public final class LowerCaseFormatter implements TextFormatter {
    public LowerCaseFormatter() {}

    @Override
    public String name() {
        return "lower";
    }

    @Override
    public String format(String input) {
        return input.toLowerCase(Locale.ROOT);
    }
}
```

### 4. 注册提供者

`src/main/resources/META-INF/services/demo.spi.api.TextFormatter` 的内容为：

```text
# 两个独立的提供者
 demo.spi.provider.UpperCaseFormatter
 demo.spi.provider.LowerCaseFormatter
```

### 5. 发现、校验并按名称选择

```java
// src/main/java/demo/spi/app/Main.java
package demo.spi.app;

import demo.spi.api.TextFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public final class Main {
    public static void main(String[] args) {
        Map<String, TextFormatter> formatters = new HashMap<>();
        for (TextFormatter formatter : ServiceLoader.load(TextFormatter.class)) {
            String name = formatter.name();
            if (name == null || name.isBlank()) {
                throw new IllegalStateException("Formatter name must not be blank");
            }
            if (formatters.putIfAbsent(name, formatter) != null) {
                throw new IllegalStateException("Duplicate formatter: " + name);
            }
        }

        String requested = args.length == 0 ? "upper" : args[0];
        TextFormatter selected = formatters.get(requested);
        if (selected == null) {
            throw new IllegalArgumentException("No formatter: " + requested);
        }
        System.out.println(selected.format("Hello SPI"));
    }
}
```

宿主没有导入任何具体提供者。默认选择、按名称查找和名称冲突处理都由应用定义，JDK 不会替应用完成这些决策。这个示例遍历了所有提供者，所以会实例化它们；只有“发现所需提供者就停止”或先用元数据筛选时，才能避免创建后续不需要的实现。

### 6. 编译、运行与打包

以下命令在项目根目录执行，适用于 Windows PowerShell，要求 JDK 17+ 且 `javac`、`java`、`jar` 在 PATH 中。首次运行时 `out` 目录应为空；修改服务配置后应重新复制资源，避免旧文件影响结果。

```powershell
New-Item -ItemType Directory -Path out -Force | Out-Null
$sources = (Get-ChildItem -Path src/main/java -Filter *.java -Recurse).FullName
javac -encoding UTF-8 --release 17 -d out $sources
Copy-Item -LiteralPath src/main/resources/META-INF -Destination out -Recurse -Force
java -cp out demo.spi.app.Main upper
java -cp out demo.spi.app.Main lower
jar --create --file spi-demo.jar -C out .
java -cp spi-demo.jar demo.spi.app.Main upper
```

输出依次为 `HELLO SPI`、`hello spi`、`HELLO SPI`。可以用 `jar --list --file spi-demo.jar` 确认 JAR 内确实包含服务配置资源。真正拆分部署时，应同时把 API、应用、选定提供者及其依赖放在运行时类路径上。

## 四、ServiceLoader 的工作过程

对于上面的 **classpath** 示例，可以按以下过程理解：

```text
ServiceLoader.load(服务类型)
    -> 创建加载器，确定用于查找的 ClassLoader
    -> 随迭代逐步查找 META-INF/services/<服务名称>
    -> 解析提供者声明，校验与加载相关类型
    -> 需要实例时调用提供者的 public 无参构造器
    -> 返回提供者，供应用选择并调用
```

这是概念流程，不是某个 JDK 版本源码的逐行展开。Java 9+ 还支持模块化提供者，因此不能把所有 `ServiceLoader` 用法都简化成“读取一个文件后调用 `Class.forName()`”。

### 1. 懒加载与缓存

- `ServiceLoader.load()` 不等于立即实例化所有提供者。发现、加载与实例化通常随迭代或流的消费按需推进。
- 对同一个 `ServiceLoader` 反复迭代时，已实例化的提供者会从该加载器的缓存返回，未发现的部分继续按需处理。
- `ServiceLoader` 不是全局单例注册表。反复创建新的加载器，可能重复发现和创建提供者；不要在每个请求中都重新全量加载。
- 缓存的是加载器内部的发现结果 / 实例，不意味着提供者本身线程安全。实例能否共享应由扩展契约和实现保证。

### 2. 常用 API 与版本

| API | 用途 | 注意点 |
| --- | --- | --- |
| `ServiceLoader.load(S.class)` | 使用当前线程上下文类加载器查找服务 | `ServiceLoader` 自 Java 6 提供；上下文加载器的可见性会影响发现结果 |
| `load(S.class, classLoader)` | 显式指定类加载器 | 常用于插件或容器场景，仍需保证接口类型身份一致 |
| `iterator()` / 增强 for | 逐步返回服务实例 | `hasNext()`、`next()` 都可能因配置或加载问题报错 |
| `stream()` | 返回 `Provider<S>` 流，先查看类型等元数据，再按需 `get()` | Java 9+；不是直接得到实例流，也不保证整条流操作完全不加载类 |
| `findFirst()` | 获取首个可用提供者，结果为 `Optional<S>` | Java 9+；首个可用不等于业务上最高优先级 |
| `reload()` | 清除该加载器的缓存，让后续使用重新发现 | 不负责增加类路径、卸载类或关闭旧实例；旧迭代器 / 流应丢弃 |
| `load(layer, S.class)` | 从指定模块层及其祖先查找提供者 | Java 9+，适用于 JPMS 模块层 |

`stream()` 可以根据注解等类型元数据筛选，然后对选中的 `Provider` 调用 `get()`。显式命名模块使用静态 `provider()` 工厂时，`Provider.type()` 反映工厂方法声明的返回类型，不一定是工厂类或最终实例的具体类型；不要据此无条件读取某个实现类上的注解。

### 3. 顺序、异常、并发与生命周期

**选择顺序**：不要将 classpath 枚举或打包顺序当作业务优先级。JDK 不内置按业务名称、`@Order` 或 `@Primary` 选择提供者的协议；需要稳定选择时，应明确名称、能力匹配、优先级及冲突规则。

**错误类型**：错误的配置语法、找不到实现、类型不匹配、构造器不符合要求或实例化失败等，可能抛出 `ServiceConfigurationError`。它继承 `Error`，`catch (Exception)` 不能覆盖它，而且错误可能在遍历时才发生。对必需扩展通常应启动失败并给出诊断，不能无条件吞掉错误后假装功能可用。

**线程安全**：`ServiceLoader` 实例不保证多线程安全。可以在初始化阶段由单线程完成发现、校验，再安全发布只读的实现映射；映射只读仍不等于其中的提供者对象线程安全。

**生命周期**：`ServiceLoader` 不自动注入依赖，也不自动为提供者执行统一的关闭协议。涉及线程池、连接和文件句柄时，应由宿主明确管理；昂贵资源可以通过 SPI 工厂在真正使用时创建，避免把网络连接等副作用都放在提供者构造器中。

## 五、类加载器与 TCCL：为什么“依赖在，却发现不了”

### 1. 默认使用线程上下文类加载器

`ServiceLoader.load(TextFormatter.class)` 默认使用当前线程的上下文类加载器（TCCL）。这是为了让框架有机会发现应用或插件侧提供的实现，而不局限于定义框架类的加载器。

以 JDBC 为例，`java.sql` 中的标准契约由 JDK 提供，具体驱动通常在应用依赖中；只使用加载标准库自身的加载器，不一定能看到应用驱动。

TCCL 是一种选择查找视角的机制，不是取消父委派，也不会让所有加载器相互可见。容器、插件宿主、自定义线程池的上下文加载器不合适时，可能出现不同线程发现结果不同的问题。

### 2. 显式指定加载器

在需要固定查找范围的场景，可以显式传入加载器：

```java
ServiceLoader<TextFormatter> loader = ServiceLoader.load(
        TextFormatter.class,
        TextFormatter.class.getClassLoader()
);
```

此片段假定已导入前文类型。它只适用于“加载接口的加载器也能看到提供者”的场景；如果提供者只在子插件加载器中，应传入相应的插件加载器，并确保它共享宿主的 SPI 接口类型。

同名接口如果被两个隔离加载器分别定义，在 JVM 中是不同类型。插件把 SPI API 重复打包并由自己的加载器重新定义，可能引发 `not a subtype`、`ClassCastException` 或链接错误。通常应共享 API，把实现与其私有依赖隔离；长期缓存插件实例、加载器或 TCCL 还可能阻止回收。

## 六、Java 9+ 模块化 SPI（JPMS）

如果采用显式命名模块，应通过模块声明描述服务使用和提供关系，不能照搬只有 `META-INF/services` 的 classpath 做法。将前文示例拆成三个模块后，模块描述符可写为：

```java
// API 模块：导出服务契约。
module demo.formatter.api {
    exports demo.spi.api;
}
```

```java
// 提供者模块：通常不必导出实现包。
module demo.formatter.provider {
    requires demo.formatter.api;
    provides demo.spi.api.TextFormatter with
            demo.spi.provider.UpperCaseFormatter,
            demo.spi.provider.LowerCaseFormatter;
}
```

```java
// 宿主模块：声明要使用的服务，不直接依赖提供者模块。
module demo.formatter.app {
    requires demo.formatter.api;
    uses demo.spi.api.TextFormatter;
}
```

每个片段分别放在对应模块的 `module-info.java` 中。提供者及其依赖必须部署到可解析、可发现的模块路径或模块层；`uses` 声明本身不会下载或安装实现。

| 部署方式 | 提供者声明方式 | 创建实例的方式 |
| --- | --- | --- |
| 普通 classpath JAR | `META-INF/services/<服务名>` | 实现服务类型的公开类，具有公开无参构造器 |
| 显式命名模块 | `module-info.java` 中的 `provides ... with ...` | 公开无参构造器，或符合要求的 `public static provider()` 方法 |
| 自动模块 | 由 JAR 中的服务配置形成模块服务声明 | 使用公开无参构造器，不支持静态 `provider()` 创建方式 |

显式命名模块使用静态 `provider()` 时，该方法必须无参数，声明的返回类型可赋值给服务类型。工厂类本身可以不实现服务接口。**不能把这一特性套用到普通 classpath 提供者**；普通 JAR 只有静态工厂、没有合规构造器时，并不会因此被正确加载。

## 七、JDBC 为什么不再普遍需要 Class.forName

### 1. JDBC 4.0 的自动发现

早期 JDBC 程序通常显式执行 `Class.forName("某驱动类")`，通过类初始化促使驱动注册。JDBC 4.0 起，符合规范的驱动可通过服务提供者配置支持自动发现。

以现代 MySQL Connector/J 为例，驱动 JAR 通常包含：

```text
META-INF/services/java.sql.Driver
```

其中声明的驱动类为 `com.mysql.cj.jdbc.Driver`。历史版本可能使用其他类名，不能不看版本就照抄。

### 2. 发现、注册与连接不是同一件事

```text
应用调用 DriverManager 的相关入口
    -> 触发驱动初始化流程（时机依 JDK 实现）
    -> ServiceLoader 发现并初始化 JDBC Driver 提供者
    -> 常见驱动在类初始化时调用 DriverManager.registerDriver(...)
    -> DriverManager 按调用方可见性等规则遍历已注册驱动
    -> 尝试连接，驱动处理支持的 JDBC URL
```

这是常见实现的机制说明。现代 JDK 中驱动初始化可延迟到相关调用时发生，不能把原理固定描述为“加载 `DriverManager` 时必然在静态代码块立刻加载全部驱动”。

驱动的 `connect()` 对不支持的 URL 通常返回 `null`，连接失败则可能抛出 SQL 异常。SPI 负责提供者发现，驱动注册表及 URL 适配属于 JDBC / 驱动的后续协议。

因此，通常不再需要手工加载驱动的前提是：驱动版本支持自动发现、配置资源完整、实现及依赖可见。遇到 `No suitable driver` 时，还应检查 URL、驱动兼容性、加载器可见性和打包结果，而不是只补一行 `Class.forName()`。

## 八、JDK SPI、Spring 扩展机制与 Dubbo SPI

| 机制 | 典型声明 / 入口 | 主要特点 |
| --- | --- | --- |
| JDK `ServiceLoader` | `META-INF/services` 或 JPMS `uses/provides` | JDK 标准服务发现，框架依赖少；选择策略与生命周期由宿主决定 |
| Spring Bean 容器 | `@Bean`、组件扫描、配置类等 | 管理依赖注入、作用域、生命周期与代理，不自动把所有 JDK SPI 对象变成 Bean |
| Spring `SpringFactoriesLoader` | `META-INF/spring.factories` | 按约定读取工厂类型与实现列表，是 Spring 自己的发现工具，不等于 `ServiceLoader` |
| Spring Boot 自动配置 | 自动配置候选清单 + 导入流程 + 条件注解 | 按条件注册 Bean，并支持用户配置等覆盖机制，不只是加载几个实现类 |
| Dubbo SPI | 常见为 `META-INF/dubbo/` 等目录下的扩展声明 | 支持扩展名选择、自适应、包装等框架能力，具体能力与目录规则依版本而定 |

### 1. Spring 的 spring.factories

传统工厂声明常用“接口或扩展点类型 = 实现类列表”的格式，例如：

```properties
com.example.Extension=com.example.FirstExtension,com.example.SecondExtension
```

这只是格式示意，不是本文可运行示例。它与 JDK `META-INF/services` 的“一行一个实现类”格式不同，读取方与创建协议也不同。不要把文件放错位置后期待另一套加载器识别。

### 2. Spring Boot 自动配置的版本区别

- **Boot 2.6 及更早的常见做法**：通过 `spring.factories` 中的 `org.springframework.boot.autoconfigure.EnableAutoConfiguration` 键注册自动配置类。
- **Boot 2.7**：引入 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 作为自动配置注册方式，并兼容原有方式。
- **Boot 3.x 及后续版本**：自动配置注册使用上述 `.imports` 文件，不再支持用 `spring.factories` 的该键注册自动配置。

`.imports` 中每行写一个自动配置类。`spring.factories` 在其他扩展场景仍然可以使用，不能把“自动配置迁移”理解为“Spring 完全移除了该文件”。

自动配置候选发现后，还要经过条件判断和 Bean 定义注册；`@ConditionalOnClass`、`@ConditionalOnMissingBean` 等能力属于 Boot / Spring，不是 JDK SPI 的内置能力。

### 3. Dubbo 为什么要扩展 SPI

JDK SPI 没有内置完整的扩展名寻址、自动装配、装饰包装和运行时自适应选择协议。Dubbo 围绕自己的扩展点需求，提供更丰富的扩展管理能力，常见配置采用 `扩展名=实现类`。

这说明“SPI”是一类可扩展设计思想，而不是只能使用一套固定文件格式。讨论优劣时应明确具体框架与版本；不能说 JDK 完全不能按需加载，而应说它的按需发现、实例化不等于框架级的按名称路由与生命周期管理。

## 九、优点、局限与适用场景

| 方面 | 收益 | 对应成本或边界 |
| --- | --- | --- |
| 解耦与替换 | 宿主只依赖契约，提供者可独立发布 | 接口兼容、打包与依赖治理仍需维护 |
| 轻量 | JDK 即提供标准发现机制，无需额外容器 | 不包含完整 DI、条件装配、排序与生命周期管理 |
| 插件扩展 | 新增兼容实现通常不必修改宿主源码 | 类路径 / 模块路径可见性和错误配置会影响运行 |
| 多实现共存 | 可同时装配多个能力提供者 | 必须明确选哪个、是否组合、冲突与缺失如何处理 |
| 延迟创建 | 迭代 / 流消费时可按需创建实例 | 构造器过重或应用全量遍历仍会产生初始化开销 |

适合相对稳定的扩展点，如编解码器、规则插件、存储适配器、驱动与工具框架。仅有两个固定的业务分支时，显式工厂或策略映射可能更直接；实现需要复杂依赖装配时，可以考虑交给 DI 容器，或用 SPI 发现工厂后由宿主传入依赖。

SPI 也不是隔离不可信代码的沙箱。提供者与宿主通常在同一 JVM 内执行，应该像普通依赖一样管理来源、权限、版本与资源使用。

## 十、打包、排障与工程注意事项

### 1. 常见故障对照

| 现象 | 常见原因 | 检查方向 |
| --- | --- | --- |
| 没发现任何实现 | 没注册、资源未复制、提供者不可见 | 看运行时目录 / JAR 中的路径与文件名，检查实际 classpath、TCCL 或模块层 |
| `Provider ... not found` | 配置类名错误，实现或依赖未部署 | 检查完整类名、依赖树和打包内容；必要时进一步检查链接错误的根因 |
| `not a subtype` | 类没实现契约，或契约由不同加载器分别定义 | 确认实现关系以及双方接口的 `ClassLoader` |
| 无法获取公开无参构造器 | 类或构造器不可访问、非静态内部类等 | 对照部署方式检查提供者规则，别混用模块工厂与 classpath 规则 |
| 构造时失败 / `ServiceConfigurationError` | 提供者初始化异常、资源依赖失败或声明错误 | 查看完整异常链，定位具体提供者与根因 |
| 本地正常，合并 JAR 后少了实现 | 同名服务资源被覆盖或裁剪 | 检查最终服务清单，配置正确的资源合并与保留规则 |
| 选择结果随打包变化 | 把发现顺序当作业务优先级 | 显式定义名称、优先级和冲突校验 |
| 模块中找不到服务或报配置错误 | 未声明 `uses`、`provides`，或模块不可发现 | 检查描述符、模块路径与模块层 |

### 2. Fat JAR 与资源合并

多个依赖 JAR 可能都有同名的 `META-INF/services/<服务名>`。打成一个合并 JAR 时，如果资源被简单覆盖，就会丢失部分提供者声明。使用 Maven Shade 等工具时，可通过 `ServicesResourceTransformer` 合并服务资源，并验证重定位后的类名与最终内容。

不同打包器的规则不同。Spring Boot 可执行 JAR 常保留嵌套依赖，由其加载器读取；不能把所有 fat JAR 都当成同一种平铺合并方式。代码压缩、依赖裁剪和原生镜像也需要按工具链验证服务元数据与实现是否保留。

### 3. 版本兼容、发现成本与热更新

接口升级可能使旧提供者在调用新抽象方法时出现 `AbstractMethodError` 等兼容问题。默认方法有时能改善演进兼容性，但仍需审查语义；应为 SPI 制定版本与兼容策略，而不是只保证“类名没有改”。

对关键扩展，建议启动时完成发现、冲突检查与必要的兼容验证，明确缺失是失败还是降级；延迟加载能降低初始开销，但会把部分错误延后到运行时。

`reload()` 只是清理加载器缓存，不会自动将磁盘上新放的 JAR 加入既有类路径，不会终止旧实现，也不保证类卸载。完整热更新还需要加载器或模块层隔离、引用释放、资源关闭和调用切换协议，不能只调用一次 `reload()`。

## 十一、面试常见问答

**SPI 的核心思想是什么？** 宿主定义扩展契约，外部提供者实现并声明注册信息，宿主运行时发现并选择实现。它降低对具体实现的编译依赖，但仍需要部署兼容实现。

**SPI 和 API 是不是对立关系？** 不是。API 更关注调用已有能力，SPI 更关注为宿主提供扩展能力；同一个接口可以同时承担这两种角色。

**调用 load() 就把全部实现创建出来了吗？** 通常没有，发现与实例化是按需推进的。但应用一旦完整遍历获取实例，就会创建遍历到的提供者；需要区分加载器的机制与调用者的消费方式。

**为什么 JDBC 常常不用 Class.forName？** 支持 JDBC 4.0 自动发现的驱动通过服务声明被发现与初始化，随后参与 JDBC 的驱动注册与连接流程。前提是依赖、资源、加载器可见性和 URL 正确。

**JDK SPI 最大的局限是什么？** 它提供通用发现能力，但没有完整的名称路由、优先级、DI 和生命周期协议；接口兼容、错误处理与实例管理需要宿主设计。

**Spring Boot 自动配置是不是直接调用 JDK ServiceLoader？** 不能这样概括。Boot 有自己的候选发现、导入、条件判断和 Bean 注册流程，自动配置清单格式也随版本演进，应与 JDK SPI 分开解释。

## 十二、参考资料

- [Java 17 ServiceLoader API](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/ServiceLoader.html)：提供者约定、加载时机、错误与模块化用法。
- [ServiceLoader.Provider API](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/ServiceLoader.Provider.html)：`type()` 与 `get()` 的语义。
- [Java 17 DriverManager API](https://docs.oracle.com/en/java/javase/17/docs/api/java.sql/java/sql/DriverManager.html)：JDBC 驱动发现与连接管理。
- [SpringFactoriesLoader API](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/io/support/SpringFactoriesLoader.html)：Spring 工厂发现机制。
- [Spring Boot 自动配置文档](https://docs.spring.io/spring-boot/reference/features/developing-auto-configuration.html)：自动配置候选声明与条件装配。
- [Spring Boot 3.0 Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)：自动配置注册方式的迁移说明。
- [Maven Shade：资源转换器](https://maven.apache.org/plugins/maven-shade-plugin/examples/resource-transformers.html)：服务清单合并与 `ServicesResourceTransformer`。

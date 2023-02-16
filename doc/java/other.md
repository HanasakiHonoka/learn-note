# Java中的SPI注入机制

## 一 SPI机制
SPI ，全称为 `Service Provider Interface`，是一种服务发现机制。它通过在 ClassPath 路径下的 META-INF/services 文件夹查找文件（服务接口），自动加载文件里所定义的类（服务接口的具体实现类）。

当外部程序装配这个模块的时候，就能通过该 jar 包 META-INF/services/ 里的配置文件找到具体的实现类名，并装载实例化，完成模块的注入。这样就能很好的找到服务接口的实现类，而不需要再代码里制定。

JDK 提供服务实现查找的一个工具类：java.util.ServiceLoader

## 二. 具体步骤
2.1.定义一个接口

2.2.编写接口的实现类

2.3.在resources/META-INF/services目录下创建该接口的全限定类名，命名的文件

2.4.在命名的文件上添加某些该接口的实现类的全限定类名

2.5.通过java.util.ServiceLoader来加载实现该接口的实现类

## 实战

[待补充](https://blog.csdn.net/ccwangwang/article/details/127461086)

## 实际运用

JDBC中SPI机制的体现？

DriverManager类中通过静态代码块，java的spi机制，会去加载classpath下驱动包里的resources/META-INF/services文件下的驱动实现类
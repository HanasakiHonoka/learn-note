## CSS

### **盒子模型**

**盒子模型**分为两种 第一种是**W3c标准的盒子模型（标准盒模型）** 、第二种**IE标准的盒子模型（怪异盒模型）**。

**区别**：

**标准盒模型**中**width**指的是内容区域**content**的宽度；**height**指的是内容区域**content**的高度。**标准盒模型下盒子的大小** = **content** + **border** + **padding** + **margin**。

**怪异盒模型**中的**width**指的是内容、边框、内边距总的宽度（content + border + padding）；**height**指的是内容、边框、内边距总的高度。**怪异盒模型下盒子的大小=width（content + border + padding） + margin**。

**box-sizing**三个值：

**content-box**： *默认值*，border和padding不算到width范围内，可以理解为是W3c的**标准模型**(default)。

**border-box**：border和padding划归到width范围内，可以理解为是IE的**怪异盒模型**

**padding-box：**将padding算入width范围。

- - 当设置为box-sizing:content-box时，将采用标准模式解析计算（默认模式）
  - 当设置为box-sizing:border-box时，将采用怪异模式解析计算



### **BFC(块级格式化上下文)**

**概念**：

BFC 即 Block Formatting Contexts (块级格式化上下文)，**具有 BFC 特性的元素可以看作是隔离了的独立容器，容器里面的元素不会在布局上影响到外面的元素，并且 BFC 具有普通容器所没有的一些特性。**通俗一点来讲，可以把 BFC 理解为一个封闭的大箱子，箱子内部的元素无论如何翻江倒海，都不会影响到外部。

**生成BFC的条件**：

1. float的值不为none
2. overflow的值不为visible
3. display的值为inline-block,table-ceil,table-caption,flex
4. position的值不为relative，static

**作用**：

1. 不和浮动元素重叠
2. 清除元素内部浮动
3. 防止margin重叠



### flex布局

#### 设置容器的属性有：

```css
display:flex;

flex-direction:row（默认值） | row-reverse | column |column-reverse

flex-wrap:nowrap（默认值） | wrap | wrap-reverse

justify-content:flex-start（默认值） | flex-end | center |space-between | space-around | space-evenly

align-items:stretch（默认值） | center  | flex-end | baseline | flex-start

align-content:stretch（默认值） | flex-start | center |flex-end | space-between | space-around | space-evenly
```

#### 设置项目的属性有：

```css
order:0（默认值） | <integer>

flex-shrink:1（默认值） | <number>

flex-grow:0（默认值） | <number>

flex-basis:auto（默认值） | <length>

flex:none | auto | @flex-grow @flex-shrink @flex-basis

align-self:auto（默认值） | flex-start | flex-end |center | baseline| stretch
```

#### flex-shink的计算方式

一个宽度为400px的容器，里面的三个项目width分别为120px，150px，180px。分别对这项目1和项目2设置flex-shrink值为2和3。

```css
.container{

  display: flex;

  width: 400px; // 容器宽度为400px

}
```

在这个例子中，项目溢出 400 - (120 + 150 + 180) = -50px。计算压缩量时总权重为各个项目的宽度乘以flex-shrink的总和，这个例子压缩总权重为120 * 2 + 150 * 3+ 180 * 1 = 870。各个项目压缩空间大小为总溢出空间乘以项目宽度乘以flex-shrink除以总权重：

item1的最终宽度为：120 - 50 * 120 * 2 / 870 ≈ 106px

item2的最终宽度为：150 - 50 * 150 * 3 / 870 ≈ 124px

item3的最终宽度为：180 - 50 * 180 * 1 / 870 ≈ 169px

其中计算时候值如果为小数，则向下取整。



### 实现盒子垂直居中的方式

#### 1. 固定宽高垂直居中

![](https://www.hualigs.cn/image/6098cc0dbad19.jpg)

```css
position: absolute;
left: 50%;
top: 50%;
width:200px;
height:100px;
margin-left:-100px;
margin-top:-50px;
```

#### 2. 不固定高宽div垂直居中的方法

```css
position: absolute;
top: 50%;
left: 50%;
background-color: #000;
width:50%;
height: 50%;
transform: translate(-50%,-50%);
```

#### 3. flex方法

```css
justify-content:center;//子元素水平居中
align-items:center;//子元素垂直居中
display:flex;
```



### 两边定宽，中间自适应的方法

#### 1. float+margin

```html
<style type="text/css">
    .main{
        overflow: hidden;
        background: #eee;
    }
    .left{
        background: red;
        width: 200px;
        height: 300px;
        float: left;
    }    
    .right{
        background: blue;
        width: 200px;
        height: 300px;
        float: right;
    }
    .middle{
        background: green;
        height: 300px;
        margin-left: 200px;
        margin-right: 200px;
    }
</style>
<div class="main">
    <div class="left"></div>
    <div class="right"></div>
    <div class="middle"></div>        
</div>
```

#### 2. Position+margin

```html
<style type="text/css">
    .main{
        position: relative;
    }
    .left{
        background: red;
        height: 300px;
        width: 200px;
        position: absolute;
        left: 0;
        top: 0;
    }    
    .right{
        background: blue;
        height: 300px;
        width: 200px;
        position: absolute;
        right: 0;
        top: 0;
    }
    .middle{
        background: green;
        height: 300px;
       	margin-left: 200px;
        margin-right: 200px;
    }
</style>
    <div class="main">
        <div class="left"></div>        
        <div class="right"></div>
        <div class="middle"></div>
    </div>
```

#### 3. flex布局

```html
<style type="text/css">
    .main{
        display: flex;
        align-items: center;
    }
    .left{
        background: red;
        width: 200px;
        height: 300px;
    }    
    .right{
        background: blue;
        width: 200px;
        height: 300px;
    }
    .middle{
        background: green;
        height: 300px;
        width: 100%;
    }
</style>
    <div class="main">
        <div class="left"></div>        
        <div class="middle"></div>
        <div class="right"></div>
    </div>
```

|   单值语法    |     等同于     |     备注     |
| :-----------: | :------------: | :----------: |
| flex: initial | flex: 0 1 auto | 初始值，常用 |
|    flex: 0    |  flex: 0 1 0%  |  适用场景少  |
|  flex: none   | flex: 0 0 auto |     推荐     |
|    flex: 1    |  flex: 1 1 0%  |     推荐     |
|  flex: auto   | flex: 1 1 auto |  适用场景少  |

### defer和async的区别

- defer：用于开启新的线程下载脚本文件，并使脚本在文档解析完成后执行。
- async：用于异步下载脚本文件，下载完毕立即解释执行代码。

关于defer我们需要注意下面几点：

1. defer只适用于外联脚本，如果script标签没有指定src属性，只是内联脚本，不要使用defer；
2. 如果有多个声明了defer的脚本，则会按顺序下载和执行 ；
3. defer脚本会在DOMContentLoaded和load事件之前执行。

关于async，也需要注意以下几点：

1. 只适用于外联脚本，这一点和defer一致；
2. 如果有多个声明了async的脚本，其下载和执行也是异步的，不能确保彼此的先后顺序；
3. async会在load事件之前执行，但并不能确保与DOMContentLoaded的执行先后顺序 。



## Nginx

### 1. 什么是Nginx？

Nginx ，是一个 Web 服务器和反向代理服务器，用于 HTTP、HTTPS、SMTP、POP3 和 IMAP 协议。目前使用的最多的 Web 服务器或者代理服务器，像淘宝、新浪、网易、迅雷等都在使用。
Nginx 的主要功能如下：

1. 作为 http server (代替 Apache ，对 PHP 需要 FastCGI 处理器支持)
2. FastCGI：Nginx 本身不支持 PHP 等语言，但是它可以通过 FastCGI 来将请求扔给某些语言或框架处理。
3. 反向代理服务器
4. 实现负载均衡
5. 虚拟主机

### 2.fastcgi 与 cgi 的区别？

1）cgi
web 服务器会根据请求的内容，然后会 fork 一个新进程来运行外部 c 程序（或 perl 脚本…）， 这个进程会把处理完的数据返回给 web 服务器，最后 web 服务器把内容发送给用户，刚才 fork 的进程也随之退出。
如果下次用户还请求改动态脚本，那么 web 服务器又再次 fork 一个新进程，周而复始的进行。

2）fastcgi
web 服务器收到一个请求时，他不会重新 fork 一个进程（因为这个进程在 web 服务器启动时就开启了，而且不会退出），web 服务器直接把内容传递给这个进程（进程间通信，但 fastcgi 使用了别的方式，tcp 方式通信），这个进程收到请求后进行处理，把结果返回给 web 服务器，最后自己接着等待下一个请求的到来，而不是退出。

### 3. Nginx 常用命令

```
启动  nginx 。
停止  nginx -s stop 或 nginx -s quit 。
重载配置  ./sbin/nginx -s reload(平滑重启) 或 service nginx reload 。
重载指定配置文件  .nginx -c /usr/local/nginx/conf/nginx.conf 。
查看nginx版本 nginx -v 。
检查配置文件是否正确  nginx -t 。
显示帮助信息  nginx -h 。
```

### 4. Nginx 有哪些优点？

- 跨平台、配置简单。

- 非阻塞、高并发连接

  处理 2-3 万并发连接数，官方监测能支持 5 万并发。

- 内存消耗小

  开启 10 个 Nginx 才占 150M 内存。

- 成本低廉，且开源。

- 稳定性高，宕机的概率非常小。

### 5. **请列举 Nginx 和 Apache 之间的不同点？**

- 轻量级，同样起 web 服务，Nginx 比 Apache 占用更少的内存及资源。
- 抗并发，Nginx 处理请求是异步非阻塞的，而 Apache 则是阻塞型的，在高并发下 Nginx 能保持低资源低消耗高性能。
- 最核心的区别在于 Apache 是同步多进程模型，一个连接对应一个进程；Nginx 是异步的，多个连接（万级别）可以对应一个进程。
- Nginx 高度模块化的设计，编写模块相对简单。

### 6. **Nginx 是如何实现高并发的？**

如果一个 server 采用一个进程(或者线程)负责一个request的方式，那么进程数就是并发数。那么显而易见的，就是会有很多进程在等待中。等待最多的应该是网络传输。

而 Nginx 的异步非阻塞工作方式正是利用了这点等待的时间。在需要等待的时候，这些进程就空闲出来待命了。因此表现为少数几个进程就解决了大量的并发问题。

Nginx是如何利用的呢，简单来说：同样的 4 个进程，如果采用一个进程负责一个 request 的方式，那么，同时进来 4 个 request 之后，每个进程就负责其中一个，直至会话关闭。期间，如果有第 5 个request进来了。就无法及时反应了，因为 4 个进程都没干完活呢，因此，一般有个调度进程，每当新进来了一个 request ，就新开个进程来处理。

Nginx 不这样，每进来一个 request ，会有一个 worker 进程去处理。但不是全程的处理，处理到什么程度呢？处理到可能发生阻塞的地方，比如向上游（后端）服务器转发 request ，并等待请求返回。那么，这个处理的 worker 不会这么傻等着，他会在发送完请求后，注册一个事件：“如果 upstream 返回了，告诉我一声，我再接着干”。于是他就休息去了。此时，如果再有 request 进来，他就可以很快再按这种方式处理。而一旦上游服务器返回了，就会触发这个事件，worker 才会来接手，这个 request 才会接着往下走。

这就是为什么说，Nginx 基于事件模型。

由于 web server 的工作性质决定了每个 request 的大部份生命都是在网络传输中，实际上花费在 server 机器上的时间片不多。这是几个进程就解决高并发的秘密所在。即：

**webserver 刚好属于网络 IO 密集型应用，不算是计算密集型。**

而正如叔度所说的

**异步，非阻塞，使用 epoll ，和大量细节处的优化。**

也正是 Nginx 之所以然的技术基石。

### 7. **为什么 Nginx 不使用多线程？**

Apache: 创建多个进程或线程，而每个进程或线程都会为其分配 cpu 和内存（线程要比进程小的多，所以 worker 支持比 perfork 高的并发），并发过大会榨干服务器资源。

Nginx: 采用单线程来异步非阻塞处理请求（管理员可以配置 Nginx 主进程的工作进程的数量）(epoll)，不会为每个请求分配 cpu 和内存资源，节省了大量资源，同时也减少了大量的 CPU 的上下文切换。所以才使得 Nginx 支持更高的并发。

### 8. **Nginx 有哪些负载均衡策略？**
负载均衡，即代理服务器将接收的请求均衡的分发到各服务器中。

Nginx 默认提供了 3 种负载均衡策略：

**1、轮询（默认）round_robin**

每个请求按时间顺序逐一分配到不同的后端服务器，如果后端服务器 down 掉，能自动剔除。

**2、IP 哈希 ip_hash**

每个请求按访问 ip 的 hash 结果分配，这样每个访客固定访问一个后端服务器，可以解决 session 共享的问题。

当然，实际场景下，一般不考虑使用 ip_hash 解决 session 共享。

**3、最少连接 least_conn**

下一个请求将被分派到活动连接数量最少的服务器
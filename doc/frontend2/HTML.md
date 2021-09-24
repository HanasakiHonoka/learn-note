### 块级元素、行内元素
块级元素：独占一行；元素的宽高、以及内外边距都可设置；元素宽度在不设置的情况下，是它本身父容器的100%。
`div h1-h5 table ol ul form`

                   
行内元素：不会自动进行换行；元素的宽高不可设置；内边距可以设置、外边距水平方向有效，竖直方向无效；元素宽度在不设置的情况下，随内部元素的内容变化。
`span a i strong big img input slect textarea`

互相转换:
1. display，将元素设置为块级、行内或是其它。　　
2. float，隐形地把内联元素转换为行内块级元素。不会占据一行，相当于display：inline-block;　　
3. position,属性值为absolute、fixed 时,隐形地把内联元素转换为块级元素，其它属性值不会做转换。

`<img>、<input>、<textarea>、<select>、<object>`是行内元素，但是它们却可以设置宽高，因为它们是行内置换元素(内容不受CSS视觉格式化模型控制，CSS渲染模型并不考虑对此内容的渲染，且元素本身一般拥有固有尺寸（宽度，高度，宽高比）的元素，被称之为置换元素)


#### meta作用
`<meta> `标签提供了 HTML 文档的元数据。元数据不会显示在客户端，但是会被浏览器解析。

META元素通常用于指定网页的描述，关键词，文件的最后修改时间，作者及其他元数据。

元数据可以被使用浏览器（如何显示内容或重新加载页面），搜索引擎（关键词），或其他 Web 服务调用。

#### 同源策略是什么, 如何解决跨域
同源： 端口,协议,域名都一致

策略：
1. 无法读取非同源的 cookie、Storage、indexDB的内容
2. 无法读取非同源的DOM
3. 无法发送非同源的AJAX，更加准确的说应该是发送了请求但被浏览器拦截了。

目的:
1.为了防止恶意网页可以获取其他网站的本地数据。

2.为了防止恶意网站iframe其他网站的时候，获取数据。

3.为了防止恶意网站在自已网站有访问其他网站的权利，以免通过cookie免登，拿到数据。

**威胁：**

**xss**
Cross Site Script，译为是跨站脚本攻击；其原本缩写是 CSS，但为了和层叠样式表(Cascading Style Sheet)有所区分，因而被迫改名为 XSS。主要分为反射型和储存型

xss存在的主要原因在于没有对于用户的提交内容和接口的返回内容没有进行严格的过滤。
而防止xss的主要手段也是对输入和url参数进行过滤，对输出进行编码，还有就是cookie设置http-only

**csrf**
Cross-site request forgery,跨站请求伪造，简单来说就是盗用了你的身份(cookie)，以你的名义发送恶意请求，
从另一篇讲的很不错的文章里偷的图来说明csrf的攻击过程


跨域解决：
1. CORS
​	CORS（跨域资源共享）使用专用的HTTP头，服务器（api.baidu.com）告诉浏览器，特定URL（baidu.com）的ajax请求可以直接使用，不会激活同源策略。让web服务器明确授权非同源页面脚本来访问自身，以Response特定标头`Access-Control-Allow-Origin`
2. JSONP
​	这个方案相当于黑魔法，因为js调用（实际上是所有拥有src属性的 <\script>、<\img>、<\iframe>）是不会经过同源策略，例如baidu.com引用了CDN的jquery。所以我通过调用js脚本的方式，从服务器上获取JSON数据绕过同源策略。

    缺点：
    1.安全问题，src引用是开放的，所以jsonp的资源都被所有人访问到。解决方法是用jsonp中的token参数，通过A域和B域共用同一套cookie来验证A的身份。
    2.只能用GET方式不能用POST方式获取数据即只能读不能写。
    3.可被注入恶意代码如?callback=alert(1); 这问题只能用正则过滤字符串的方法解决，过滤callback后的内容不能有括号之类的条件。



3. 正向代理 
   当你访问```baidu.com/api/login```的时候，通过在```baidu.com```的nginx服务器会识别你是api下的资源，会自动代理到```api.baidu.com/login```，浏览器本身是不知道我实际上是访问的```api.baidu.com```的数据，和前端资源同源，所以也就不会触发浏览器的同源策略。

4. iframe实现
   
    https://mp.weixin.qq.com/s?__biz=MzUxNjQ1NjMwNw==&mid=2247484470&idx=1&sn=c00f03c3bb7506cb87c68ab22e1cc338&chksm=f9a66e2aced1e73c04ab7e5d9e63ca8b969d22611e00f232411487aabc99e37dd4c4d46e6440&token=1976933310&lang=zh_CN#rd

    现在我启动了两个服务

1、localhost:3001 下有  a.html 和 c.html

a.html 是内容页，需要使用数据的终端页（以下简称A）

c.html 是辅助页（以下简称C）

2、localhost:3002 下有 b.html

b.html 也是辅助页，用于请求数据（以下简称B）



内容页 A

在 A 中，使用 iframe 嵌入了 B，并且全局设置了一个函数 getData

这个函数的作用是，为了给 C页面调用，传入接口的数据的

```html
<body>
    我是A页面
    <script>
        window.getData=function(data){            

            console.log("获取到数据",data)

        }    

    </script>

    <iframe src="http://localhost:3002/b.html" ></iframe>

</body>
```

辅助页 B 

B 页面当然是用于请求接口了，这里使用 定时器模拟接口，请求成功后跳转到 C

```html
<body>

    我是B页面
    <script>
        console.log("B页面开始请求接口")

        setTimeout(function(){            

            window.name="我是B页面保存的数据"

            location.href="http://localhost:3001/c.html"
        },2000)    

    </script>

</body>
```

辅助页 C 

B 请求完，跳到C 之后，C 拿到 window.name，然后调用 A 的方法 getData，并且把数据传过去

```html
<body>
    我是C页面
    <script>// 调用页面A 的方法，并把 name 传过去
        parent.getData(window.name)    

    </script>

</body>
```
   

### 浏览器缓存机制

浏览器每次发起请求都会在浏览器缓存中查找结果以及缓存标识;

浏览器在获得服务端请求结果后，会存到浏览器缓存中

**强制缓存**就是向浏览器缓存查找该请求结果，并根据该结果的缓存规则来决定是否使用该缓存结果的过程：
- 若在缓存中找不到结果以及缓存标识，强制缓存失效,直接向服务器发起请求；
- 存在结果和标识，但结果失效，触发协商缓存；
- 存在结果和标识，结果未失效，触发强制缓存，直接返回结果
通过header字段Expires和Cach-Control管理
后者优先级更高。
Expires: Wed, 21 Oct 2000 07:28:00 GMT
Cache-Control: max-age=20000

**协商缓存**
Etag：  一个hash值或者版本号
if-none-match： 若true，返回304 ； 若false 返回200

Last-Modified/ if-modified-since

Etag优先级更高 ， 后者1s级别的


### `<script>`使用时机
放在`<head>`内，只有当脚本全部下载解析并执行后才去解析`<body>`导致页面白屏。


### POST Get区别
1. 都能发送数据和接收数据，post通过报文，get通过地址栏
2. get会由于url的长度而受限
3. get发一个tcp包（包含header 和data）， post发俩。第一个（header）会返回100，然后发第二个data
4. get安全性低于post，URL是可见的，可能会泄露私密信息； 数据查询用get；
5. get只能支持ASCII字符，向服务器传的中文字符可能会乱码，post支持标准字符集，可以正确传递中文字符

### 八种请求类型
|||
|  ----  | ----  | 
|get ||
post ||
delete  |删除
put     |更新实体
trace   |提供一种方法来测试当一个请求发生的时候，服务器通过网络收到的内容。所以它会返回你发送的内容
head    |HEAD请求和GET请求资源类似，但仅仅返回相应的头部，没有具体的响应体
options |OPTIONS允许客户端请求一个服务所支持的请求方法。它所对应的响应头是Allow，它非常简洁地列出了支持的方法。
connect |主要用来建立一个对资源的网络连接。一旦建立连接后，会响应一个200状态码和一条"Connectioin Established"的消息。

**简单请求**
只要同时满足以下两大条件，就属于简单请求。

（1) 请求方法是以下三种方法之一：

HEAD
GET
POST

（2）HTTP的头信息不超出以下几种字段：
Accept
Accept-Language
Content-Language
Last-Event-ID
Content-Type：只限于三个值application/x-www-form-urlencoded、multipart/form-data、text/plain
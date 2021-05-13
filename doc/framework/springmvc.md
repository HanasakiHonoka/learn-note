# SpringMVC
# SpringMVC的流程？

* （1）用户发送请求至前端控制器DispatcherServlet；
* （2）DispatcherServlet收到请求后，调用HandlerMapping处理器映射器，请求获取Handler；
* （3）处理器映射器根据请求url找到具体的处理器Handler，生成处理器对象及处理器拦截器(如果有则生成)，一并返回给DispatcherServlet；
* （4）DispatcherServlet 调用 HandlerAdapter处理器适配器，请求执行Handler；
* （5）HandlerAdapter 经过适配调用 具体处理器进行处理业务逻辑；
* （6）Handler执行完成返回ModelAndView；
* （7）HandlerAdapter将Handler执行结果ModelAndView返回给DispatcherServlet；
* （8）DispatcherServlet将ModelAndView传给ViewResolver视图解析器进行解析；
* （9）ViewResolver解析后返回具体View；
* （10）DispatcherServlet对View进行渲染视图（即将模型数据填充至视图中）
* （11）DispatcherServlet响应用户。

![](https://img-blog.csdn.net/20180708224853769?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2E3NDUyMzM3MDA=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)

* 前端控制器 DispatcherServlet：接收请求、响应结果，相当于转发器，有了DispatcherServlet 就减少了其它组件之间的耦合度。
* 处理器映射器 HandlerMapping：根据请求的URL来查找Handler
* 处理器适配器 HandlerAdapter：负责执行Handler
* 处理器 Handler：处理器，需要程序员开发
* 视图解析器 ViewResolver：进行视图的解析，根据视图逻辑名将ModelAndView解析成真正的视图（view）
* 视图View：View是一个接口， 它的实现类支持不同的视图类型，如jsp，freemarker，pdf等等

# SpringMVC的优点:

（1）可以支持各种视图技术，而不仅仅局限于JSP；

（2）与Spring框架集成（如IoC容器、AOP等）；

（3）清晰的角色分配：前端控制器(dispatcherServlet) ，请求到处理器映射（handlerMapping)，处理器适配器（HandlerAdapter)，视图解析器（ViewResolver）。

（4） 支持各种请求资源的映射策略。

# SpringMVC怎么样设定重定向和转发的？

（1）转发：在返回值前面加"forward:"，譬如"forward:user.do?name=method4"

（2）重定向：在返回值前面加"redirect:"，譬如"redirect:http://www.baidu.com"
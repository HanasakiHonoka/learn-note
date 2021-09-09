### 闭包
作用：闭包是指那些能够访问自由变量的函数。
- 即使创建它的上下文已经销毁，它仍然存在（比如，内部函数从父函数中返回）
- 在代码中引用了自由变量

闭包是一种保护私有变量的机制，在函数执行时形成私有的作用域，保护里面的私有变量不受外界干扰。
直观的说就是形成一个不销毁的栈环境。

**原理**
闭包的实现原理，其实是利用了作用域链的特性，我们都知道作用域链就是在当前执行环境下访问某个变量时，如果不存在就一直向外层寻找，最终寻找到最外层也就是全局作用域，这样就形成了一个链条。

- 作用1：隐藏变量，避免全局污染
- 作用2：可以读取函数内部的变量
  
同时闭包使用不当，优点就变成了缺点：
- 缺点1：导致变量不会被垃圾回收机制回收，造成内存消耗
- 缺点2：不恰当的使用闭包可能会造成内存泄漏的问题

```javascript
function addN(){
    let n =1;
    function inneradd(){
        n++;
        console.log(n);
        console.log(this);
    }
    return inneradd;
}
x = addN();
x();  // 2
x();  // 3
```

### JS垃圾回收


### JS引擎单线程机制
当线程中没有执行任何同步代码的前提下才会执行异步代码，setTimeout是异步代码，所以setTimeout只能等js空闲才会执行


**setTimeout 和 setInterval 在执行异步代码的时候有着根本的不同**

如果一个计时器被阻塞而不能立即执行，它将延迟执行直到下一次可能执行的时间点才被执行（比期望的时间间隔要长些）
如果setInterval回调函数的执行时间将足够长（比指定的时间间隔长），它们将连续执行并且彼此之间没有时间间隔。







#### undefined 和null区别
在if判断中，两者都被自动转换为false。

null 表示“没有对象”，即该处不应该有值

（1） 作为函数的参数，表示该函数的参数不是对象。

（2） 作为对象原型链的终点

undefined表示"缺少值"，就是此处应该有一个值，但是还没有定义

（1）变量被声明了，但没有赋值时，就等于undefined。

（2) 调用函数时，应该提供的参数没有提供，该参数等于undefined。

（3）对象没有赋值的属性，该属性的值为undefined。

（4）函数没有返回值时，默认返回undefined。



### 执行上下文





### 获取scrolltop
```var scrollTop = document.documentElement.scrollTop || window.pageYOffset || document.body.scrollTop;```

- IE6/7/8： 
对于没有doctype声明的页面里可以使用  ```document.body.scrollTop ```来获取 scrollTop高度 ； 
对于有doctype声明的页面则可以使用 ```document.documentElement.scrollTop； ```
- Safari: 
safari 比较特别，有自己获取scrollTop的函数 ： ```window.pageYOffset ； ```
- Firefox: 
火狐等等相对标准些的浏览器就省心多了，直接用 ```document.documentElement.scrollTop ；```


#### this指向
js执行可执行代码时，会创建对应执行上下文。
包含三部分：{
    变量对象(Variable object，VO)
    作用域链(Scope chain)
    this
}

谁直接调用产生 this 指针的函数，这函数里的 this 指针就指向谁。

**函数调用this指向**
指向全局window

**方法调用this指向**
对象中的函数称为方法，this指向这个对象；
若方法为箭头函数，则this指向创建对象的作用域，window

```javascript
var bj=10;
var obj={
    name:"八戒",
    age:"500",
    say:function(){
        var bj=40;
        console.log(this);//window
        console.log(this.bj);//10
        console.log(this.name);//这里没有输出内容
    }
}
var elseObj=obj.say;
elseObj();
```
elseObj只是一个函数，等同于window进行函数调用，所以this指向window
因为Window自己有name的属性且为空，所以this.name输出空，若为其他则输出undefined


更多内容参考
https://www.cnblogs.com/zjjDaily/p/9482958.html

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


#### 箭头函数this指向
- 箭头函数this指向在定义时就已经确定，箭头函数不会创建this，它只会从自己作用域链的上一层继承this (出自MDN的解释)
- 箭头函数的this由它的外层（函数或全局）作用域来决定 (出自你不知道的JS P-99)
- 箭头函数体内的this就是定义时所在的对象，而非运行时所在的对象 （出自阮一峰 ES6标准入门 P-81）
- 所有的箭头函数都没有自己的this，指向外层
  
定义在一个**函数**中，此时箭头函数中的this取决于外层函数中的this指向。
```javascript
function foo() {
  setTimeout(() => {
    console.log(this);
  }, 1000);
}
foo(); //
var obj = {};
foo.call(obj); // 
```
分别为：
window

obj  ，setTimeout运行时的作用域在obj中


定义在**对象**中.
由于对象没有作用域，所以沿着作用域链继续向上查找，会找到全局
[JS中存在基于函数的作用域，意味中每声明一个函数，都会为其自身创建一个气泡，而其他结构不会创建作用域气泡]
```javascript
var name = 'window';
const person1 = {
  name: 'person1',
  sayName: () => {
    console.log(this.name);
  }
}

person1.sayName() //
```
结果： window
```javascript
var name = 'window';
const person1 = {
  name: 'person1',
  obj: {
      init: () => {
        console.log(this.name);
      },
      end: function(){
         console.log(this);
        }
  }
}
person1.obj.init(); //
person1.obj.end(); //
```
结果：

window

obj

**使用构造函数**，分别是全局作用域>>Person构造函数作用域>>箭头函数作用域。创建personA的时候，将构造函数的this指向了personA，由于该构造函数是箭头函数的上一层作用域，所以此时箭头函数中this指向personA。personB同理。
```javascript
var name = "global";
function Person(name) {
    this.name = name;
    this.sayName = () => {
        console.log(this.name)
    }
}
const personA = new Person('aaa');
const personB = new Person('bbb');
const personC ={};
personA.sayName(); // 
personB.sayName(); //
personA.sayName.call(personC); //
```
结果：

aaa

bbb

aaa

更多参考： https://juejin.cn/post/6844903667955400717


### 0.1+0.2≠0.3 原因及解决方法

通过JS数字存储遵循IEEE754，采用双精度64位存储。
二进制科学计数法小数部分52位，另一以为表示正负号，共53位，超出部分取1舍0

流程:
1. 进制转换
2. 对阶运算
3. 转换10进制

解决：
1. *10取整后再求小数
2. 当和的差值小于一个极小数Number.EPSILON就判断true

#### 保留小数(n位)
```num = num.toFixed(n);```

#### BigInt 提出
BigInt 是一个内置的对象，它提供了了一种方式来表示所有大于 2^53 的数字，也就是 JavaScript 原始类型 Number 能够能够可靠表示的最大数字。
- 长整数表示
- 精确计算（Number.MAX_SAFE_INTEGER）

**Number.MAX_SAFE_INTEGER  和 Number.MAX_VALUE 区别**

Number.MAX_SAFE_INTEGER 是可以在计算中安全使用的最大整数。

例如，Number.MAX_SAFE_INTEGER + 1 === Number.MAX_SAFE_INTEGER + 2是真的 - 任何大于MAX_SAFE_INTEGER的整数都不能准确地在内存中表示。所有位用于表示数字的数字。

Number.MAX_VALUE另一方面是使用双精度浮点表示可能表示的最大数目。一般来说，数字越大，精确度越低。

### Symbol

值类型: Number,String, null, undifined, Boolean

变量之间的**互相赋值**，是指开辟一块新的内存空间，将变量值赋给新变量保存到新开辟的内存里面；之后两个变量的值变动互不影响。

引用类型理解：变量之间的**互相赋值**，只是指针的交换，而并非将对象（普通对象，函数对象，数组对象）复制一份给新的变量，对象依然还是只有一个，只是多了一个指引


#### == 和 === 的区别
- ==操作符会先将两边的值进行强制类型转换再比较是否相等，而===操作符不会进行类型转换。
- ==操作符只要求比较两个值是否相等，而===操作符不仅要求值相等，而且要求类型相同
  
特殊情况：NaN ==/=== NaN 都返回false 所以需要用 isNaN()做判断

值类型将始终与具有相同值的另一个值类型的完全相等。
完全相同结构的引用类型是不相等的

在 symbol 出现之前，对象键只能是字符串，如果试图使用非字符串值作为对象的键，那么该值将被强制转换为字符串



Symbol() 函数会返回 symbol 类型的值，该类型具有静态属性和静态方法。它的静态属性会暴露几个内建的成员对象；它的静态方法会暴露全局的 symbol 注册，且类似于内建对象类，但作为构造函数来说它并不完整，因为它不支持语法："new Symbol()"。所以使用 Symbol 生成的值是不相等：

```javascript
const s1 = Symbol();
const s2 = Symbol();
console.log(s1 === s2); // false
```

就是避免当不同的库向对象添加属性存在命名冲突的风险.
即使名称一样，依然会重新添加属性。

`JSON.stringify()` 不会输出symbol值

### 前端模块化

一个文件就是一个模块，有自己的作用域，只向外暴露特定的变量和函数。目前流行的js模块化规范有CommonJS、AMD、CMD以及ES6的模块系统。
#### CommonJS
Node.js是commonJS规范的主要实践者，它有四个重要的环境变量为模块化的实现提供支持：`module、exports、require、global`。实际使用时，用`module.exports`定义当前模块对外输出的接口（不推荐直接用`exports`），用`require`加载模块.
#### AMD和require.js
。。。


### instanceof



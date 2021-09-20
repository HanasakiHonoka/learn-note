

### 基本数据类型和引用数据类型

基本类型：Number、String 、Boolean、Null、Undefined和Symbol

引用类型：对象类型Object type，比如：Object 、Array 、Function 、Data等。

区别：

原始值：存储在栈（stack）中的简单数据段，也就是说，它们的值直接存储在**变量访问的位置**。

这是因为这些原始类型占据的空间是固定的，所以可将他们存储在较小的内存区域 – 栈中。这样存储便于迅速查寻变量的值。

引用值：存储在堆（heap）中的对象，也就是说，**存储在变量处的值是一个指针（point），指向存储对象的内存地址。**

这是因为：引用值的大小会改变，所以不能把它放在栈中，否则会降低变量查寻的速度。相反，放在变量的栈空间中的值是该对象存储在堆中的地址。地址的大小是固定的，所以把它存储在栈中对变量性能无任何负面影响。

### **怎么判断一个对象是不是数组类型？**

1. **从原型入手，Array.prototype.isPrototypeOf(obj)**

利用isPrototypeOf()方法，判定Array是不是在obj的原型链中，如果是，则返回true,否则false。

2. **从构造函数入手，obj instanceof Array**

在一些跨框架的页面中的数组，使用该方法可能不会那么顺利，原因是在不同的框架中创建的数组不会相互共享其prototype属性。

3. **根据对象的class属性(类属性)，跨原型链调用toString()方法。**

js中提供了调用对象原型中的toString方法， Object.prototype.toString.call(obj)；因为很多对象继承的toString（）方法被重写了，为了能够调用正确的toString（）版本，也就是最原始的版本。可以使用Function.call()的方法，其中call可以这么理解，相当于obj去借用这个 Object.prototype.toString()。
```Object.prototype.toString.call(o)=='[object Array]'```

4. **Array.isArray()方法**

### **ES6新特性**

1. let const
2. 模版字符串
3. 箭头函数
4. 对象（字典）

- 键值对重名简写
- 对象字面量方法赋值简写
- 提供浅复制方法 assign
- 数据解构 数据展开

 数组：

- find();查找数组某个元素
- findIndex();查找某个元素的索引值
- some();数组中是否有元素符合条件
- every();数组中是否所有的元素都符合条件
- foreach（）
- map（）
- filter（）
- reduce（）

对象：

- Object.assign(); 复制一个对象
- Object.keys(); 得到一个对象的所有属性
- Object.values(); 得到一个对象的所有可枚举属性值
- Object.entries(); 得到一个对象的键值对数组
- Object.fromEntries(); entries的逆操作

### **遍历属性方法**

for-in 可以通过对象访问且可以被枚举的属性都会返回，包括实例属性和原型属性。

Object.keys() 返回包含该对象所有可枚举属性名称的字符串数组。

Object.getOwnPropertyNames() 返回包含该对象的自有属性，包括可枚举和不可枚举的字符串数组

### 柯里化实现

```javascript
const curry = (fn, ...args) => 
    // 函数的参数个数可以直接通过函数数的.length属性来访问
    args.length >= fn.length // 这个判断很关键！！！
    // 传入的参数大于等于原始函数fn的参数个数，则直接执行该函数
    ? fn(...args)
    /**
     * 传入的参数小于原始函数fn的参数个数时
     * 则继续对当前函数进行柯里化，返回一个接受所有参数（当前参数和剩余参数） 的函数
    */
    : (..._args) => curry(fn, ...args, ..._args);

function add1(x, y, z) {
    return x + y + z;
}
const add = curry(add1);
```

### 手写Promise.all()

1. Promise.myAll()返回的肯定是一个promise对象，所以可以直接写一个return new Promise((resolve, reject) => {})(这应该是一个惯性思维)

2. 遍历传入的参数，用Promise.resolve()将参数"包一层"，使其变成一个promise对象
3. 关键点是何时"决议"，也就是何时resolve出来，在这里做了计数器（count），每个内部promise对象决议后就将计数器加一，并判断加一后的大小是否与传入对象的数量相等，如果相等则调用resolve()，如果任何一个promise对象失败，则调用reject()方法。
    **一些细节：**

4. 官方规定Promise.all()接受的参数是一个可遍历的参数，所以未必一定是一个数组，所以用Array.from()转化一下

5. 使用for…of进行遍历，因为凡是可遍历的变量应该都是部署了iterator方法，所以用for…of遍历最安全

```javascript
Promise.all = function (iterator) {  
    let count = 0//用于计数，当等于len时就resolve
    let len = iterator.length
    let res = []//用于存放结果
    return new Promise((resolve,reject) => {
        for(let i in iterator){
            Promise.resolve(iterator[i])//先转化为Promise对象
            .then((data) => {
                res[i] = data;
                if(++count === len){
                    resolve(res)
                }
            })
            .catch(e => {
                reject(e)
            })
        }
    })
}
//加最大并发量版本
Promise.all = function (iterator, maxNum) {  
    let len = iterator.length
    let res = []//用于存放结果
    let max = Math.min(len, maxNum); // 最大并发量
    return new Promise((resolve,reject) => {
        function run(p) {
            Promise.resolve(p)
            .then(data => {
                res.push(data);
                if(iterator.length > 0) {
                    run(iterator.shift());
                } else if(res.length === len) {
                    resolve(res);
                }
            })
            .catch(e => {
                reject(e);
            })
        }
        for(let i = 0; i < max; i++){
            run(iterator.shift());
        }
    })
}
```

### 手写Promise.race()

谁先决议那么就返回谁，所以将all的计数器和逻辑判断全部去除掉就可以了。

```javascript
Promise.race = function (iterators) {  
    return new Promise((resolve,reject) => {
        for (const p of iterators) {
            Promise.resolve(p)
            .then((res) => {
                resolve(res)
            })
            .catch(e => {
                reject(e)
            })
        }
    })

}
```

### 手写防抖

```javascript
function debounce(fn,time) {
    let timer = null;
    return function() {
    if(timer) clearTimeout(timer);
    timer = setTimeout(fn,time);
    }
}
```

### 手写节流

```js
//时间戳
function throttle1(fn,time) {
    let prev = Date.now();
    return function(){
        if(Date.now()-prev>=time){
            fn.call(this);
            prev = Date.now();
        }
    }
}
//计时器
function throttle2(fn,time) {
    let timer = null;
    return function() {
        if(timer==null) {
            timer = setTimeout(function(){
                fn.call(this);
                timer=null;
             },time);
        }
    }
}
```

### 原型链图

[![gts1PI.png](https://z3.ax1x.com/2021/05/10/gts1PI.png)](https://imgtu.com/i/gts1PI)

### 手写ajax

```javascript
function ajax(url) {
  return new Promise((resolve,reject)=>{
    xhr = new XMLHttpRequest();
  	xhr.open("get",url,true);
  	xhr.onload = function() {
    	if(xhr.status==200) {
      	resolve();
    	}else reject();
  	} 
  	xhr.onreadystatechange = function() {
    	if(xhr.status==200&&xhr.readyState==4) {
      	resolve();
    	}
  	}
  	xhr.send();});
}
```

### 手写new

```javascript
function myNew(){
    const obj = new Object();
    const Constructor = Array.prototype.shift.call(arguments);
    obj.__proto__ = Constructor.prototype;
    let ret = Constructor.apply(obj,arguments); // 判断构造函数是否存在返回值
    return typeof ret === 'object'? ret : obj;
}
```

### 手写instanceof

```javascript
function myInstanceof(target, origin) {
    // 非object直接返回false
    if(typeof target !== 'object' || target === null) return false;
    
    var proto = Object.getPrototypeOf(target);
  	//或 var proto = target.__proto__;
    while (proto) {
      if (proto === origin.prototype) {
        return true;
      }
      proto = Object.getPrototypeOf(proto);
      //或 proto = target.__proto__;
    }
    return false;
}

```

### 手写bind

```javascript
Function.prototype.bind = function(oThis) {
let args = Array.prototype.slice.call(arguments,1);
let fTobind = this;
var fNOP = function() {};
var fBond = function() {
return fTobind.apply(this instanceof fNOP? this: oThis,
	args.concat(Array.prototype.slice.call(arguments)));
}
fNOP.prototype = this.prototype;
fBond.prototype = new fNOP();
return fBond;
}
```



### 浅拷贝和深拷贝

浅拷贝：复制一层对象的属性，并不包括对象里面的为引用类型的数据，当改变拷贝的对象里面的引用类型时，源对象也会改变。

深拷贝：重新开辟一个内存空间，需要递归拷贝对象里的引用，直到子属性都为基本类型。两个对象对应两个不同的地址，修改一个对象的属性，不会改变另一个对象的属性。

浅拷贝：

copy = Object.Assign({}, obj);

深拷贝：

- JSON.parse(JSON.stringify(obj))
- lodash
- 递归调用（手写）

```javascript
function deepCopy(data,hash) {
if(typeof data !== "object"|| data === null) {
throw new TypeError("传入参数不是对象。")
}
// 判断传入的待拷贝对象的引用是否存在于hash中
if(hash.has(data)) {
return hash.get(data)
}
let newData = {};
const dataKeys = Object.keys(data);
dataKeys.forEach(value => {
const currentDataValue = data[value];
// 基本数据类型的值和函数直接赋值拷贝
if(typeof currentDataValue !== "object" || currentDataValue === null || currentDataValue instanceof RegExp||currentDataValue instanceof Date) {
newData[value] = currentDataValue;
}else if(Array.isArray(currentDataValue)) {
//实现数组深拷贝
newData[value] = [...currentDataValue];
}else if(currentDataValue instanceof Set) {
//实现set数据的深拷贝
newData[value] = new Set([...currentDataValue]);
}else if(currentDataValue instanceof Map) {
//实现Map数据的深拷贝
newData[value] = new Map([...currentDataValue]);
}else {
// 将这个待拷贝对象的引用存于hash中
hash.set(data,data);
//普通对象递归赋值
newData[value] = deepCopy(currentDataValue,hash);
}
});
return newData;
}
```

### 手写大数相加

```js
function bigNumAdd(a, b) {
  //1.比较两个数长度  然后短的一方前面补0
  if (a.length > b.length) {
    let arr = Array(a.length - b.length).fill(0);
    b = arr.join('') + b
  } else {
    let arr = Array(b.length - a.length).fill(0);
    a = arr.join('') + a
  }
  //2.反转两个数 （这里是因为人习惯从左往右加 而数字相加是从右到左 因此反转一下比较好理解）
  a = a.split('').reverse();
  b = b.split('').reverse();
  //3.循环两个数组  并进行相加  如果和大于10 则 sign = 1,当前位置的值为(和%10)
  let sign = 0;//标记 是否进位
  let newVal = [];//用于存储最后的结果
  for (let j = 0; j < a.length; j++) {
    let val = a[j] / 1 + b[j] / 1 + sign;//除1是保证都为数字 这里也可以用Number()
    if (val >= 10) {
      sign = 1;
      newVal.unshift(val % 10)//这里用unshift而不是push是因为可以省了使用reverse
    } else {
      sign = 0;
      newVal.unshift(val)
    }
  }

  return newVal.join('');
}
```



### 继承的六种方式

1. 原型链继承  **核心：** 将父类的实例作为子类的原型 **缺点**： 引用类型的属性被所有实例共享
2. 构造继承  **核心：**使用父类的构造函数来增强子类实例，等于是复制父类的实例属性给子类（没用到原型） **优点**：1.避免了引用类型的属性被所有实例共享 2.可以在 Child 中向 Parent 传参 **缺点**： 方法都在构造函数中定义，每次创建实例都会创建一遍方法
5. 组合继承 **核心：**通过调用父类构造，继承父类的属性并保留传参的优点，然后通过将父类实例作为子类原型，实现函数复用 **优点：** 融合原型链继承和构造函数的优点，是 JavaScript 中最常用的继承模式。**缺点：**执行了两次父类构造函数。 
4. 原型式继承：**核心：**声明一个过渡函数对象，返回过渡对象的一个实例，该实例的原型继承了父对象。**优点：** 减小实例化父类对象带来的开销。**缺点**： 引用类型的属性被所有实例共享。
5. 寄生式继承：**核心：**对原型式继承的二次封装，添加了自己的属性和方法。
6. 寄生组合继承：**核心：**通过寄生方式，砍掉父类的实例属性，这样，在调用两次父类的构造的时候，就不会初始化两次实例方法/属性，避免的组合继承的缺点 **优点**： 这种方式的高效率体现它只调用了一次 Parent 构造函数，并且因此避免了在 Parent.prototype 上面创建不必要的、多余的属性。与此同时，原型链还能保持不变；因此，还能够正常使用 instanceof 和 isPrototypeOf。开发人员普遍认为寄生组合式继承是引用类型最理想的继承范式。

### 箭头函数的特点

- **箭头函数表达式**的语法比[函数表达式](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Operators/function)更简洁，并且没有自己的[this](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Operators/this)，[arguments](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Functions/arguments)，[super](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Operators/super)或[new.target](https://developer.mozilla.org/zh-CN/docs/Web/JavaScript/Reference/Operators/new.target)。箭头函数表达式更适用于那些本来需要匿名函数的地方，并且它不能用作构造函数。
- 箭头函数不会创建自己的this,它只会从自己的作用域链的上一层继承this。
- 由于 箭头函数没有自己的this指针，通过 call() *或* apply() 方法调用一个函数时，只能传递参数（不能绑定this），他们的第一个参数会被忽略。（这种现象对于bind方法同样成立）
- 箭头函数不能用作构造器，和 new一起用会抛出错误。
- 箭头函数没有prototype属性。
-  [yield](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Operators/yield) 关键字通常不能在箭头函数中使用（除非是嵌套在允许使用的函数内）。因此，箭头函数不能用作函数生成器。

### 手写双向绑定原理

```html
姓名：<span id="spanName"></span>
<br>
<input type="text" id="inpName">
```

#### 1. Object.defineProperty

```javascript
let obj = {
  name: ""
};
let spanName = document.getElementById("spanName");
let inpName = document.getElementById("inpName");
let newObj = JSON.parse(JSON.stringify(obj));
Object.defineProperty(obj,'name',{
  get() {
    return newObj.name;
  },
  set(val) {
    if(val===newObj.name) return;
    newObj.name = val;
    observer();
  }
})
function observer() {
  spanName.innerHTML = obj.name;
  inpName.value = obj.name;
}
inpName.addEventListener("input",function() {
  obj.name = this.value;
})
```

缺点：

1.对原始数据克隆

2.需要分别给对象的每个属性设置监听

#### 2. Proxy

```javascript
let obj = {
  name: ""
};
let spanName = document.getElementById("spanName");
let inpName = document.getElementById("inpName");
obj = new proxy(obj,{
	get(target,prop) {
		return target[prop];
	},
	set(target,prop,value) {
		target[prop] = value;
    observer();
	}
});
function observer() {
  spanName.innerHTML = obj.name;
  inpName.value = obj.name;
}
inpName.addEventListener("input",function() {
  obj.name = this.value;
})
```



### 数组去重

#### 1. 双层循环

在这个方法中，我们使用循环嵌套，最外层循环 array，里面循环 res，如果 array[i] 的值跟 res[j] 的值相等，就跳出循环，如果都不等于，说明元素是唯一的，这时候 j 的值就会等于 res 的长度，根据这个特点进行判断，将值添加进 res。

#### 2. indexOf

我们可以用 indexOf 简化内层的循环：

```javascript
var array = [1, 1, '1'];
function unique(array) {
    var res = [];
    for (var i = 0, len = array.length; i < len; i++) {
        var current = array[i];
        if (res.indexOf(current) === -1) {
            res.push(current);
        }
    }
    return res;
}
console.log(unique(array));
```

#### 3. 排序后去重

试想我们先将要去重的数组使用 sort 方法排序后，相同的值就会被排在一起，然后我们就可以只判断当前元素与上一个元素是否相同，相同就说明重复，不相同就添加进 res，让我们写个 demo：

```javascript
var array = [1, 1, '1'];

function unique(array) {
    var res = [];
    var sortedArray = array.concat().sort();
    var seen;
    for (var i = 0, len = sortedArray.length; i < len; i++) {
        // 如果是第一个元素或者相邻的元素不相同
        if (!i || seen !== sortedArray[i]) {
            res.push(sortedArray[i])
        }
        seen = sortedArray[i];
    }
    return res;
}
console.log(unique(array));
```

如果我们对一个已经排好序的数组去重，这种方法效率肯定高于使用 indexOf。

#### 4. filter

ES5 提供了 filter 方法，我们可以用来简化外层循环：

比如使用 indexOf 的方法：

```javascript
var array = [1, 2, 1, 1, '1'];

function unique(array) {
    var res = array.filter(function(item, index, array){
        return array.indexOf(item) === index;
    })
    return res;
}
console.log(unique(array));
```

#### 5. Object键值对

因为 1 和 '1' 是不同的，但是因为对象的键值只能是字符串，所以我们可以使用 `typeof item + item` 拼成字符串作为 key 值来避免只保留一个这个问题：

```javascript
var array = [1, 2, 1, 1, '1'];
function unique(array) {
    var obj = {};
    return array.filter(function(item, index, array){
        return obj.hasOwnProperty(typeof item + item) ? false : (obj[typeof item + item] = true)
    })
}
console.log(unique(array)); // [1, 2, "1"]
```

#### 6. ES6

##### Set

```javascript
var array = [1, 2, 1, 1, '1'];
function unique(array) {
   return Array.from(new Set(array));
}
console.log(unique(array)); // [1, 2, "1"]
```

##### Map

```javascript
function unique (arr) {
    const seen = new Map()
    return arr.filter((a) => !seen.has(a) && seen.set(a, 1))
}
```



### JS格式化数字（每3位加逗号）

#### 方法一——拆分数组

执行过程就是把数字转换成字符串后，打散为数组，再从末尾开始，逐个把数组中的元素插入到新数组（result）的开头。每插入一个元素，counter就计一次数（加1），当counter为3的倍数时，就插入一个逗号，但是要注意开头（i为0时）不需要逗号。最后通过调用新数组的join方法得出结果。

```js
// 方法一
function toThousands(num) {
    var result = [],
        counter = 0;
    num = (num || 0).toString().split('');
    for (var i = num.length - 1; i >= 0; i--) {
        counter++;
        result.unshift(num[i]);
        if (!(counter % 3) && i != 0) {
            result.unshift(',');
        }
    }
    return result.join('');
}
```

#### 方法二——方法一的字符串版

方法二是方法一的改良版，不把字符串打散为数组，始终对字符串操作。

```js
// 方法二
function toThousands(num) {
    var result = '',
        counter = 0;
    num = (num || 0).toString();
    for (var i = num.length - 1; i >= 0; i--) {
        counter++;
        result = num.charAt(i) + result;
        if (!(counter % 3) && i != 0) {
            result = ',' + result;
        }
    }
    return result;
}
```

#### 方法三——循环匹配末尾的三个数字

如果数字的位数是3的倍数时，最后一次匹配到的内容肯定是三个数字，但是最前面的三个数字前不需要加逗号；
如果数字的位数不是3的倍数，那num变量最后肯定会剩下1到2个数字，循环过后，要把剩余的数字插入到结果字符串的开头。

```js
// 方法三
function toThousands(num) {
    var num = (num || 0).toString(),
        result = '';
    while (num.length > 3) {
        result = ',' + num.slice(-3) + result;
        num = num.slice(0, num.length - 3);
    }
    if (num) {
        result = num + result;
    }
    return result;
}
```

#### 方法四——分组合并法

先把数字的位数补足为3的倍数，通过正则表达式，将其切割成每三个数字一个分组，再通过join方法添加逗号，最后还要把补的0移除。

```js
// 方法四
function toThousands(num) {
    var num = (num || 0).toString(),
        temp = num.length % 3;
    switch (temp) {
        case 1:
            num = '00' + num;
            break;
        case 2:
            num = '0' + num;
            break;
    }
    return num.match(/\d{3}/g).join(',').replace(/^0+/, '');
}
```



### JS显示转换与隐式转换

#### **显示转换**

显式类型转换主要是指通过String、Number、Boolean等构造方法转换相应的字符串、数字、布尔值

1.**转换成字符串**

![img](https://upload-images.jianshu.io/upload_images/14976946-afd1f47fe1964d91.png)

对象类型，先将对象类型转换成基本类型

- 调用toString()方法，如果返回基本类型，就调用String()构造方法转换该值
- 如果toString()方法返回的不是基本类型，则再调用valueOf()方法，如果返回基本类型的值，则用String()构造方法转换该值。
- 如果valueOf()方法返回的也不是基本类型，就抛出错误

2.**转换成数值**

注意：

- 字符串转换成基本类型，是精确转换
- console.log(Number(' \t\n 3.23322\t '));//Number可以自动去掉两头空白符，输出3.23322

对象类型转换为数字，和上面对象转换为字符串类似，只是转为数字时是先调用**valueOf()**方法，再调用**tostring()**方法：

- 首先调用对象自身的valueOf()方法，如果返回基本类型的值，则用Number构造函数进行转换。
- 如果valueOf()返回的不是基本类型的值，则再调用toString()方法，如果返回基本类型的值，值用Number构造函数进行转换。

3.**转换成布尔值**

除了一下几种情况返回false，其他都返回true

null, 0, -0, +0, NaN, undefined, 空字符串

#### 隐式转换

自动类型转换就是不需要人为强制的进行转换，js会自动将类型转换为需要的类型，所以该转换操作用户是感觉不到的，因此又称为隐性类型转换。自动类型转换实际上和强制类型转换一样，也是通过String()、Number()、Boolean()等构造函数进行转换，只是该操作是JS自己自动完成的而已。

**坑点**：

1.字符串连接符与算术运算符隐式转换规则混淆。当一边是字符串是，加号就是字符串连接符，会将其他数据类型调用String()转换成字符串然后拼接

```javascript
1+'true'   //1true     String(1)+'true'='1true'    
1+true     //2          1+Number(true)=1+1=2
1+undefined   //NaN    1+Number(undefined)=1+NaN=NaN
1+null    //1           1+Number(null)=1+0=1
```

2.关系运算符：会把其他数据类型转换成number之后再比较关系

```javascript
'2'>10   //false 当有一边是字符串时，会将它使用Number()转换，再比较 Number('2')>10=2>10 false
'2'>'10'  //true 当两边都是字符串时，同时转换成number然后比较，字符挨个进行unicode编码的比较 '2'.charCodeAt()>'10'.charCodeAt()=50>49
'abc'>'b'  //false 同理上面
NaN==NaN  //false 
undefined==null  //true
```

3.复杂类型在进行隐式转换时，会先转成String，再转换成Number类型进行比较

```bash
[1,2]=='1,2' //true [1,2].valueOf()=>[1,2] [1,2].toString()=>'1,2'
```



```javascript
// var a=?
if(a==1&&a==2&&a==3){
 console.log(1);
}
如何完善a,使其正确打印1

解答：
复杂数据类型会先调用valueOf()方法，然后转换成number运算,可以重写对象的valueOf()方法

var a={
 i:1,
 valueOf:function(){
         return a.i++
 }
}
```

4.逻辑非隐式转换与关系运算符隐式转换搞混淆

```javascript
[]==0 //true [].valueof()=[] => [].toString()='' =>Number('')=0
![]==0 //true 本质是![]逻辑非与0比较；逻辑非优先级高于关系运算符 Boolean([])=1 => !1=0
[]==![] //true 本质是空数组[]与![]这个逻辑非表达式结果进行比较 [].valueOf().toString()='' ![]=false Number('')==Number(false)
[]==[] //false 引用类型存储在最终栈中存储的是地址，地址不同
{}==!{}  //false 本质是对象{}与!{}的逻辑表达式结果进行比较 {}.valueOf().toString()='[object Object]' !{}=false Number('[object Object]')==Number('false') 结果是false
{}=={}  //地址不同
```

### 为什么0.1 + 0.2 > 0.3

我们都知道，计算机用位来储存及处理数据。每一个二进制数（二进制串）都一一对应一个十进制数。

##### 计算机内部如何表示小数

再看小数怎么用按位计数法表示，以十进制数0.625为例：

| 十进制值 | 进制 | 按位格式 | 描述                                             |
| -------- | ---- | -------- | ------------------------------------------------ |
| 0.625    | 10   | 0.625    | 6x10^-1 + 2x10^-2 + 5x10^-3 = 0.6 + 0.02 + 0.005 |
| 0.625    | 2    | 0.101    | 1x2^-1 + 0 x2^-2 + 1x2^-3 = 1/2 + 0 + 1/8        |

关于十进制与二进制间如何转换，这里不细说，直接给出结论：

**十进制整数转二进制方法：除2取余；十进制小数转二进制方法：乘2除整**

**如何用二进制表示0.1**

十进制0.1转换成二进制，乘2取整过程：

```tap
0.1 * 2 = 0.2 # 0
0.2 * 2 = 0.4 # 0
0.4 * 2 = 0.8 # 0
0.8 * 2 = 1.6 # 1
0.6 * 2 = 1.2 # 1
0.2 * 2 = 0.4 # 0

.....
```

从上面可以看出，0.1的二进制格式是：0.0001100011....。这是一个**二进制无限循环小数**，但计算机内存有限，我们不能用储存所有的小数位数。那么在精度与内存间如何取舍呢？

答案是：**在某个精度点直接舍弃**。当然，代价就是，0.1在计算机内部根本就不是精确的0.1，而是一个有舍入误差的0.1。当代码被编译或解释后，0.1已经被四舍五入成一个与之很接近的计算机内部数字，以至于计算还没开始，一个很小的舍入错误就已经产生了。这也就是 0.1 + 0.2 不等于0.3 的原因。

##### JS中如何进入浮点数运算

1. ##### 将浮点运算转换成整数计算

整数是完全精度的，不存在舍入误差。例如，一些关于人民币的运算，都会以分为基本单位，计算采用分，展示再转换成元。当然，这样也有一些问题，会带来额外的工作量，如果那天人民币新增了一个货币单位，对系统的扩展性也会有考验。

2. **使用bignumber进行运算**



### 闭包

**概念：**

闭包就是定义在一个函数内部，并能够读取其他函数内部变量的函数。

**用途：**

它的最大用处有两个，一个是前面提到的可以读取函数内部的变量，另一个就是让这些变量的值始终保持在内存中。

**使用的注意点：**

1）由于闭包会使得函数中的变量都被保存在内存中，内存消耗很大，所以不能滥用闭包，否则会造成网页的性能问题，在IE中可能导致内存泄露。解决方法是，在退出函数之前，将不使用的局部变量全部删除。

2）闭包会在父函数外部，改变父函数内部变量的值。所以，如果你把父函数当作对象（object）使用，把闭包当作它的公用方法（Public Method），把内部变量当作它的私有属性（private value），这时一定要小心，不要随便改变父函数内部变量的值。



### 数组扁平化

**需求**:多维数组=>一维数组

```js
let ary = [1, [2, [3, [4, 5]]], 6];
let str = JSON.stringify(ary);
```

**第0种处理:直接的调用**

```js
arr_flat = arr.flat(Infinity);
```

**第一种处理**

```js
ary = str.replace(/[\[\]]/g, '').split(',');
```

**第二种处理**

```js
str = str.replace(/[\[\]]/g, '');
str = '[' + str + ']';
ary = JSON.parse(str);
```

**第三种处理：递归处理**

```js
let result = [];
let fn = function (ary) {
  for (let i = 0; i < ary.length; i++) {
    let item = ary[i];
    if (Array.isArray(item)) {
      fn(item);
    } else {
      result.push(item);
    }
  }
}
fn(ary);
```

**第四种处理：用 reduce 实现数组的 flat 方法**

```js
function flatten(ary) {
  return ary.reduce((pre, cur) => {
    return pre.concat(Array.isArray(cur) ? flatten(cur) : cur);
  }, [])
}
```

**第五种处理：扩展运算符**

```js
while (ary.some(Array.isArray)) {
  ary = [].concat(...ary);
}
```

### ES6 Symbol

ES6 引入了一种新的原始数据类型 Symbol ，表示独一无二的值，最大的用法是用来定义对象的唯一属性名。

Symbol 函数栈不能用 new 命令，因为 Symbol 是原始数据类型，不是对象。可以接受一个字符串作为参数，为新创建的 Symbol 提供描述，用来显示在控制台或者作为字符串的时候使用，便于区分。

#### 使用场景

#####  作为属性名

**用法**

由于每一个 Symbol 的值都是不相等的，所以 Symbol 作为对象的属性名，可以保证属性不重名。
**注意点** 

Symbol 值作为属性名时，该属性是公有属性不是私有属性，可以在类的外部访问。但是不会出现在 for...in 、 for...of 的循环中，也不会被 Object.keys() 、 Object.getOwnPropertyNames() 返回。如果要读取到一个对象的 Symbol 属性，可以通过 Object.getOwnPropertySymbols() 和 Reflect.ownKeys() 取到。

##### 定义常量

Symbol.for()
Symbol.for() 类似单例模式，首先会在全局搜索被登记的 Symbol 中是否有该字符串参数作为名称的 Symbol 值，如果有即返回该 Symbol 值，若没有则新建并返回一个以该字符串参数为名称的 Symbol 值，并登记在全局环境中供搜索。

Symbol.keyFor()
Symbol.keyFor() 返回一个已登记的 Symbol 类型值的 key ，用来检测该字符串参数作为名称的 Symbol 值是否已被登记。
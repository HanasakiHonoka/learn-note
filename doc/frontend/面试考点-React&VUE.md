## Vue和React的区别

1. **模版 vs JSX**

React与Vue最大的不同是模板的编写。Vue鼓励你去写近似常规HTML的模板。写起来很接近标准HTML元素，只是多了一些属性。这些属性也可以被使用在单文件组件中，尽管它需要在在构建时将组件转换为合法的JavaScript和HTML。Vue鼓励你去使用HTML模板去进行渲染，因此，通过把原有的模板整合成新的Vue模板，Vue很容易提供旧的应用的升级。这也让新来者很容易适应它的语法。 

另一方面，React推荐你所有的模板通用JavaScript的语法扩展——JSX书写。React/JSX乍看之下，觉得非常啰嗦，但使用JavaScript而不是模板来开发。JSX只是JavaScript混合着XML语法，然而一旦你掌握了它，它使用起来会让你感到畅快。 而相反的观点是Vue的模板语法去除了往视图/组件中添加逻辑的诱惑，保持了**关注点分离**。 值得一提的是，与React一样，Vue在技术上也支持render函数和JSX，但只是不是默认的而已。

2. **数据是不是可变的**

react整体是函数式的思想，把组件设计成纯组件，状态和逻辑通过参数传入，所以在react中，是单向数据流，推崇结合immutable来实现数据不可变。react在setState之后会重新走渲染的流程，如shouldComponentUpdate返回的是true，就继续渲染，如果返回了false，就不会重新渲染，PureComponent就是重写了shouldComponentUpdate，然后在里面作了props和state的浅层对比。

而vue的思想是响应式的，也就是基于是数据可变的，通过对每一个属性建立Watcher来监听，当属性变化的时候，响应式的更新对应的虚拟dom。

总之，react的性能优化需要手动去做，而vue的性能优化是自动的，但是vue的响应式机制也有问题，就是当state特别多的时候，Watcher也会很多，会导致卡顿，所以大型应用（状态特别多的）一般用react，更加可控。

3. **类式的组件写法，还是声明式的写法**

react是类式的写法，api很少，而vue是声明式的写法，通过传入各种options，api和参数都很多。所以react结合typescript更容易一起写，vue稍微复杂。vue结合vue-class-component也可以实现类式的写法，但是还是需要通过decorator来添加声明，并不纯粹。

react可以通过高阶组件（Higher Order Components--HOC）来扩展，而vue需要通过mixins来扩展.React刚开始也有mixin的写法，通过React.createClass的api，不过现在很少用了。
 Vue也不是不能实现高阶组件，只是特别麻烦，因为Vue对与组件的option做了各种处理，想实现高阶组件就要知道每一个option是怎么处理的，然后正确的设置。

4. **什么功能内置，什么交给社区去做**

react做的事情很少，很多都交给社区去做，vue很多东西都是内置的，写起来确实方便一些，
比如 redux的combineReducer就对应vuex的modules，比如reselect就对应vuex的getter和vue组件的computed, vuex的mutation是直接改变的原始数据，而redux的reducer是返回一个全新的state，所以redux结合immutable来优化性能，vue不需要。

**react整体的思路就是函数式，所以推崇纯组件，数据不可变，单向数据流，当然需要双向的地方也可以做到，比如结合redux-form，而vue是基于可变数据的，支持双向绑定。react组件的扩展一般是通过高阶组件，而vue组件会使用mixin。vue内置了很多功能，而react做的很少，很多都是由社区来完成的，vue追求的是开发的简单，而react更在乎方式是否正确。**

##  Vue

### Vue的生命周期

beforeCreated

created

beforeMount

mounted

beforeUpdate

updated

beforeDestroy

destroyed

### Vue组件通信
父组件A通过props的方式向子组件B传递，B to A 通过在 B 组件中 $emit, A 组件中 v-on 的方式实现
https://segmentfault.com/a/1190000019208626

### Vue双向绑定原理

在Vue2.0中，数据双向绑定就是通过 Object.defineProperty 去监听对象的每一个属性，然后在get,set方法中通过发布订阅者模式来实现的数据响应，但是存在一定的缺陷，比如只能监听已存在的属性，对于新增删除属性就无能为力了，同时无法监听数组和对象的变化，所以在Vue3.0中将其换成了功能更强大的Proxy。

```javascript
let spanName = document.getElementById('spanName');
let inpName = document.getElementById('inpName');
let newObj = JSON.parse(JSON.stringify(obj));
Object.defineProperty(obj,'name',{
  get() {
    return newObj.name;
  },
  set(val) {
    if(val === newObj.name) reutrn;
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



Proxy是ES6的一个新特性，是在目标对象之前架设一层“拦截”，外界对该对象的访问，都必须先通过这层拦截，因此提供了一种机制，可以对外界的访问进行过滤和改写。

```javascript
let obj = {
  name: ""
};
obj = new Proxy(obj, {
    get(target,key) {
      return target[key];
    },
    set(target,key,value) {
      target[key]=value;
      observer(value);
    }
  })
  function observer(val) {
    spanName.innerHTML = val;
    inpName.value = val;
  }
  inpName.addEventListener('input', function() {
    obj.name = this.value;
  })
```

##### Object.defineProperty和Proxy的区别

- *defineproperty只能监听某个属性不能全对象监听*

  proxy不用设置具体属性

  defineproperty监听需要知道那个对象的那个属性，而proxy只需要知道那个对象就可以了。也就是会省去for in 循环提高了效率

- Object.defineProperty对对象自身做修改, 而Proxy只是在Object基础上加一层拦截，不修改原对象

- 监听不了数组的变化

- 监听手段比较单一，只能监听set和get, Proxy有10几种监听

- 必须得把所有的属性全部添加defineProperty, Proxy对整个对象都会进行拦截

- Proxy是代理在`对象`级别的，defineProperty是代理到`静态的值`级别，所以Proxy的强大就在这里

- Proxy可以监听的类型：

  <img src="https://z3.ax1x.com/2021/09/07/hInWJx.png" alt="hInWJx.png" style="zoom:50%;" />

#### Proxy监听数组访问

```js
var arr = [1,2,3,4];
let arrProxy = new Proxy(arr, {
    get(target, propKey) {
        if (Array.isArray(target) && typeof Array.prototype[propKey] === 'function') {
            Promise.resolve().then(e => {
                console.log('操作了数组', propKey);
            })
        }
        return target[propKey]
    }
})
arrProxy.push(5);
console.log('push结束了');
// push结束了
// 操作了数组 push
```

#### .configurable：可配执行 .enumerble：枚举性 .writable:可读写性 .value:数据值

1) 在使用Object.defineProperty、Object.defineProperties 或 Object.create 函数的情况下添加数据属性，writable、enumerable和configurable默认值为false。

2) 使用对象直接量创建的属性，writable、enumerable和configurable特性默认为true。

## React




### React的生命周期

1. 挂载卸载过程

* constructor()

* componentWillMount()

* componentDidMount()

* componentWillUnmount ()

2. 更新过程

- componentWillReceiveProps (nextProps)
- shouldComponentUpdate(nextProps,nextState)
- componentWillUpdate (nextProps,nextState)
- componentDidUpdate(prevProps,prevState)
- render()

3. React新增的生命周期(个人补充)

- getDerivedStateFromProps(nextProps, prevState)
-  getSnapshotBeforeUpdate(prevProps, prevState)

![hL7su6.md.png](https://z3.ax1x.com/2021/09/09/hL7su6.md.png)

### 虚拟DOM

所谓的Virtual DOM基本上说就是它名字的意思：虚拟DOM，DOM树的虚拟表现。它的诞生是基于这么一个概念：改变真实的DOM状态远比改变一个JavaScript对象的花销要大得多。 Virtual DOM是一个映射真实DOM的JavaScript对象，如果需要改变任何元素的状态，那么是先在Virtual DOM上进行改变，而不是直接改变真实的DOM。当有变化产生时，一个新的Virtual DOM对象会被创建并计算新旧Virtual DOM之间的差别。之后这些差别会应用在真实的DOM上。 

 当新一项被加进去这个JavaScript对象时，一个函数会计算新旧Virtual DOM之间的差异并反应在真实的DOM上。计**算差异的算法是高性能框架的秘密所在**，React和Vue在实现上有点不同。 Vue宣称可以更快地计算出Virtual DOM的差异，这是由于它在渲染过程中，会**跟踪每一个组件的依赖关系**，**不需要**重新渲染整个组件树。 而对于React而言，每当应用的状态被改变时，**全部子组件都会重新渲染**。当然，这可以通过shouldComponentUpdate这个生命周期方法来进行控制，但Vue将此视为默认的优化。 小结：如果你的应用中，交互复杂，需要处理大量的UI变化，那么使用Virtual DOM是一个好主意。如果你更新元素并不频繁，那么Virtual DOM并不一定适用，性能很可能还不如直接操控DOM。

### diff策略



![4dmfsK.png](https://z3.ax1x.com/2021/09/23/4dmfsK.png)

#### 虚拟dom库

- **Snabbdom**

- - Vue.js2.x内部使用的虚拟DOM就是改造的Snabbdom
  - 大约200SLOC(single line of code)
  - 通过模块可扩展
  - 源码使用TypeScript开发
  - 最快的Virtual DOM之一

#### diff算法

##### snabbdom的核心

- `init()`设置模块.创建`patch()`函数
- 使用`h()`函数创建JavaScript对象`(Vnode)`描述`真实DOM`
- `patch()`比较`新旧两个Vnode`
- 把变化的内容更新到`真实DOM树`

#### init函数

```js
import {init} from 'snabbdom/build/package/init.js'
import {h} from 'snabbdom/build/package/h.js'

// 1.导入模块
import {styleModule} from "snabbdom/build/package/modules/style";
import {eventListenersModule} from "snabbdom/build/package/modules/eventListeners";

// 2.注册模块
const patch = init([
  styleModule,
  eventListenersModule
])

// 3.使用h()函数的第二个参数传入模块中使用的数据(对象)
let vnode = h('div', [
  h('h1', {style: {backgroundColor: 'red'}}, 'Hello world'),
  h('p', {on: {click: eventHandler}}, 'Hello P')
])

function eventHandler() {
  alert('疼,别摸我')
}

const app = document.querySelector('#app')

patch(app,vnode)
复制代码
```

init函数时设置模块,然后创建patch()函数,当init使用了导入的模块就能够在h函数中用这些模块提供的api去创建`虚拟DOM(Vnode)对象`;在上文中就使用了`样式模块`以及`事件模块`让创建的这个虚拟DOM具备样式属性以及事件属性,最终通过`patch函数`对比`两个虚拟dom`(会先把app转换成虚拟dom),更新视图。

#### h函数

一些地方也会用`createElement`来命名,它们是一样的东西,都是创建`虚拟DOM`的。

**总结**:`h函数`先生成一个`vnode`函数,然后`vnode`函数再生成一个`Vnode`对象(虚拟DOM对象)。

#### patch函数(核心)

- pactch(oldVnode,newVnode)
- 把新节点中变化的内容渲染到真实DOM,最后返回新节点作为下一次处理的旧节点(核心)
- 对比新旧`VNode`是否相同节点(节点的key和sel相同)
- 如果不是相同节点,删除之前的内容,重新渲染
- 如果是相同节点,再判断新的`VNode`是否有`text`,如果有并且和`oldVnode`的`text`不同直接更新文本内容`(patchVnode)`
- 如果新的VNode有children,判断子节点是否有变化`(updateChildren,最麻烦,最难实现)`

**patchVnode：**

![4dwSGq.png](https://z3.ax1x.com/2021/09/23/4dwSGq.png)

**updateChildren:**

这个函数我分为三个部分,
- `部分1:声明变量`,
- `部分2:同级别节点比较`,
- `部分3:循环结束的收尾工作`

![4dwdQf.png](https://z3.ax1x.com/2021/09/23/4dwdQf.png)

`同级别节点比较`的`五种`情况:

- 1. `oldStartVnode/newStartVnode`(旧开始节点/新开始节点)相同
  2. `oldEndVnode/newEndVnode`(旧结束节点/新结束节点)相同
  3. `oldStartVnode/newEndVnode`(旧开始节点/新结束节点)相同
  4. `oldEndVnode/newStartVnode`(旧结束节点/新开始节点)相同
  5. `特殊情况当1,2,3,4的情况都不符合`的时候就会执行,在`oldVnodes`里面寻找跟`newStartVnode`一样的节点然后位移到`oldStartVnode`,若没有找到在就`oldStartVnode`创建一个

- 执行过程是一个循环,在每次循环里,只要执行了上述的情况的五种之一就会结束一次循环

- `循环结束的收尾工作`:直到oldStartIdx>oldEndIdx || newStartIdx>newEndIdx(代表旧节点或者新节点已经遍历完)

### React函数式组件和类组件的区别

1. 函数组件看似只是一个返回值是DOM结构的函数，其实它的背后是**无状态组件**（Stateless Components）的思想。**函数组件中，你无法使用State，也无法使用组件的生命周期方法，**这就决定了函数组件都是展示性组件（Presentational Components），接收Props，渲染DOM，而不关注其他逻辑。

2. 函数组件中没有this。所以你再也不需要考虑this带来的烦恼。而在类组件中，你依然要记得绑定this这个琐碎的事情。

3. 函数组件更容易理解。当你看到一个函数组件时，你就知道它的功能只是接收属性，渲染页面，它不执行与UI无关的逻辑处理，它只是一个**纯函数**。而不用在意它返回的DOM结构有多复杂。

4. 函数式组件可以**直接调用**，返回一个新的React元素；类组件在调用时是需要**创建一个实例**的，然后通过**调用实例里的render方法**来返回一个React元素。
5. 类组件没有具体的要求。函数式组件一般是用在大型项目中来分割大组件（函数式组件不用创建实例，所有更高效），一般情况下能用函数式组件就不用类组件，提升效率。

### React key的作用

- key相同：若组件属性有所变化，则react只更新组件对应的属性；没有变化则不更新。
- key值不同：则react先销毁该组件，然后重新创建该组件。

**用法：**

- 纯静态的同级同结构节点的渲染，可以采用index作为key的值，因为这里不需要动态去变化节点里面的内容的值；
- 对于一组动态变化的数组来说，采用index作为key的值，会有可能出现问题，因为index的值和数组内的元素内容不具有关联性，所以即使采用了index作为key，子组件的内容有可能不会随着属性的变化而发生变化（只要组件内该元素不与属性构成联系），所以一般采用数组中元素的某一个唯一值作为key，这样一来，只要统一位置的节点的key值不一致，就会直接销毁和新建而不是直接更新；
- 对于一个不想受到父组件属性状态影响而导致没必要的渲染的组件，可以采用key值，因为只要key值不发生改变，组件的属性不变，即使父组件渲染，该组件也不会发生变化，只有组件的状态或者属性发生变化，组件才会二次渲染；一旦key值变化，就直接组件销毁然后再新建该组件。
- 事实上如果key值不唯一的话，react只会渲染第一个，剩下的react会认为是同一项，直接忽略。

### React父子组件通信方法

- 父组件向子组件通信：使用 props
- 子组件向父组件通信：使用 props 回调
- 跨级组件间通信：使用 context 对象
- 非嵌套组件间通信：使用事件订阅

eg：

#### context

父组件需要声明自己支持 context，并提供 context 中属性的 PropTypes

子组件需要声明自己需要使用 context，并提供其需要使用的 context 属性的 PropTypes

父组件需提供一个 getChildContext 函数，以返回一个初始的 context 对象

```jsx
export const {Provider,Consumer} = React.createContext("默认名称");
// father <Provider value={name}>
//son <Customer>
```

#### EventEmitter

新建一个 ev.js，引入 events 包，并向外提供一个事件对象，供通信时使用：

```js
import { EventEmitter } from "events";
export default new EventEmitter();
```

Foo.js：

```jsx
import React,{ Component } from "react";
import emitter from "./ev"

export default class Foo extends Component{
    constructor(props) {
        super(props);
        this.state = {
            msg:null,
        };
    }
    componentDidMount(){
        // 声明一个自定义事件
        // 在组件装载完成以后
        this.eventEmitter = emitter.addListener("callMe",(msg)=>{
            this.setState({
                msg
            })
        });
    }
    // 组件销毁前移除事件监听
    componentWillUnmount(){
        emitter.removeListener(this.eventEmitter);
    }
    render(){
        return(
            <div>
                { this.state.msg }
                我是非嵌套 1 号
            </div>
        );
    }
}
```

Boo.js：

```jsx
import React,{ Component } from "react";
import emitter from "./ev"

export default class Boo extends Component{
    render(){
        const cb = (msg) => {
            return () => {
                // 触发自定义事件
                emitter.emit("callMe","Hello")
            }
        }
        return(
            <div>
                我是非嵌套 2 号
                <button onClick = { cb("blue") }>点击我</button>
            </div>
        );
    }
}
```



## jquery ajax, Axios, Fetch区别

### jQuery ajax

```js
$.ajax({
   type: 'POST',
   url: url,
   data: data,
   dataType: dataType,
   success: function () {},
   error: function () {}
});
```

是对原生XHR的封装，除此以外还增添了对JSONP的支持。

缺点：

- 本身是针对MVC的编程,不符合现在前端MVVM的浪潮
- 基于原生的XHR开发，XHR本身的架构不清晰，已经有了fetch的替代方案
- JQuery整个项目太大，单纯使用ajax却要引入整个JQuery非常的不合理（采取个性化打包的方案又不能享受CDN服务）

### Axios

```js
axios({
    method: 'post',
    url: '/user/12345',
    data: {
        firstName: 'Fred',
        lastName: 'Flintstone'
    }
})
.then(function (response) {
    console.log(response);
})
.catch(function (error) {
    console.log(error);
});
```

Axios本质上也是对原生XHR的封装，只不过它是Promise的实现版本，符合最新的ES规范，从它的官网上可以看到它有以下几条特性：

- 从 node.js 创建 http 请求
- 支持 Promise API
- 客户端支持防止CSRF
- **提供了一些并发请求的接口**（axios.all和axios.spread）

这个支持防止CSRF其实挺好玩的，是怎么做到的呢，就是让你的每个请求都带一个从cookie中拿到的key, 根据浏览器同源策略，假冒的网站是拿不到你cookie中得key的，这样，后台就可以轻松辨别出这个请求是否是用户在假冒网站上的误导输入，从而采取正确的策略。

### Fetch

Fetch API提供了一个JavaScript 接口，用于访问和操纵HTTP 管道的部分，例如请求和响应。

1. 它提供了一个全局的`fetch()`方法，该方法提供了一种简单，合理的方式来跨网络以获取资源。 fetch 是一种 HTTP 数据请求方式，是 XMLHTTPRequest 的一种替代方案。 fetch 不是 ajax 的进一步封装，而是原生 js。 Fetch 函数就是原生 js。

2. 当接收到一个代表错误的HTTP状态码时，从`fetch()`返回的Promise 不会被标记为reject，即使该HTTP相应的状态码是404 或者 500。 相反，它会将Promise状态标记为 resolve,仅当网络故障时或请求被阻止时，才会标记为reject。

3. 默认情况， fetch不会从服务端发送或接受任何cookie，如果站点依赖于用户session，则会导致未经认证的请求。
4. **response** 这里的response 只是一个HTTP响应，而不是真的JSON。为了获取JSON的内容我们需要使用对应的 .json() 、.text() 、 .blob()方法。

**优缺点：**

- 符合关注分离，没有将输入、输出和用事件来跟踪的状态混在一个对象里。
- 更加底层，提供了API丰富（request、response）
- 脱离了XHR，是ES规范里新的实现方式
- 1. fetch 只对网络请求报错，对400/500 都当成功的请求，需要封装去处理
- 2. fetch 默认不会带cookie，需要添加配置项
- 3. fetch不支持abort，不支持超时控制，使用setTimeout 及 Promise.reject 的实现的超时控制并不能阻止请求过程继续在后台运行，造成了量的浪费
- 4. fetch没有办法原生检测请求的进度，而XHR可以

 

## React和Vue相比原生JS有哪些提高

- 不用写一大堆操作 DOM 的代码了，可能提高了工作效率

- 操作 DOM 代码写的不好的话，可能会对性能造成影响

- 框架流行以后，就有人会在这基础上造轮子，比如 UI 框架、组件通信，方便你更快的实现业务

- 这些框架都有虚拟 DOM 的机制，这层机制可以实现跨端

- 1，引入了前端数据流概念，数据和视图分离，数据驱动视图

  2，引入组件化概念，方便复用，方便维护，方便扩展

## React hooks

### useState

与 class 组件中的 `setState` 方法不同，`useState` 不会自动合并更新对象。你可以用函数式的 `setState` 结合展开运算符来达到合并更新对象的效果。

```jsx
setState(prevState => {
  // 也可以使用 Object.assign
  return {...prevState, ...updatedValues};
});
```

### useEffect

该 Hook 接收一个包含命令式、且可能有副作用代码的函数。

在函数组件主体内（这里指在 React 渲染阶段）改变 DOM、添加订阅、设置定时器、记录日志以及执行其他包含副作用的操作都是不被允许的，因为这可能会产生莫名其妙的 bug 并破坏 UI 的一致性。

使用 `useEffect` 完成副作用操作。赋值给 `useEffect` 的函数会在组件渲染到屏幕之后执行。你可以把 effect 看作从 React 的纯函数式世界通往命令式世界的逃生通道。

#### effect 的执行时机

与 `componentDidMount`、`componentDidUpdate` 不同的是，在浏览器完成布局与绘制**之后**，传给 `useEffect` 的函数会延迟调用。这使得它适用于许多常见的副作用场景，比如设置订阅和事件处理等情况，因此不应在函数中执行阻塞浏览器更新屏幕的操作。

然而，并非所有 effect 都可以被延迟执行。例如，在浏览器执行下一次绘制前，用户可见的 DOM 变更就必须同步执行，这样用户才不会感觉到视觉上的不一致。（概念上类似于被动监听事件和主动监听事件的区别。）React 为此提供了一个额外的 [`useLayoutEffect`](https://react.docschina.org/docs/hooks-reference.html#uselayouteffect) Hook 来处理这类 effect。它和 `useEffect` 的结构相同，区别只是调用时机不同。

虽然 `useEffect` 会在浏览器绘制后延迟执行，但会保证在任何新的渲染前执行。React 将在组件更新前刷新上一轮渲染的 effect。

执行 useEffect 时，将 useEffect Hook 添加到 Hook 链表中，然后创建 fiberNode 的 updateQueue，并把本次 effect 添加到 updateQueue 中。

- 对比 useEffect 依赖项是否有改变的底层实现是 `Object.is()`;
- useEffect 依赖项的作用是决定 effect 是否要执行。这样可以避免重复调用 effect，提升程序执行效率；
- effect 清除函数即时 effect 中 `return` 的内容。在组件本次渲染中的清除函数会在下一次渲染时才执行；
- useEffect 的执行流程：组件初次渲染时，在 UI 完成渲染后（DOM 更新完毕）会执行一次 effect；在组件重新渲染时，会对比依赖项在本次渲染和上一次渲染的值/引用是否有发生改变，根据对比结果给 effect 打上相应的 tag，然后等在 UI 渲染完成后，先执行上一次渲染的 effect 清除函数，然后再根据本次 effect 的 tag 来决定是否要执行本次 effect；
- 在组件销毁之前，会执行上一次渲染的 effect 清除函数
- 上一次渲染时的 effect 清除函数和本次 effect 放在 UI 渲染完成后才执行的原因是避免了 effect 的执行阻塞 UI 渲染（DOM 更新）

### useContext

```jsx
const themes = {
  light: {
    foreground: "#000000",
    background: "#eeeeee"
  },
  dark: {
    foreground: "#ffffff",
    background: "#222222"
  }
};

const ThemeContext = React.createContext(themes.light);

function App() {
  return (
    <ThemeContext.Provider value={themes.dark}>
      <Toolbar />
    </ThemeContext.Provider>
  );
}

function Toolbar(props) {
  return (
    <div>
      <ThemedButton />
    </div>
  );
}

function ThemedButton() {
  const theme = useContext(ThemeContext);  
  return (    
    <button style={{ background: theme.background, 
        color: theme.foreground }}>      
      I am styled by theme context!    
    </button>  
  );
}
```

### useReducer

```jsx
// 语法
const [state, dispatch] = useReducer(reducer, initialArg, init);

// 实例
const initialState = {count: 0};

function reducer(state, action) {
  switch (action.type) {
    case 'increment':
      return {count: state.count + 1};
    case 'decrement':
      return {count: state.count - 1};
    default:
      throw new Error();
  }
}

function Counter() {
  const [state, dispatch] = useReducer(reducer, initialState);
  return (
    <>
      Count: {state.count}
      <button onClick={() => dispatch({type: 'decrement'})}>-</button>
      <button onClick={() => dispatch({type: 'increment'})}>+</button>
    </>
  );
}
```

### useCallback和useMemo

#### 相同点：

useCallback 和 useMemo 都是性能优化的手段，类似于类组件中的 shouldComponentUpdate，在子组件中使用 shouldComponentUpdate， 判定该组件的 props 和 state 是否有变化，从而避免每次父组件render时都去重新渲染子组件。

#### 区别：

useCallback 和 useMemo 的区别是useCallback返回一个函数，当把它返回的这个函数作为子组件使用时，可以避免每次父组件更新时都重新渲染这个子组件，

```jsx
const renderButton = useCallback(
     () => (
         <Button type="link">
            {buttonText}
         </Button>
     ),
     [buttonText]    // 当buttonText改变时才重新渲染renderButton
);
```

useMemo返回的的是一个值，用于避免在每次渲染时都进行高开销的计算。例：

// 仅当num改变时才重新计算结果

```jsx
const result = useMemo(() => {
    for (let i = 0; i < 100000; i++) {
      (num * Math.pow(2, 15)) / 9;
    }
}, [num]);
```

#### useCallback

```jsx
const memoizedCallback = useCallback(
  () => {
    doSomething(a, b);
  },
  [a, b],
);
```

把内联回调函数及依赖项数组作为参数传入 `useCallback`，它将返回该回调函数的 memoized 版本，该回调函数仅在某个依赖项改变时才会更新。当你把回调函数传递给经过优化的并使用引用相等性去避免非必要渲染（例如 `shouldComponentUpdate`）的子组件时，它将非常有用。

`useCallback(fn, deps)` 相当于 `useMemo(() => fn, deps)`。

#### useMemo

```jsx
const memoizedValue = useMemo(() => computeExpensiveValue(a, b), [a, b]);
```

把“创建”函数和依赖项数组作为参数传入 `useMemo`，它仅会在某个依赖项改变时才重新计算 memoized 值。这种优化有助于避免在每次渲染时都进行高开销的计算。

记住，传入 `useMemo` 的函数会在渲染期间执行。请不要在这个函数内部执行与渲染无关的操作，诸如副作用这类的操作属于 `useEffect` 的适用范畴，而不是 `useMemo`。

如果没有提供依赖项数组，`useMemo` 在每次渲染时都会计算新的值。



### useRef

```jsx
const refContainer = useRef(initialValue);
```

`useRef` 返回一个可变的 ref 对象，其 `.current` 属性被初始化为传入的参数（`initialValue`）。返回的 ref 对象在组件的整个生命周期内保持不变。

一个常见的用例便是命令式地访问子组件：

```jsxj
function TextInputWithFocusButton() {
  const inputEl = useRef(null);
  const onButtonClick = () => {
    // `current` 指向已挂载到 DOM 上的文本输入元素
    inputEl.current.focus();
  };
  return (
    <>
      <input ref={inputEl} type="text" />
      <button onClick={onButtonClick}>Focus the input</button>
    </>
  );
}
```

本质上，`useRef` 就像是可以在其 `.current` 属性中保存一个可变值的“盒子”。

你应该熟悉 ref 这一种[访问 DOM](https://react.docschina.org/docs/refs-and-the-dom.html) 的主要方式。如果你将 ref 对象以 `<div ref={myRef} />` 形式传入组件，则无论该节点如何改变，React 都会将 ref 对象的 `.current` 属性设置为相应的 DOM 节点。

然而，`useRef()` 比 `ref` 属性更有用。它可以[很方便地保存任何可变值](https://react.docschina.org/docs/hooks-faq.html#is-there-something-like-instance-variables)，其类似于在 class 中使用实例字段的方式。

这是因为它创建的是一个普通 Javascript 对象。而 `useRef()` 和自建一个 `{current: ...}` 对象的唯一区别是，`useRef` 会在每次渲染时返回**同一个** ref 对象。

请记住，当 ref 对象内容发生变化时，`useRef` 并*不会*通知你。变更 `.current` 属性不会引发组件重新渲染。如果想要在 React 绑定或解绑 DOM 节点的 ref 时运行某些代码，则需要使用[回调 ref](https://react.docschina.org/docs/hooks-faq.html#how-can-i-measure-a-dom-node) 来实现。

## React Router

### 使用 hash 来切换

在进入页面的时候获取当前 url 的 hash 值，根据这个 hash 值去更新 `UI` 从而通过 `showUI()` 来切换到对应的组件

同时添加 onClick 事件点击不同按钮时，就在 url 设置对应的 hash，并切换对应的组件。

这样其实已经满足我们的要求了，如果我在地址栏里输入 `localhost:8080/#login`，就会显示 `<Login/>`。但是这个 “#” 符号不太好看，如果输入 `localhost:8080/login` 就完美了。

### 使用 pathname 切换

如果要做得像上面说的那样，我们只能用 `window.location.pathname` 去修改 url 了。只要把上面代码里的 `hash` 改成 `pathname` 就好了。

### 使用history去切换

幸运的是 H5 提供了一个好用的 [history API](https://developer.mozilla.org/zh-CN/docs/Web/API/History_API)，使用 `window.history.pushState()` 使得我们即可以修改 url 也可以不刷新页面，一举两得。

现在只需要修改点击回调里的 `window.location.pathname = 'xxx'` 就可以了，用 `window.history.pushState()` 去代替。

### 约束

在前端使用路由要有个前提，那就是后端要将全部的路径都指向首页，即 `index.html`。否则后端会出现 404 错误。

什么叫全部路径都指向首页呢？我们想一下正常的多页网页是怎么样的：**如果访问了一个不存在的路径，如 `localhost:8080/fuck.html`，那么后端会返回一个 `error.html`，里面内容显示 “找不到网页”，这种情况就是后端处理网页的路由了。因为正是后端根据不同 url 返回不同的 `xxx.html` 呀。**

如果前端使用路由，那么后端将全部路径都指向 `index.html`。**当我们访问到一个不存在路径时，如 `localhost:8080/fuck`，后端不管三七二十一返回 `index.html`。但是这个 `index.html` 里有我们写的 JS 代码（React 打包后的）呀，这 JS 代码其中就包含了我们做的路由。所以我们的路由发现不存在这个路径时，就切换到 Error 组件来充当 “找不到网页” 的 HTML 文件。这就叫前端控制路由。**

## Redux

### redux 为什么要把 reducer 设计成纯函数

### 1. redux三大原则

1. 单一数据流 整个应用state都被储存在一个store里面 构成一个Object tree
2. State是只读的 唯一改变state的方法就是触发action, action是一个用于描述已发生事件的普通对象
3. 使用纯函数来执行修改 为了描述action如何改变state tree， 你需要编写reducers

把reducer设计成纯函数，可以实现时间旅行，记录/回放或者热加载
首先你得看看文档怎么说reducer的作用的，‘**接收旧的 state 和 action，返回新的 state**’，您可得瞧好咯，他就是起一个对数据做简单处理后返回state的作用，为什么只起这个作用，这时用设计这个词回答这个问题才恰当，**因为redux把reducer设计成只负责这个作用**。很白痴的问答对吧，所以题目的答案也就简单了，reducer的职责不允许有副作用，副作用简单来说就是不确定性，如果reducer有副作用，那么返回的state就不确定，**举个例子**，你的reducer就做了一个value = value + 1这个逻辑，然后返回state为{value}，ok，这个过程太jr纯了，然后你可能觉得要加个请求来取得value后再加1，那么你的逻辑就是value = getValue() + 1, getValue是个请求函数，返回一个值，这种情况，退一万步讲，如果你的网络请求这次出错，那么getValue就返回的不是一个数值，value就不确定了，所以return的state你也不确定了，前端UI拿到的数据也不确定了，所以就是这个环节引入了副作用，redux设计好的规范就被你破坏了，redux就没卵用了。

最后我回答下如何解决这个副作用，实际上也很白痴的问题，这里的请求可以放在reducer之前，你先请求，该做出错处理的就做出错处理，等拿到实际数据后在发送action来调用reducer。这样通过前移副作用的方式，使reducer变得纯洁。

### 2. 在 React 中如何使用 Redux 的 connect() ?

您需要按照两个步骤在容器中使用您的 Store：

1. **使用`mapStateToProps()`：**它将 Store 中的状态变量映射到您指定的属性。

2. **将上述属性连接到容器：**`mapStateToProps`函数返回的对象连接到容器。你可以从`react-redux`导入`connect()`。

   ```js
   import React from 'react'
   import { connect } from 'react-redux'
   
   class App extends React.Component {
     render() {
       return <div>{this.props.containerData}</div>
     }
   }
   
   function mapStateToProps(state) {
     return { containerData: state.data }
   }
   
   export default connect(mapStateToProps)(App)
   ```

### 3. mapStateToProps() 和 mapDispatchToProps() 之间有什么区别?

`mapStateToProps()`是一个实用方法，它可以帮助您的组件获得最新的状态（由其他一些组件更新）：

```js
const mapStateToProps = (state) => {
  return {
    todos: getVisibleTodos(state.todos, state.visibilityFilter)
  }
}
```

`mapDispatchToProps()`是一个实用方法，它可以帮助你的组件触发一个动作事件（可能导致应用程序状态改变的调度动作）：

```js
const mapDispatchToProps = (dispatch) => {
  return {
    onTodoClick: (id) => {
      dispatch(toggleTodo(id))
    }
  }
}
```

### 4. 为什么 Redux 状态函数称为 reducers ?

Reducers 总是返回状态的累积（基于所有先前状态和当前 Action）。因此，它们充当了状态的 Reducer。每次调用 Redux reducer 时，状态和 Action 都将作为参数传递。然后基于该 Action 减少（或累积）该状态，然后返回下一状态。您可以*reduce*一组操作和一个初始状态（Store），在该状态下执行这些操作以获得最终的最终状态。

### 5. 如何在 Redux 中发起 AJAX 请求?

您可以使用`redux-thunk`中间件，它允许您定义异步操作。

让我们举个例子，使用*fetch API*将特定帐户作为 AJAX 调用获取：

```js
export function fetchAccount(id) {
  return dispatch => {
    dispatch(setLoadingAccountState()) // Show a loading spinner
    fetch(`/account/${id}`, (response) => {
      dispatch(doneFetchingAccount()) // Hide loading spinner
      if (response.status === 200) {
        dispatch(setAccount(response.json)) // Use a normal function to set the received state
      } else {
        dispatch(someError)
      }
    })
  }
}

function setAccount(data) {
 return { type: 'SET_Account', data: data }
}
```

### 6. 访问 Redux Store 的正确方法是什么?

在组件中访问 Store 的最佳方法是使用`connect()`函数，该函数创建一个包裹现有组件的新组件。此模式称为*高阶组件*，通常是在 React 中扩展组件功能的首选方式。这允许您将状态和 Action 创建者映射到组件，并在 Store 更新时自动传递它们。

我们来看一个使用 connect 的`<FilterLink>`组件的例子：

```js
import { connect } from 'react-redux'
import { setVisibilityFilter } from '../actions'
import Link from '../components/Link'

const mapStateToProps = (state, ownProps) => ({
  active: ownProps.filter === state.visibilityFilter
})

const mapDispatchToProps = (dispatch, ownProps) => ({
  onClick: () => dispatch(setVisibilityFilter(ownProps.filter))
})

const FilterLink = connect(
  mapStateToProps,
  mapDispatchToProps
)(Link)

export default FilterLink
```

由于它具有相当多的性能优化并且通常不太可能导致错误，因此 Redux 开发人员几乎总是建议使用`connect()`直接访问 Store（使用上下文API）。

```js
class MyComponent {
  someMethod() {
    doSomethingWith(this.context.store)
  }
}
```

### 7. React Redux 中展示组件和容器组件之间的区别是什么?

**展示组件**是一个类或功能组件，用于描述应用程序的展示部分。

**容器组件**是连接到 Redux Store的组件的非正式术语。容器组件*订阅*Redux 状态更新和*dispatch*操作，它们通常不呈现 DOM 元素；他们将渲染委托给展示性的子组件。

### 8. Redux 中常量的用途是什么?

常量允许您在使用 IDE 时轻松查找项目中该特定功能的所有用法。它还可以防止你拼写错误，在这种情况下，你会立即得到一个`ReferenceError`。

通常我们会将它们保存在一个文件中（`constants.js`或`actionTypes.js`）。

```js
export const ADD_TODO = 'ADD_TODO'
export const DELETE_TODO = 'DELETE_TODO'
export const EDIT_TODO = 'EDIT_TODO'
export const COMPLETE_TODO = 'COMPLETE_TODO'
export const COMPLETE_ALL = 'COMPLETE_ALL'
export const CLEAR_COMPLETED = 'CLEAR_COMPLETED'
```

在 Redux 中，您可以在两个地方使用它们：

1. **在 Action 创建时:**

   让我们看看`actions.js`:

   ```js
   import { ADD_TODO } from './actionTypes';
   
   export function addTodo(text) {
     return { type: ADD_TODO, text }
   }
   ```

2. **在 reducers 里:**

   让我们创建`reducer.js`文件:

   ```js
   import { ADD_TODO } from './actionTypes'
   
   export default (state = [], action) => {
     switch (action.type) {
       case ADD_TODO:
         return [
           ...state,
           {
             text: action.text,
             completed: false
           }
         ];
       default:
         return state
     }
   }
   ```

### 9. 什么是 redux-saga?

`redux-saga`是一个库，旨在使 React/Redux 项目中的副作用（数据获取等异步操作和访问浏览器缓存等可能产生副作用的动作）更容易，更好。

这个包在 NPM 上有发布:

```shell
$ npm install --save redux-saga
```

#### redux-saga 的模型概念是什么?

*Saga*就像你的项目中的一个单独的线程，它独自负责副作用。`redux-saga`是一个 redux*中间件*，这意味着它可以在项目启动中使用正常的 Redux 操作，暂停和取消该线程，它可以访问完整的 Redux 应用程序状态，并且它也可以调度 Redux 操作。

#### 在 redux-saga 中`call()`和`put()`之间有什么区别?

`call()`和`put()`都是 Effect 创建函数。`call()`函数用于创建 Effect 描述，指示中间件调用 promise。`put()`函数创建一个 Effect，指示中间件将一个 Action 分派给 Store。

让我们举例说明这些 Effect 如何用于获取特定用户数据。

```
~~~js
function* fetchUserSaga(action) {
  // `call` function accepts rest arguments, which will be passed to `api.fetchUser` function.
  // Instructing middleware to call promise, it resolved value will be assigned to `userData` variable
  const userData = yield call(api.fetchUser, action.userId)

  // Instructing middleware to dispatch corresponding action.
  yield put({
    type: 'FETCH_USER_SUCCESS',
    userData
  })
}
~~~
```

#### 什么是 Redux Thunk?

*Redux Thunk*中间件允许您编写返回函数而不是 Action 的创建者。 thunk 可用于延迟 Action 的发送，或仅在满足某个条件时发送。内部函数接收 Store 的方法`dispatch()`和`getState()`作为参数。

#### `redux-saga`和`redux-thunk`之间有什么区别?

*Redux Thunk*和*Redux Saga*都负责处理副作用。在大多数场景中，Thunk 使用*Promises*来处理它们，而 Saga 使用*Generators*。Thunk 易于使用，因为许多开发人员都熟悉 Promise，Sagas/Generators 功能更强大，但您需要学习它们。但是这两个中间件可以共存，所以你可以从 Thunks 开始，并在需要时引入 Sagas。


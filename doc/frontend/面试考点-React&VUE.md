## Vue和React的区别

1. **模版vsJSX**

React与Vue最大的不同是模板的编写。Vue鼓励你去写近似常规HTML的模板。写起来很接近标准HTML元素，只是多了一些属性。这些属性也可以被使用在单文件组件中，尽管它需要在在构建时将组件转换为合法的JavaScript和HTML。Vue鼓励你去使用HTML模板去进行渲染，使用相似于Angular风格的方法去输出动态的内容。因此，通过把原有的模板整合成新的Vue模板，Vue很容易提供旧的应用的升级。这也让新来者很容易适应它的语法。 

另一方面，React推荐你所有的模板通用JavaScript的语法扩展——JSX书写。React/JSX乍看之下，觉得非常啰嗦，但使用JavaScript而不是模板来开发，赋予了开发者许多编程能力。JSX只是JavaScript混合着XML语法，然而一旦你掌握了它，它使用起来会让你感到畅快。 而相反的观点是Vue的模板语法去除了往视图/组件中添加逻辑的诱惑，保持了关注点分离。 值得一提的是，与React一样，Vue在技术上也支持render函数和JSX，但只是不是默认的而已。

2. **状态管理 vs 对象属性**

如果你对React熟悉，你就会知道应用中的状态是（React）关键的概念。也有一些配套框架被设计为管理一个大的state对象，如Redux。此外，state对象在React应用中是不可变的，意味着它不能被直接改变（这也许不一定正确）。在React中你需要使用setState()方法去更新状态。在Vue中，state对象并不是必须的，数据由data属性在Vue对象中进行管理。而在Vue中，则不需要使用如setState()之类的方法去改变它的状态，在Vue对象中，data参数就是应用中数据的保存者。 对于管理大型应用中的状态这一话题而言，Vue.js的作者尤雨溪曾说过，（Vue的）解决方案适用于小型应用，但对于对于大型应用而言不太适合。

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

##### Object.defineP roperty和Proxy的区别

- Object.defineProperty对对象自身做修改, 而Proxy只是在Object基础上加一层拦截，不修改原对象
- 监听不了数组的变化
- 监听手段比较单一，只能监听set和get, Proxy有10几种监听
- 必须得把所有的属性全部添加defineProperty, Proxy对整个对象都会进行拦截
- Proxy是代理在`对象`级别的，defineProperty是代理到`静态的值`级别，所以Proxy的强大就在这里

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

### 虚拟DOM

所谓的Virtual DOM基本上说就是它名字的意思：虚拟DOM，DOM树的虚拟表现。它的诞生是基于这么一个概念：改变真实的DOM状态远比改变一个JavaScript对象的花销要大得多。 Virtual DOM是一个映射真实DOM的JavaScript对象，如果需要改变任何元素的状态，那么是先在Virtual DOM上进行改变，而不是直接改变真实的DOM。当有变化产生时，一个新的Virtual DOM对象会被创建并计算新旧Virtual DOM之间的差别。之后这些差别会应用在真实的DOM上。 

 当新一项被加进去这个JavaScript对象时，一个函数会计算新旧Virtual DOM之间的差异并反应在真实的DOM上。计**算差异的算法是高性能框架的秘密所在**，React和Vue在实现上有点不同。 Vue宣称可以更快地计算出Virtual DOM的差异，这是由于它在渲染过程中，会**跟踪每一个组件的依赖关系**，**不需要**重新渲染整个组件树。 而对于React而言，每当应用的状态被改变时，**全部子组件都会重新渲染**。当然，这可以通过shouldComponentUpdate这个生命周期方法来进行控制，但Vue将此视为默认的优化。 小结：如果你的应用中，交互复杂，需要处理大量的UI变化，那么使用Virtual DOM是一个好主意。如果你更新元素并不频繁，那么Virtual DOM并不一定适用，性能很可能还不如直接操控DOM。

### diff策略

![](https://www.hualigs.cn/image/6098cc90c6c3f.jpg)

#### **tree diff**

策略一：Web UI 中 DOM 节点跨层级的移动操作特别少。可以忽略不计。

对于策略一，React 对树的算法进行了简介明了的优化，即对树进行分层比较，两颗树只会对同一层级的节点进行比较。

既然 DOM 节点跨层级的移动，可以少到忽略不计，针对这种现象，React 通过 updateDepth 对 Virtual DOM 树进行层级控制，只对相同层级的DOM节点进行比较，即同一父节点下的所有子节点，当发现该节点已经不存在时，则该节点及其子节点会被完全删除掉，不会用于进一步的比较。这样只需要对树进行一次遍历，便能完成整个DOM树的比较。

由于 React 只会简单的考虑同层级节点的位置变换，对于不同层级的节点，只有创建和删除操作。当根节点R发现子节点中A消失了，就会直接销毁A；当D节点发现多了一个子节点A，就会创建新的A子节点（包括其子节点）。执行的操作为：

create A —> create B —> create C —> delete A

所以。当出现节点跨级移动时，并不会像想象中的那样执行移动操作，而是以 A 为根节点的整个树被整个重新创建，这是影响 React 性能的操作，所以 官方建议不要进行 DOM 节点跨层级的操作。

#### **component diff**

如果是同一类型的组件，按照原策略继续比较 Virtual DOM 树即可

如果不是，则将该组件判断为 dirty component，从而替换整个组件下的所有子节点

对于同一类型下的组件，有可能其 Virtual DOM 没有任何变化，如果能确切知道这一点，那么就可以节省大量的 diff 算法时间。因此， React 允许用户通过 shouldComponentUpdate()来判断该组件是否需要大量 diff 算法分析。不同类型的组件很少存在相似 DOM 树的情况，因此，这种极端因素很难在实际开发过程中造成重大影响。

#### **element diff**

INSERT_MARKUP（插入）：如果新的组件类型不在旧集合里，即全新的节点，需要对新节点执行插入操作。

MOVE_EXISTING （移动）：旧集合中有新组件类型，且 element 是可更新的类型，generatorComponentChildren 已调用 receiveComponent，这种情况下 prevChild=nextChild，就需要做移动操作，可以复用以前的 DOM 节点。

REMOVE_NODE （删除）：旧组件类型，在新集合里也有，但对应的 elememt 不同则不能直接复用和更新，需要执行删除操作，或者旧组件不在新集合里的，也需要执行删除操作。

React发现这样操作非常繁琐冗余，因为这些集合里含有相同的节点，只是节点位置发生了变化而已，却发生了繁琐的删除、创建操作，实际上只需要对这些节点进行简单的位置移动即可。

针对这一现象，React 提出了优化策略：

允许开发者对同一层级的同组子节点，添加唯一key进行区分，虽然只是小小的改动，但性能上却发生了翻天覆地的变化。

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
- 3. etch不支持abort，不支持超时控制，使用setTimeout 及 Promise.reject 的实现的超时控制并不能阻止请求过程继续在后台运行，造成了量的浪费
- 4. fetch没有办法原生检测请求的进度，而XHR可以


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

它使用虚拟DOM而不是真实DOM。
它使用服务器端渲染。
它遵循单向数据流或数据绑定

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


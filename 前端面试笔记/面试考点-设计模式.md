### **观察者模式和发布订阅模式的区别**

- 观察者模式里，只有两个角色 —— 观察者 + 被观察者
- 而发布订阅模式里，却不仅仅只有发布者和订阅者两个角色，还有一个经常被我们忽略的 —— 经纪人Broker

往更深层次讲：

- 观察者和被观察者，是松耦合的关系
- 发布者和订阅者，则完全不存在耦合

从使用层面上讲：

- 观察者模式，多用于单个应用内部
- 发布订阅模式，则更多的是一种跨应用的模式(cross-application pattern)，比如我们常用的消息中间件

### **发布订阅模式**

在“发布者-订阅者”模式中，称为发布者的消息发送者不会将消息编程为直接发送给称为订阅者的特定接受者，这意味着发布者和订阅者不知道彼此的存在。存在第三个组件，称为消息代理或调度中心，他由发布者和订阅者都知道，他过滤所有传入的消息并相应地分发他们。

![](https://upload-images.jianshu.io/upload_images/17538702-504f98d78a0882aa.png)

```javascript
//先有订阅，再有发布
class PubSub{
  constructor(){
    this.sub = {}; //订阅者名单
  }
  subscribe(type,handler){ //订阅
    /*
       @type : 订阅者标识
       @handler : 订阅者具体订阅内容 
    */
    if(!this.sub[type]){ // 如果该对象不存在订阅名单初始化一个数组
      this.sub[type] = [];
    }
    this.sub[type].push(handler); //如果该对象存在于订阅者名单中，就push进数组
  }
  publish(type,params){
    /*
       @type : 订阅者标识
       @params : 发布时的额外参数 
    */
    if(!(type in this.sub)){
      console.log(type+'该对象没有订阅')
      return;
    }
    this.sub[type].forEach((handler)=>{ //发布 遍历该对象下的所有订阅的事件 进行执行
      handler(params)
    })
  }
  removeSub(type,handler){
    if(!(type in this.sub)){
      console.log( type+'该对象没有订阅')
      return;
    }
    if(!handler){
      delete this.sub[type]
    }else{
      let idx = this.sub[type].findIndex((item)=>{
        return item == handler
      })
      if(idx<0){
        console.log(type+'没有订阅过此事件');
        return;
      }
      this.sub[type].splice(idx,1);
      if(this.sub[type].length<=0){
        delete this.sub[type]
      }
    }
  }
}

//Example:

function xiaoming(params){
  console.log('我是小明订阅的事件'+params)
}

function xiaohong1(params){
  console.log('我是小红订阅的事件1'+params)
}

function xiaohong2(params){
  console.log('我是小红订阅的事件2'+params)
}

let pubsub = new PubSub();
pubsub.subscribe('小明',xiaoming);

pubsub.subscribe('小红',xiaohong1);

pubsub.subscribe('小红',xiaohong2);

pubsub.removeSub('小红',xiaohong2)

pubsub.publish('小明','发布了');

pubsub.publish('小红','发布了');
```

### **观察者模式**

观察者模式指的是一个对象（Subject）维持一系列依赖于他的对象（Observer）,当有关状态发生变更时Subject对象通知一系列Observer对象进行更新。

![](https://upload-images.jianshu.io/upload_images/17538702-33c0ab6feaf76872.png)

```javascript
class Subject{
  constructor(){
    this.obList = [];  //observer 集合
    this.state = 1; //默认状态
  }
  getState(){  // 读取状态监听
    console.log('我被读取了')
    return this.state;
  }
  setState(value){  //重写状态监听
    console.log('我被重写了')
    this.state = value;
    this.notifyAllOber();  //重写时通知所有的观察者
  }
  attach(ob){  // 添加观察者
    this.obList.push(ob);
  }
  notifyAllOber(){
    this.obList.forEach((ob)=>{  //核心 通知所有观察者进行更新
      ob.update();
    })
  }
};

class Observer{
  constructor(name,subject){
    this.name = name;
    this.subject = subject;
    this.subject.attach(this); // 添加观察者
  }
  update(){
    console.log(this.name,'我观察到了变化')
  }
} 

let subject = new Subject();

let observer = new Observer('我是张三',subject);

subject.setState(2)
```

### 单例模式

单例模式是指保证一个类仅有一个实例，并提供一个访问它的全局节点。实现方式是先判断实例是否存在，如果存在则直接返回，如果不存在就创建之后再返回，这就确保了一个类只有一个实例对象。

```javascript
class Num{
    constructor(num){
        this.num = num;

    }
    getNum(){
        return this.num;
    }
}

let createInstance = (function(){
    var instance = null;
    return function(num){
        if(!instance){
            instance = new Num(num)
        }
        return instance;
    }
})();

var num1 = createInstance(10);
var num2 = createInstance(20);

console.log(num1===num2) //true
```

### 策略模式

策略模式指的是定义一系列的算法，把他们一个个封装起来，并且使他们可以相互替换。

一个基于策略模式的程序至少有两部分组成，一部分是策略类(可变)，它封装了具体算法，并负责整个具体计算过程，另一部分是环境类（不变），环境类接收客户的请求，随后将请求委托给某一个策略类。也就是说在环境类中维持对某个策略的引用。

比如财务部门算年终奖，不同的身份有不同的奖金额度。
 举例：CEO : 10个月奖金，CTO：5个月奖金，CFO：4个月奖金，COO：2个月奖金。
 如果使用正常的逻辑去做那就是使用if条件判断职位类型。使用策略模式可以参考一下代码

```javascript
//策略类
var strategies = {
    "CEO":function(bonus){ //首席执行官
        return bonus*10
    },
    "CTO":function(bonus){ //首席技术官
        return bonus*5
    },
    "CFO":function(bonus){ //首席财务官
        return bonus*4
    },
    "COO":function(bonus){ //首席运营官
        return bonus*2
    }
}

var calculateBonus = function(position,bonus){
    return strategies[position](bonus);
}
//环境类
var ceobonus = calculateBonus('CEO',50);
var ctobonus = calculateBonus('CTO',30);

console.log(ceobonus,ctobonus);//500 150
```


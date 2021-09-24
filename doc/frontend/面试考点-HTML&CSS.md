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

// 决定主轴的方向（即项目的排列方向）。
flex-direction:row（默认值） | row-reverse | column |column-reverse

// 默认情况下，项目都排在一条线（又称"轴线"）上。flex-wrap属性定义，如果一条轴线排不下，如何换行。
flex-wrap:nowrap（默认值） | wrap | wrap-reverse

// 定义了项目在主轴上的对齐方式。
justify-content:flex-start（默认值） | flex-end | center |space-between(两端对齐，项目之间的间隔都相等。) | space-around (每个项目两侧的间隔相等。所以，项目之间的间隔比项目与边框的间隔大一倍)| space-evenly

// 定义项目在交叉轴上如何对齐。
align-items:stretch（默认值） | center  | flex-end | baseline | flex-start

// 定义了多根轴线的对齐方式。如果项目只有一根轴线，该属性不起作用
align-content:stretch（默认值） | flex-start | center |flex-end | space-between | space-around | space-evenly
```

#### 设置项目的属性有：

```css
// 定义项目的排列顺序。数值越小，排列越靠前，默认为0。
order:0（默认值） | <integer>

// 定义项目的放大比例，默认为0，即如果存在剩余空间，也不放大。
flex-shrink:1（默认值） | <number>

// 定义了项目的缩小比例，默认为1，即如果空间不足，该项目将缩小。
flex-grow:0（默认值） | <number>

// 定义了在分配多余空间之前，项目占据的主轴空间（main size）。浏览器根据这个属性，计算主轴是否有多余空间。它的默认值为auto，即项目的本来大小。有width默认width，都有basis>width
flex-basis:auto（默认值） | <length>

flex:none | auto | @flex-grow @flex-shrink @flex-basis
  
// 允许单个项目有与其他项目不一样的对齐方式，可覆盖align-items属性。默认值为auto，表示继承父元素的align-items属性，如果没有父元素，则等同于stretch。
align-self:auto（默认值） | flex-start | flex-end |center | baseline| stretch
```

#### flex-shink的计算方式

有width默认width，都有basis>width

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



|   单值语法    |     等同于     |     备注     |
| :-----------: | :------------: | :----------: |
| flex: initial | flex: 0 1 auto | 初始值，常用 |
|    flex: 0    |  flex: 0 1 0%  |  适用场景少  |
|  flex: none   | flex: 0 0 auto |     推荐     |
|    flex: 1    |  flex: 1 1 0%  |     推荐     |
|  flex: auto   | flex: 1 1 auto |  适用场景少  |

### 

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

### z-index影响下的层级布局

#### 定义：

z-index 属性设置元素的堆叠顺序。拥有更高堆叠顺序的元素总是会处于堆叠顺序较低的元素的前面。（仅在节点的 position 属性为 relative, absolute 或者 fixed 时生效。）

该属性设置一个定位元素沿 z 轴的位置，z 轴定义为垂直延伸到显示区的轴。如果为正数，则离用户更近，为负数则表示离用户更远。

#### 基本规则：

1. 不设置position 属性, 但为节点加上 z-index 属性. 发现 z-index 对节点没起作用。
2. 设置了position属性（值不为static），对应的节点将脱离文本流，漂浮（覆盖）在其他节点上边。所以设置position属性能提升节点的显示等级。
3. 如果所有节点都定义了相同的position，例如absolute。但没有设置z-index属性，节点将按出现的顺序依次覆盖前面节点重叠的部分，遵循“后来居上”原则。
4. z-index 为 0 的节点与没有定义 z-index 在同一层级内没有高低之分; 但 z-index 大于等于 1 的节点会遮盖没有定义 z-index 的节点; z-index 的值为负数的节点将被没有定义 z-index 的节点覆盖。
5. 子元素永远覆盖在父元素的上方。

#### 从父规则：

若是父节点都设置了一样的z-index值，则无论子节点设置何值，都只依照父节点的z-index值，根据上边所述的“基本规则”，完成节点之间的叠放。

#### 默认值规则：

若只是设置了position属性，而未设置z-index属性的话:

* IE6、7以及IE8的混杂模式下，z-index的默认值是0，表示对应的节点将要加入到布局定位的层级元素的比较中去。
* 在其他浏览器中的默认值为auto，不参与到层级元素的比较，其设置了z-index的子节点直接跳过“从父规则”加入到层级元素的比较。
  

### 媒体查询

```css
@media *mediatype* and|not|only *(media feature)* {*
  CSS-Code;
*}
```

举例

如果文档宽度小于 300 像素则修改背景颜色(background-color):

```css
@media screen and (max-width: 300px) {
  body {
    background-color:lightblue;
  }
}
```


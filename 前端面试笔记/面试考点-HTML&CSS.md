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

flex-direction:row（默认值） | row-reverse | column |column-reverse

flex-wrap:nowrap（默认值） | wrap | wrap-reverse

justify-content:flex-start（默认值） | flex-end | center |space-between | space-around | space-evenly

align-items:stretch（默认值） | center  | flex-end | baseline | flex-start

align-content:stretch（默认值） | flex-start | center |flex-end | space-between | space-around | space-evenly
```

#### 设置项目的属性有：

```css
order:0（默认值） | <integer>

flex-shrink:1（默认值） | <number>

flex-grow:0（默认值） | <number>

flex-basis:auto（默认值） | <length>

flex:none | auto | @flex-grow @flex-shrink @flex-basis

align-self:auto（默认值） | flex-start | flex-end |center | baseline| stretch
```

#### flex-shink的计算方式

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



### 实现盒子垂直居中的方式

#### 1. 固定宽高垂直居中

![](https://www.haorooms.com/uploads/images/fix_centered.gif)

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
        <div class="center"></div>
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

|   单值语法    |     等同于     |     备注     |
| :-----------: | :------------: | :----------: |
| flex: initial | flex: 0 1 auto | 初始值，常用 |
|    flex: 0    |  flex: 0 1 0%  |  适用场景少  |
|  flex: none   | flex: 0 0 auto |     推荐     |
|    flex: 1    |  flex: 1 1 0%  |     推荐     |
|  flex: auto   | flex: 1 1 auto |  适用场景少  |


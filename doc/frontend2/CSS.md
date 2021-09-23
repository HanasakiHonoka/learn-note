### 盒模型
标准盒模型： width = content;

怪异盒模型： width = padding+content;

`box-sizing: content-box | border-box`

#### javascript如何设置获取盒模型对应的宽和高
`dom.style.width/height` 只能取到行内样式的宽和高，style标签中和link外链的样式取不到。

`dom.currentStyle.width/height` 取到的是最终渲染后的宽和高，只有IE支持此属性。

`window.getComputedStyle(dom).width/height` 同（2）但是多浏览器支持，IE9以上支持。

`dom.getBoundingClientRect().width/height` 也是得到渲染后的宽和高，大多浏览器支持。IE9以上支持，除此外还可以取到相对于视窗的上下左右的距离


### rem px em区别
px：固定像素

em：是相对长度单位。相对于当前对象内文本的字体尺寸。如当前对行内文本的字体尺寸未被人为设置，则相对于浏览器的默认字体尺寸

rem: 相对的只是HTML根元素


### 实现无缝无限动画

```css
.box1{
    position: absolute;
    background-color: blue;
}
.box2{
    left: -100px;
    background-color: red;
}
.box{
    width: 100px;
    height: 50px;
    position: absolute;
}
.content{
    animation: move 2s infinite linear;
}
.bb{
    width: 100px;
    height: 50px;
    position: relative;
    overflow: hidden;
    border: 1px solid black; 
}
@keyframes move {
    from{
        transform:translateX(0);
    }
    to{
        transform:translateX(200px);
    }
}
```
```javascript
<div class="bb">
    <div class="content">
        <div class="box box1"></div>
        <div class="box box2"></div>
        <div class="box box1" style="left:-200px"></div>
    </div>

</div>
```

### 块级标签 行级标签 行内块级标签

块级：`div h1-h6 p ul ol `

1.独占一行，不和其他元素待在同一行
2.能设置宽高

行级：`<a> <span> <em> <i>`

1.能和其他元素待在同一行
2.不能设置宽高

行内块级：`<img> <input> <textarea>`


### 几种选择器
- 类选择器 .id
- id选择器 #id
- 属性选择器 .id[attr=]
- 元素选择器 p
- 伪类选择器
  `a:hover ` `first-child` `:focus`
  
  `<a>`访问情况：`:link （未访问）— :visited （已访问过）— :hover（悬停） — :active （激活：点击和释放间）`

    `text-decoration:none` 可去掉下划线
```css
body article p     后代元素
 article > p     直接子元素
 p + img     邻接兄弟
 p ~ img   通用兄弟
 ul > li[class="a"]  {  }    使用关系选择器
```
### 层叠优先级
1. 后面的覆盖前面
2. 权重: 内联1000, id100, 类/伪类/属性10, 伪元素/元素1、
   伪元素 `::before` `::after` `::first-line`
3. `!important`
 
### css 优化
1. 合并css文件，如果页面加载10个css文件,每个文件1k，那么也要比只加载一个100k的css文件慢。
2. 减少css嵌套，最好不要嵌套三层以上。
3. 不要在ID选择器前面进行嵌套，ID本来就是唯一的而且权限值大，嵌套完全是浪费性能。
4. 建立公共样式类，把相同样式提取出来作为公共类使用。
5. 减少通配符*或者类似[hidden="true"]这类选择器的使用，挨个查找所有...这性能能好吗？
6. 巧妙运用css的继承机制，如果父节点定义了，子节点就无需定义。
7. 拆分出公共css文件，对于比较大的项目可以将大部分页面的公共结构样式提取出来放到单独css文件里，这样一次下载 后就放到缓存里，当然这种做法会增加请求，具体做法应以实际情况而定。
8. 不用css表达式，表达式只是让你的代码显得更加酷炫，但是对性能的浪费可能是超乎你想象的。
9. 少用css rest，可能会觉得重置样式是规范，但是其实其中有很多操作是不必要不友好的，有需求有兴趣，可以选择normolize.css。
10. cssSprite，合成所有icon图片，用宽高加上background-position的背景图方式显现icon图，这样很实用，减少了http请求。
11. 善后工作，css压缩(在线压缩工具 YUI Compressor)
12. GZIP压缩，是一种流行的文件压缩算法。

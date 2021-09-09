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
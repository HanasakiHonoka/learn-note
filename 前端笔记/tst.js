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
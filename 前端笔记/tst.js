function printNum(n,m,q,joinArr,swapArr) {
    let map = new Map();
    for(let i = 0; i < m; i++) {
      if(map.has(joinArr[i][0])) {
        map.set(joinArr[i][0],[...map.get(joinArr[i][0]), joinArr[i][1]]);
      } else {
        map.set(joinArr[i][0],[joinArr[i][1]]);
      }
    }
    console.log(map.get(1), ' 111111111111')
    let result = [];
    for(let i = 1; i <= n; i++) {
        console.log(map.get(1), ' 111111111111')
      let length = map.get(i).length;
      result.push(length);
    }
    return result;
  }
  let res = printNum(5,5,3,[[1,2],[2,3],[3,4],[4,5],[1,4]],[[2,5],[1,2],[3,4]])
  console.log(res);
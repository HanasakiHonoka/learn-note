
x = [[11,55,23,77],[41,55,21,44],[111,5,43,62],[44,33,49,63],[33,42,92,33]]


x.sort((a,b)=>{
   if(a[0]==b[0]){
      if(a[1]==b[1]){
         if(a[2]==b[2]){
            return  a[3]-b[3]
         }
         return a[2]-b[2];
      }
      return a[1]-b[1];
   }
   return a[0]-b[0];
})

console.log(x)
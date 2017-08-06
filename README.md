# GPSHook
利用Xposed技术hook系统方法 改变GPS定位 实现Android模拟定位
我们公司大神帮忙修改的东西，参考文章:http://www.jianshu.com/p/91e312faa6c3 ，http://www.jianshu.com/p/796e94d8af31, 欢迎issues.



没有xposed的基础的，可以学习一下简单的。

/////////////////
package  com.test;

public class MainActivity extends Activity{
.......
.....
public  void t(String a , int b ){
....................
}


}

/////////////////////////////////////////////////

.......................

//hook方法

.......................

findAndHookMethod("com.test.MainActivity", laparam.classLoader , "t" , String.class , int.class , new XC_...
  
  
  afterfindAndHook(....){
  .........
  ........
  }


);
<image src="./pay.png">

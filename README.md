# JavaAgent
use javaagent modify jvm class

from jdk5, jdk import java.lang.instrument, we can modify class file throw running premain method when jvm load class.

* instrumentation
  * 静态instrumentation
    * 通过添加agent代理，可以对jvm加载前的class文件动态修改
  * 动态instrumentation
    * 通过Attach API，与jvm启动后的进程通信，进而同台修改class文件


## demo1:

使用静态instrumentation， 在jvm启动时添加一个代理(javaagent)，每个代理是一个jar包，代理类包含一个premain方法，premain方法可以对加载前的class文件进行修改，无需对原有应用做任何修改，就可以实现类的动态修改和增强
```
instrumentation.addTransformer()

java -javaagent:agent.jar MyClass
```

>demo2:

使用动态instrumentation，在jvm启动后通过Attach API远程加载和使用，实现对class文件的修改
1. 编写Attach Agent(打包成agent.jar)(如方法一)
    ```
    instrumentation.retransformClasses()
    ```
2. 使用VirtualMachine.attach()方法和目标jvm进程跨进程通信
    ```
    VirtualMachine vm = VirtualMachine.attach(args[0]);
    vm.loadAgent("F:\\Project\\java\\javaagent\\target\\my-javaagent.jar");
    vm.detach();
    ```
3. 启动目标jvm进程
    ```
    java MyClass
    ```
4. attach到目标进程
    ```
    java MyAttachMain pid(pid为目标进程id)
    ```

>demo3

使用javaagent破解Censum

# 异常

## 异常的分类

<img src="_images/1548572803236.png" />

Throwable类是所有异常的根。所有的Java异常类都直接或者间接地继承自Throwable。可以通过继承Exception或者Exception的子类来创建自己的异常类。

- **Error**：**是程序无法处理的错误**，属于系统内部错误，由Java虚拟机抛出。很少发生。大多数错误与代码编写者执行的操作无关，而表示代码运行时 JVM（Java 虚拟机）出现的问题。例如，Java虚拟机运行错误（Virtual MachineError），当 JVM 不再有继续执行操作所需的内存资源时，将出现 OutOfMemoryError。这些异常发生时，Java虚拟机（JVM）一般会选择线程终止。

  这些错误表示故障发生于虚拟机自身、或者发生在虚拟机试图执行应用时，如Java虚拟机运行错误（Virtual MachineError）、类定义错误（NoClassDefFoundError）等。这些错误是不可查的，因为它们在应用程序的控制和处理能力之 外，而且绝大多数是程序运行时不允许出现的状况。对于设计合理的应用程序来说，即使确实发生了错误，本质上也不应该试图去处理它所引起的异常状况。在 Java中，错误通过Error的子类描述。

- **Exception**：**是程序本身可以处理的异常**。由程序和外部环境所引起的错误，能被程序捕获和处理。

- **RuntimeException**：虽然他也是Exception的子类，但它一般是由于程序设计错误才会出现的异常，比如类型转换错误，访问一个越界数组或数值错误等。这在**程序运行时才会有JVM抛出发生异常**。

- Error和RuntimeException以及他们的子类都称为**免检异常**，所有其它的异常都称为**必检异常**。必检异常就是指编译器会强制程序员检查并通过try-catch块处理他们，或者在方法头进行声明（抛出）。

### Throwable类常用方法

- **public string getMessage()**:返回异常发生时的详细信息
- **public string toString()**:返回异常发生时的简要描述
- **public string getLocalizedMessage()**:返回异常对象的本地化信息。使用Throwable的子类覆盖这个方法，可以声称本地化信息。如果子类没有覆盖该方法，则该方法返回的信息与getMessage（）返回的结果相同
- **public void printStackTrace()**:在控制台上打印Throwable对象封装的异常信息

## 异常的处理

### 声明异常

> 每个方法都必须声明它可能抛出的必检异常的类型，这称为声明异常。方法的调用者会被告知有异常，需要捕获或声明。

使用`throws`关键字进行异常的声明，例如：

```java
public void myMethod() throws IOException
```

如果要在一个方法上声明多个异常，用逗号（,）隔开即可。

*注意：*如果方法没有在父类中声明异常，那么就不能在子类中对其进行继承来声明异常。

### 抛出异常

> 检测到错误的程序可以创建一个合适的异常类型的实例并抛出它，这就抛出一个异常。

使用`throw`关键字进行异常的抛出，例如：

```java
throw new IllegalAraumentException("这是异常信息");
```

### 捕获异常

> 当抛出一个异常时，可以在try-catch块中捕获和处理它。

例如：

Java 7之前的版本：

```java
try{
    //do something  and throw exception
}catch(Exception1 ex){
    //handler for exception1
}catch(Exception2 ex){
    //handler for exception2
}
```

Java 7中，我们可以用一个catch块捕获这些所有的异常：

```java
catch(IOException | SQLException | Exception ex){
     logger.error(ex);
     throw new MyException(ex.getMessage());
}
```

如果用一个catch块处理多个异常，可以用管道符（|）将它们分开，在这种情况下异常参数变量（ex）是定义为final的，所以不能被修改。这一特性将生成更少的字节码并减少代码冗余。

但是不管是用多个catch还是一个catch捕获异常，后者都是前者的父异常，比如上面的例子中`Exception`是`SQLException`的父类，所以`Exception`应当在后面。这也很好理解，如果将父类（比如：`Exception`）异常放在前面，当产生异常时，就直接捕获了父类的异常，而不会捕获子异常(比如`SQLException`)。

如果try块中的某条语句抛出一个异常，Java就会跳过try块中剩余的语句，然后开始逐个查找处理这个异常的代码的过程（判断在catch块中的异常类实例是否是该异常对象的类型）。处理这个异常的代码称为异常处理器。

### finally子句

> 无论异常是否产生，finally子句总是会被执行的。

不论异常是否出现或者是否被捕获，都希望执行某些代码。Java有一个finally子句，可以用来达到这个目的。语法如下：

```java
try{
    //do something  and throw exception
}catch(Exception1 ex){
    //handler for exception1
}catch(Exception2 ex){
    //handler for exception2
}finally{
    //always run
}
```

在任何情况下，finally块中的代码都会执行。

*注意：*使用finally子句时可以省略掉catch块。

### 异常处理总结

- try 块：用于捕获异常。其后可接零个或多个catch块，如果没有catch块，则必须跟一个finally块。
- catch 块：用于处理try捕获到的异常。
- finally 块：无论是否捕获或处理异常，finally块里的语句都会被执行。**当在try块或catch块中遇到return语句时，finally语句块将在方法返回之前被执行。**

**在以下4种特殊情况下，finally块不会被执行：**

1. 在finally语句块中发生了异常。
2. 在前面的代码中用了System.exit()退出程序，exit是带参函数 ；若该语句在异常语句之后，finally会执行。
3. 程序所在的线程死亡。
4. 关闭CPU。

**关于返回值：**

如果try语句里有return，返回的是try语句块中变量值。  详细执行过程如下：

1. 如果有返回值，就把返回值保存到局部变量中；
2. 执行jsr(Jump to SubRoutine)指令跳到finally语句里执行；
3. 执行完finally语句后，返回之前保存在局部变量表里的值。
4. 如果try，finally语句里均有return，忽略try的return，而使用finally的return。
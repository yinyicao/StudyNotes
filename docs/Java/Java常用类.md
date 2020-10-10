# Java常用类

## BigInteger和BigDecimal类

> BigInteger和BigDecimal类可以用于表示任意大小和精度的整数或者十进制数。他们位于`java.math`包中。

使用`new BigInteger();`和`new BigDecimal();`创建BigInteger和BigDecimal的实例。

使用`add`,`substract`,`multipe`,`divide`,`remainder`方法完成算术运算（加、减、乘、除、取余），例如下面是一个取余的例子：

```java
BigInteger num1 = new BigInteger("123123");
BigInteger num2 = new BigInteger("10");
BigInteger result = num1.remainder(num2);//123123 % 10
System.out.println(result);
```

## String 、StringBuffer和StringBuilder

### 可变性

String 类中使用 final 关键字字符数组保存字符串，`private　final　char　value[]`，所以 String 对象是**不可变**的。如下，源码中String类的定义：

```java
public final class String
	implements java.io.Serializable, Comparable<String>, CharSequence {
		/** The value is used for character storage. */
    	private final char value[];
    	...
    	...
    
}
```

StringBuilder 与 StringBuffer 都继承自 AbstractStringBuilder 类，在 AbstractStringBuilder 中也是使用字符数组保存字符串`char[]value` 但是没有用 final 关键字修饰，所以这两种对象都是**可变**的。

### 线程安全性

**String** 中的对象是不可变的，也就可以理解为常量，**线程安全**。AbstractStringBuilder 是 StringBuilder 与 StringBuffer 的公共父类，定义了一些字符串的基本操作，如 expandCapacity、append、insert、indexOf 等公共方法。**StringBuffer** 对方法加了同步锁或者对调用的方法加了同步锁（在源码中可以看到每个方法都加了`synchronized`关键字），所以是**线程安全**的。**StringBuilder** 并没有对方法进行加同步锁，所以是**非线程安全**的。 　　

### 性能

每次对 String 类型进行改变的时候，都会生成一个新的 String 对象，然后将指针指向新的 String 对象。

StringBuffer 每次都会对 StringBuffer 对象本身进行操作，而不是生成新的对象并改变对象引用。

相同情况下使用 StringBuilder 相比使用 StringBuffer 仅能获得 10%~15% 左右的性能提升，但却要冒多线程不安全的风险。

如果是多任务并发访问，使用StringBuffer；如果是单任务访问，使用StringBuilder会更有效。
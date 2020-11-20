<!-- TOC -->

- [Lambda](#lambda)
  - [Lambda的使用条件](#lambda的使用条件)
  - [(参数) ->单行语句](#参数-gt单行语句)
  - [(参数) ->{多行语句}](#参数-gt多行语句)
  - [(参数) ->表达式](#参数-gt表达式)
- [方法引用](#方法引用)
  - [引用静态方法](#引用静态方法)
  - [引用某个对象的实例的普通方法](#引用某个对象的实例的普通方法)
  - [引用某个类型的任意对象的实例的普通方法](#引用某个类型的任意对象的实例的普通方法)
  - [引用构造方法](#引用构造方法)
- [Date/Time API](#datetime-api)
  - [Java8之前](#java8之前)
  - [LocalDate](#localdate)
  - [LocalTime](#localtime)
  - [LocalDateTime](#localdatetime)
  - [JDBC与Java8中的Date/Time API关联问题](#jdbc与java8中的datetime-api关联问题)
- [Stream](#stream)
  - [中间操作符](#中间操作符)
  - [终止操作符](#终止操作符)
  - [map](#map)
  - [filter](#filter)
  - [sorted](#sorted)
  - [collect](#collect)
  - [skip和limit](#skip和limit)
  - [noneMatch](#nonematch)
- [Optional](#optional)
  - [初始化](#初始化)
  - [isPresent](#ispresent)
  - [get](#get)
  - [orElse](#orelse)
  - [orElseGet](#orelseget)
  - [orElseThrow<span style='color:red'>(常用)</span>](#orelsethrow常用)
  - [map](#map-1)
  - [flatMap](#flatmap)
  - [filter](#filter-1)
  - [<span style='color:red'>Optional正确使用</span>](#optional正确使用)

<!-- /TOC -->

> JAVA8官方文档： https://docs.oracle.com/javase/8/docs/api/

Java8中新增了许多新的特性，比如，Lambda，方法引用（一般配合Lambda使用），Date/Time API，Stream，Optional，Base64加密，Interface支持默认方法和静态方法，另外还新增了一些新引擎工具，新增了对JavaScript的解释器-Nashorn等等。

## Lambda

> Lamda表达式，读作λ表达式，它实质属于函数式编程的概念，要理解函数式编程的产生目的，就要先理解匿名内部类。

先来看看传统的匿名内部类调用方式：

```java
interface MyInterface{
    void method();
}
public class Main {
    public static void test(MyInterface myInterface){
        myInterface.method();
    }
    public static void main(String[] args) {
        test(new MyInterface() {
            @Override
            public void method {
                System.out.println("Hello World!");
            }
        });
    }
}
```

在主类中的这么几行代码，嵌套几层就为了输出一个Hello World！是不是很麻烦？但是由于java结构的完整性，我们还不得不那么做，现在JDK1.8来了。

再来看看使用Lamda表达式改写上面的代码：

```java
interface MyInterface{
    void method();
}
public class Main {
    public static void test(MyInterface myInterface){
        myInterface.method();
    }
    public static void main(String[] args) {
        test(()->System.out.println("Hello World!"));
    }
}
```

这就是Lamda表达式语言，为了解决匿名内部类繁杂的操作而出现。

Lamda语法有三种形式：

  * **(参数) - >单行语句；**
  * **(参数) - >{多行语句}；**
  * **(参数) - >表达式；**

括号**（）**可以大致理解为就是方法，里面是参数变量，在上面的例子中`()->System.out.println("Hello World!")`
前面的**()**代表**void method()**方法，它没有入参，所以为空， **->**后面是一个单行语句。

接口方法如果有参数，也需要写参数。只有一个参数时，括号可以省略。

如果**->**后面是多行语句，需要用**{ }**装起来，每条语句后需要有分号，同时需要注意该Lamdba接口是否存在返回值，有则使用return返回值，否则无法通过编译 ;

**->**后面也可以是一个表达式，如：a+b等。

### Lambda的使用条件

并非任何接口都支持lambda表达式，它只支持在函数式接口下使用。所谓函数式接口就是只有一个抽象方法的接口，也称为SAM接口，即Single Abstract Method interfaces。通常如果要保证一个接口是函数式接口，就要在接口上加以限制，使用`@FunctionalInterface`注解（主要用于编译级错误检查，加上该注解，当你写的接口不符合函数式接口定义的时候，编译器会报错。  加不加`@FunctionalInterface`对于接口是不是函数式接口没有影响，该注解只是提醒编译器去检查该接口是否仅包含一个抽象方法 ），如下：

```java
@FunctionalInterface   //此为函数式接口，只能够定义一个抽象方法
interface MyInterface2<R>{
    public R upper();
}
```

另外， Java8 util.function 包下自带了43个函数式接口，具体可看文章头部官方API，或者这篇文章： https://www.jianshu.com/p/2338cabc59e1

### (参数) ->单行语句

```java
interface MyInterface{
    void method(String str);
}
public class Main {
    public static void test(MyInterface myInterface){
        myInterface.method("Hello World!");//设置参数内容
    }
    public static void main(String[] args) {
        //首先在()中定义此表达式里面需要接收变量s，后面的单行语句中就可以使用该变量了
        test((s)->System.out.println(s));
    }
}
```

### (参数) ->{多行语句}

```java
interface MyInterface{
    void method(String str);
}
public class Main {
    public static void test(MyInterface myInterface){
        myInterface.method("Hello World!");//设置参数内容
    }
    public static void main(String[] args) {
     //首先在()中定义此表达式里面需要接收变量s，后面的多行语句中就可以使用该变量了。注意：多行语句别少"；"号
        test((s)->{
            s=s+s;
            System.out.println(s);
        });
    }
}
```

### (参数) ->表达式

```java
interface MyInterface{
    int method(int a,int b);
}
public class Main {
    public static void test(MyInterface myInterface){
        int result=myInterface.method(1,2);//设置参数内容,接收返回参数
        System.out.println(result);
    }
    public static void main(String[] args) {
        test((x,y)-> x*y );//调用方法
        // 相当于
        // test((x,y)-> {return  x*y;});
    }
}
```

这样，Lamda表达式就看起来很简单了，有不有！

匿名内部类，我们比较常用的地方在哪儿？线程类Thread，以前我们可能这样写：

```java
new Thread(new Runnable() {  
    @Override  
    public void run() {  
        System.out.println("线程操作！");  
    }  
});
```

现在，使用Lamda表达式，简单写为：

```java
new Thread(()->System.out.println("线程操作！"));
```

**小结：利用Lamda表达式是为了避免匿名内部类定义过多无用的操作。**

## 方法引用

> 在Lamda新特性的支持下，JAVA8中可以使用lamda表达式来匿名实现抽象方法。  若Lambda体中的功能，已经有方法提供了实现，可以使用方法引用。可以将方法引用理解为Lambda表达式的另一种表现形式。

**方法引用的四种形式：**

- 引用静态方法-->类名称::static 方法名称；
- 引用某个对象的实例的普通方法-->示例化对象::普通方法；
- 引用某个类型的任意对象的实例的普通方法-->特定类::普通方法；
- 引用构造方法-->类名称::new

### 引用静态方法

如：String类中的valueOf()方法：`public static String valueOf(int x);` **在下面的例子中以三种方式（匿名内部类、lambda表达式和方法引用）实现接口中的function()方法，而function()的具体实现逻辑是调用String.valueOf()。**来体验一下方法引用的魅力。

```java
 /**
  * 实现方法的引用接口
  * @param <P>引用方法的参数类型
  * @param <R>引用方法的返回类型
  */
 interface MyInterface<P,R>{
     public R function(P p);//和String.valueOf(int x)类似
 }
 
interface MyInterface1{
    String function(Integer a);
}

public class Main {

    public static void test(MyInterface1 myInterface1){
        String result = myInterface1.function(2000);
        System.out.println(result+"  --");
    }

    public static void main(String[] args) {

        //1.匿名内部类实现
        test(new MyInterface1() {
            @Override
            public String function(Integer a) {
                return String.valueOf(a);
            }
        });

        //2.Lamda表达式实现
        test((a)->String.valueOf(a));

        //3.方法引用实现:引用类的静态方法
        MyInterface<Integer,String> msg = String::valueOf;
        String str = msg.function(2000);
        System.out.println(str);
    }
}
```

### 引用某个对象的实例的普通方法

如：String类中的toUpperCase()方法：`public String toUpperCase();`**在下面的例子中以方法引用的方式实现接口中的upper()方法，而upper()的具体实现逻辑是调用String实例的toUpperCase()。**

这个方法没有参数，但是有返回值，并且这个方法一定要在有实例化对象的时候才可以调用。


```java
 interface MyInterface2<R>{
     public R upper();
 }
 public class Main1 {
     public static void main(String[] args) {
         String str = new String("hello");
         MyInterface2<String> msg = str::toUpperCase;
         System.out.println(msg.upper());//调用upper方法，就相当于调用toUpperCase方法
     }
}
```

在上面的演示中已经发现，如果要实现方法的引用，就必须要有接口，并且这个接口中只能存在一个方法，否则方法是无法进行引用的。

所以为了保证被引用的接口里面只能够定义一个方法，就要在接口上加以限制，使用@FunctionalInterface 注解。


```java
@FunctionalInterface   //此为函数式接口，只能够定义一个方法
interface MyInterface2<R>{
    public R upper();
}
```

### 引用某个类型的任意对象的实例的普通方法

比如：String类中的compareTo(String str1,String str2)方法 `public int compareTo(String
anotherString);`

如果要进行比较的话，比较的形式：字符串1对象.compareTo(字符串2对象)，要准备两个参数。


```java
 @FunctionalInterface   //此为函数式接口，只能够定义一个方法
 interface MyInterface3<P>{
     public int compare(P p1,P p2);
 }
 public class Main2 {
     public static void main(String[] args) {
         MyInterface3<String> msg = String::compareTo;
         System.out.println(msg.compare("A","B"));
     }
}
```

与之前相比，方法引用前不需要再定义对象，而是可以理解为将对象定义在了参数中。

### 引用构造方法


```java
 @FunctionalInterface   //此为函数式接口，只能够定义一个方法
 interface MyInterface4<C>{
     public C person(String n,int a);
 }
 class Person{
     private String name;
     private int age;
 
     public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
    @Override
    public String toString() {
        return  "姓名："+this.name+",年龄："+this.age;
    }
}
public class Main3 {
    public static void main(String[] args) {
        MyInterface4<Person> msg = Person::new;//引用构造方法
        Person person = msg.person("小明",20);
        System.out.println(person);
    }
}
```
## Date/Time API

>  Java8 Date/Time API相关的类：LocalDate，LocalTime，LocalDateTime，这三个类有一个公共特点就是同String类一样，都是密封类（final修饰），不允许继承（都位于java.time包下） 

### Java8之前

- 使用new Date()获取月时，从0开始，每次都要+1。获取本月最后一天，要分情况判断28，29，30，31。非常麻烦，代码量非常繁琐。现使用Java8中的Date/Time API，问题都解决了！

- 常用`SimpleDateFormat dateformat = new SimpleDateFormat(new Date());`的方式来格式化时间，程序中出现大量的SimpleDateFormat类，且线程不安全。《阿里巴巴-JAVA开发手册》中提到：如果是 JDK8 的应用，可以使用 Instant 代替 Date，LocalDateTime 代替 Calendar， DateTimeFormatter 代替 SimpleDateFormat，官方给出的解释：simple beautiful strong immutable thread-safe。

### LocalDate

**类方法：**

```
1、LocalDate nowDate = LocalDate.now();  获取当前日期：2019-03-21

2、LocalDate formatDate = LocalDate.parse("2018-08-08");  自定义时间：2018-08-08
```

**实例方法：**

```
1、nowDate.getDayOfMonth()  ：21  （获取当天所属本月中的第几天）

2、nowDate.getDayOfYear()  ：80  （获取当天所属本年中的第几天）

3、nowDate.getDayOfWeek()  ：THURSDAY （获取当天所属本周的周几）

nowDate.getDayOfWeek() .getValue() ：4

4、nowDate.getMonth()  ：MARCH  （获取当月所属本年的第几月，与new Date() 相比它是从1开始，抛弃了之前的从0开始）

nowDate.getMonth().getVlue()：3
nowDate.getMonthVlue() ：3

5、nowDate.getYear()  ：2019  （获取年）

6、nowDate.getEra() ：CE  (获取次日期适用的时代)

7、nowDate.with(TemporalAdjusters.firstDayOfMonth()) ：2019-03-01（获取本月第一天）

8、nowDate.withDayOfMonth(2) ：2019-03-02（获取本月第二天）

9、nowDate.with(TemporalAdjusters.lastDayOfMonth()) ：2019-03-01（获取本月最后一天，无须再判断28、29、30、31）

10、nowDate.plusDays(1) ：2019-03-22（获取下一天日期）
```

<span style='color:red'>注：TemporalAdjusters类中有许多常用的特殊的日期的方法（类方法），使用时可以仔细查看，可以很大程度减少日期判断的代码量！</span>

### LocalTime

**类方法：**

```
LocalTime nowTime = LocalTime.now();  获取当前时间：16:45:51.591
```

**实例方法：**

```
1、nowTime.getHour() ：16（获取小时数）

2、nowTime.getMinute() ：45（获取分钟数）

3、nowTime.getSecond() ：51（获取秒数）

4、nowTime.getNano() ：805000000（获取纳秒数）

5、LocalTime zero = LocalTime.of(0, 0, 0) ：00:00:00  （构建自定义时间）

LocalTime mid = LocalTime.parse("12:00:00") ： 12:00:00
```

### LocalDateTime

**类方法：**

```
1、LocalDateTime nowDateTime = LocalDateTime.now();  获取当前日期时间：2019-03-21T16:45:51.591

2、DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
   String nowDateTime = LocalDateTime.now().format(formatter);
使用自定义格式器DateTimeFormatter替换了Java8之前的SimpleDateFormat
```

**实例方法：**基于上方LocalDate与LocalTime的实例方法，名字相同，可直接调用。

### JDBC与Java8中的Date/Time API关联问题

最新的JDBC整合了Java8中的Date/Time API，将Java8中的Date/Time类型与数据库类型进行了映射，如下：

| 数据库类型 | Java类型      |
| ---------- | ------------- |
| date       | LocalDate     |
| time       | LocalTime     |
| timestamp  | LocalDateTime |

## Stream

> 摘抄自： https://www.jianshu.com/p/11c925cdba50 

### 中间操作符

对于数据流来说，中间操作符在执行制定处理程序后，数据流依然可以传递给下一级的操作符。

中间操作符包含8种(排除了parallel,sequential,这两个操作并不涉及到对数据流的加工操作)：

> 1. map(mapToInt,mapToLong,mapToDouble) 转换操作符，把比如A->B，这里默认提供了转int，long，double的操作符。
> 2. flatmap(flatmapToInt,flatmapToLong,flatmapToDouble) 拍平操作比如把 int[]{2,3,4} 拍平 变成 2，3，4 也就是从原来的一个数据变成了3个数据，这里默认提供了拍平成int,long,double的操作符。
> 3. limit 限流操作，比如数据流中有10个 我只要出前3个就可以使用。
> 4. distint 去重操作，对重复元素去重，底层使用了equals方法。
> 5. filter 过滤操作，把不想要的数据过滤。
> 6. peek 挑出操作，如果想对数据进行某些操作，如：读取、编辑修改等。
> 7. skip 跳过操作，跳过某些元素。
> 8. sorted(unordered) 排序操作，对元素排序，前提是实现Comparable接口，当然也可以自定义比较器。

### 终止操作符

数据经过中间加工操作，就轮到终止操作符上场了；终止操作符就是用来对数据进行收集或者消费的，数据到了终止操作这里就不会向下流动了，终止操作符只能使用一次。

> 1. collect 收集操作，将所有数据收集起来，这个操作非常重要，官方的提供的Collectors 提供了非常多收集器，可以说Stream 的核心在于Collectors。关于Collectors可查看： https://www.jianshu.com/p/6ee7e4cd5314 
> 2. count 统计操作，统计最终的数据个数。
> 3. findFirst、findAny 查找操作，查找第一个、查找任何一个 返回的类型为Optional。
> 4. noneMatch、allMatch、anyMatch 匹配操作，数据流中是否存在符合条件的元素 返回值为bool 值。
> 5. min、max 最值操作，需要自定义比较器，返回数据流中最大最小的值。
> 6. reduce 规约操作，将整个数据流的值规约为一个值，count、min、max底层就是使用reduce。
> 7. forEach、forEachOrdered 遍历操作，这里就是对最终的数据进行消费了。
> 8. toArray 数组操作，将数据流的元素转换成数组。

### map

> map 操作符要求输入一个Function的函数是接口实例，功能是将T类型转换成R类型的。 

下面的map操作将原来的单词 转换成了每个单词的长度，利用了String自身的length()方法，该方法返回类型为int。这里我直接使用了lambda表达式。

```java
public class Main {
    public static void main(String[] args) {
        Stream.of("apple","banana","orange","waltermaleon","grape")
                .map(e->e.length()) //转成单词的长度 int
                .forEach(e->System.out.println(e)); //输出
    }
}
```

 当然也可以这样，直接使用成员函数引用。

```java
public class Main {
    public static void main(String[] args) {
         Stream.of("apple","banana","orange","waltermaleon","grape")
                .map(String::length) //转成单词的长度 int
                .forEach(System.out::println);
    }
}
```

### filter

> 对某些元素进行过滤，不符合筛选条件的将无法进入流的下游

```java
public class Main {
    public static void main(String[] args) {
        Stream.of(1,2,3,1,2,5,6,7,8,0,0,1,2,3,1)
                .filter(e->e>=5) //过滤小于5的
                .forEach(e->System.out.println(e));
    }
}
```

### sorted

> 排序 底层依赖Comparable 实现，也可以提供自定义比较器 

这里Integer 实现了比较器 

```java
public class Main {
    public static void main(String[] args) {
        Stream.of(2,1,3,6,4,9,6,8,0)
                .sorted()
                .forEach(e->System.out.println(e));
    }
}
```

 这里使用自定义比较，当然User 可以实现Comparable 接口 

```java
public class Main {
    public static void main(String[] args) {

        User x = new User("x",11);
        User y = new User("y",12);
        User w = new User("w",10);

        Stream.of(w,x,y)
                .sorted((e1,e2)->e1.age>e2.age?1:e1.age==e2.age?0:-1)
                .forEach(e->System.out.println(e.toString()));

    }

    static class User {
        private String name;
        private int age;
        public User(String name, int age) {
            this.name = name;
            this.age = age;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public int getAge() {
            return age;
        }
        public void setAge(int age) {
            this.age = age;
        }
        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }

}
```

### collect

> 收集，使用系统提供的收集器可以将最终的数据流收集到List，Set，Map等容器中。

这里我使用collect 将元素收集到一个set中

```java
public class Main {

    public static void main(String[] args) {
        Stream.of("apple", "banana", "orange", "waltermaleon", "grape")
                .collect(Collectors.toSet()) //set 容器
                .forEach(e -> System.out.println(e));
    }
}
```

### skip和limit

> 可以使用skip和limit实现分页效果

skip方法指定跳过（前）元素的个数，limit方法限制元素个数。将这两个方法进行结合就达到了分页的效果。

```java
// page
int pageIndex = 0;
int pageSize = 10;
List<Account> pageOfAccount = bunchOfAccounts.stream()
        .skip(pageIndex * pageSize)
        .limit(pageSize)
        .collect(Collectors.toList());
 
pageOfAccount.forEach(account -> System.out.println(account.getUid())); 
```

### noneMatch

> 数据流中有没有一个元素与条件匹配

这里 的作用是是判断数据流中 一个都没有与aa 相等元素 ，但是流中存在 aa ，所以最终结果应该是false 。

```java
public class NoneMatch {

    public static void main(String[] args) {
        boolean result = Stream.of("aa","bb","cc","aa")
                .noneMatch(e->e.equals("aa"));
        System.out.println(result);
    }
}
```

## Optional

  > 允许传递为 null 参数,这是一个可以为null的容器对象。如果值存在则isPresent()方法会返回true，调用get()方法会返回该对象。

### 初始化

**empty**

```java
// 创建一个空的optional
Optional<Object> empty = Optional.empty();
```

**of**

  > 为非null的值创建一个Optional。
  > of方法通过工厂方法创建Optional类。需要注意的是，创建对象时传入的参数不能为null。如果传入参数为null，则抛出NullPointerException 。

```java
// 调用工厂方法创建Optional实例
Optional<String> name=Optional.of("ladybug");
// 传入参数为null，抛出Nul1PointerException.
Optional<String> someNull=Optional.of(null);
```

**ofNullable**

  > 为指定的值创建一个Optional，如果指定的值为null，则返回一个空的Optional。

```
Optional<Object> optional1 = Optional.ofNullable(null);
```

  ### isPresent

  > 如果值存在返回true，否则返回false。

```java
Optional<String> optional=Optional.of("happyjava"); 
// 值存在则输出
optional.ifPresent(System.out::println);
// 等价于
if(optional.isPresent()){
    System.out.println(optonal.get());
}
```

  ### get

  > 如果Optional有值则将其返回，否则抛出NoSuchElementException。

```java
try {
	System.out.println(empty.get());
} catch(NoSuchElementException ex){
	System.err.println(ex.getMessage());
}
```

  ### orElse

  > 如果有值则将其返回，否则返回指定的其它值。

```java
Optional<Object> optional = Optional.empty();
Object test = optional.orElse("Other Value");
System.out.println(test);
```

  ### orElseGet

  > orElseGet与orElse方法类似，区别在于得到的默认值。orElse方法将传入的字符串作为默认值，orElseGet方法可以接受函数式接口的实现用来生成默认值

```java
public void orElseGetTest() {
    Optional<Object> optional = Optional.empty();
    Object name = optional.orElseGet(this::getName);
    System.out.println(name);
}

public String getName() {
    return "ladybug";
}
```

  ### orElseThrow<span style='color:red'>(常用)</span>

  > 如果有值则将其返回，否则抛出supplier接口创建的异常。

在orElseGet方法中，我们传入一个Supplier接口。然而，在orElseThrow中我们可以传入一个lambda表达式或方法，如果值不存在来抛出异常。

```java
try {
	empty.orElseThrow(IllegalArgumentException::new);
} catch (Throwable ex){
	System.out.printin("error:"+ex.getMessage());
}
```

  ### map

  > 如果有值，则对其执行调用mapping函数得到返回值。如果返回值不为null，则创建包含mapping返回值的Optional作为map方法返回值，否则返回空Optional。

  map方法用来对Optional实例的值执行一系列操作。通过一组实现了Function接口的lambda表达式传入操作。

```java
Optional<String> name = Optional.of("ladybug");
Optional<String> upperName = name.map((value) -> value.toUpperCase());
// 等同于
// Optional<String> upperName = name.map(String::toUpperCase);
System.out.println(upperName.orElse("No value found"));//LADYBUG
```

  ### flatMap

  > 如果有值，为其执行mapping函数返回Optional类型返回值，否则返回空Optional。

flatMap方法与map方法类似，区别在于mapping函数的返回值不同。map方法的mapping函数返回值可以是任何类型T，而flatMap方法的mapping函数必须是Optional。

参照map函数，使用flatMap重写的示例如下：

```java
Optional<String> name = Optional.of("Happyjava");
Optional<String> opt = name.flatMap(e -> Optional.of(e.toLowerCase()));
System.out.println(opt.orElse("No value found"));
```

  ### filter

  > 如果有值并且满足断言条件返回包含该值的Optional，否则返回空Optional。

  这里可以传入一个lambda表达式。对于filter函数我们应该传入实现了Predicate接口的lambda表达式。

```java
List<String> names = Arrays.asList("yyc","ladybug");
for(String s:names) {
    Optional<String> nameLenLessThan7 = Optional.of(s).filter((value) -> value.length() < 7);
    System.out.println(nameLenLessThan7.orElse("The name is more than 6 characters"));
}
```

### <span style='color:red'>Optional正确使用</span>

使用Optional，我们就可以把下面这样的代码进行改写。 

```java
public static String getName(User u) {
    if (u == null)
        return "Unknown";
    return u.name;
}
```

 不过，千万不要改写成这副样子。 

```java
public static String getName(User u) {
    Optional<User> user = Optional.ofNullable(u);
    if (!user.isPresent())
        return "Unknown";
    return user.get().name;
}
```

这样改写非但不简洁，而且其操作还是和第一段代码一样。无非就是用isPresent方法来替代u==null。这样的改写并不是Optional正确的用法，我们再来改写一次。 

```java
public static String getName(User u) {
    return Optional.ofNullable(u)
                    .map(user->user.name)
                    .orElse("Unknown");
}
```

 这样才是正确使用Optional的姿势。那么按照这种思路，我们可以安心的进行链式调用，而不是一层层判断了。看一段代码： 

```java
public static String getChampionName(Competition comp) throws IllegalArgumentException {
    if (comp != null) {
        CompResult result = comp.getResult();
        if (result != null) {
            User champion = result.getChampion();
            if (champion != null) {
                return champion.getName();
            }
        }
    }
    throw new IllegalArgumentException("The value of param comp isn't available.");
}
```

 让我们看看经过Optional加持过后，这些代码会变成什么样子。

```java
public static String getChampionName(Competition comp) throws IllegalArgumentException {
    return Optional.ofNullable(comp)
            .map(c->c.getResult())
            .map(r->r.getChampion())
            .map(u->u.getName())
            .orElseThrow(()->new IllegalArgumentException("The value of param comp isn't available."));
}
```

 

*本文参考：*

- JAVA8官方文档： https://docs.oracle.com/javase/8/docs/api/
- 我的博客园：[JAVA8新特性——Lamda表达式](https://www.cnblogs.com/hyyq/p/7425666.html)
- 我的博客园： [JAVA8新特性——方法引用](https://www.cnblogs.com/hyyq/p/7435693.html)
- 博客园：[Java8新特性——Optional](https://www.cnblogs.com/happy4java/p/11206824.html)
- 博客园：[Java8 如何正确使用 Optional](https://www.cnblogs.com/jpfss/p/11002205.html)
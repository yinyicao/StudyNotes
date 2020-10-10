# IO流

## 文本IO

Java中有很多I/O类，区分为输出类和输入类。最简单的输入输出类就是使用Scanner类读取文本数据、使用PrintWriter类写文本数据。

下面是一个输出类的例子（写入文件）：

```java
PrintWriter output = new PrintWriter("temp.txt");
output.print("text io");
output.close();
```

下面是一个输入类的例子（读取文件）：

```java
Scanner input = new Scanner(new File("temp.txt"));
System.out.print(input.nextLine());
```

输入对象从文件中读取数据流，输出对象将数据流写入文件。输入对象也称为输入流（input stream），输出对象称为输出流（output stream）。

## 二进制IO

> 二进制I/O类中的所有方法都声明为抛出java.io.IOException或java.io.IOException的子类。

<img src="_images/binaryio.jpg" width = "" height ="">  

- **InputStream和OutputStream**

抽象类`InputStream`是读取二进制数据的根类（输入类），抽象类`OutputStream`是写入二进制数据的根类（输出类）。

- **FileInputStream和FileOutputStream**

`FileInputStream`和`FileOutputStream`类分别用于从文件中读取、向文件中写入**字节**。

FileInputStream类的实例可以作为参数去构造一个Scanner对象，而FileOutputStream类的实例可以作为参数构造一个PrinterWriter对象。例如：

```java
new PrintWriter(new FileOutputStream("temp.txt",true));
```

表示，如果temp.txt文件不存在，则新建这个文件。如果temp.txt文件已经存在，就将新数据追加到该文件中（true参数就表示追加）。

- **FilterInputStream和FilterOutputStream**

**过滤器数据流**是为某种目的的过滤字节的数据流。基本字节输入流提供的读取方法read只能用来读取字节。如果要读取整数值、双精度值或字符串，那就需要一个过滤器类来包装字节输入流。使用过滤器类就可以读取整数值、双精度值和字符串，而不是字节或字符。`FilterInputStream`类和`FilteroutputStream`类是过滤数据的基类。需要处理基本数值类型时，就使用`DataInputStream`类和`DataoutputStream`类来过滤字节。

- **DataInputStream 和DataOutputStream**

`DataInputStream`类扩展`FilterInputStream`类，并实现`DataInput`接口；`DataoutputStream` 类扩展`FilteroutputStream`类，并实现`Dataoutput`接口，`Dataoutput`接口中具体定义了过滤的方法。

`DataInputStream`从数据流读取字节，并且将它们转换为合适的基本类型值或字符串。`DataOutputStream`将基本类型的值或字符串转换为字节，并且将字节输出到数据流。
如下是具体使用实例：

```java
try(//reate an output stream for file temp.dat 
    DataOutputStream output = new DataOutputStream(new FileOutputStream("temp.dat"));
){
//Write student test scores to the file 
    output.writeUTF("John"); 
    output.writeDouble(85.5); 
    output.writeUTF("Jim"); 
    output.writeDouble(185.5); 
    output.writeUTF("George"); 
    output.writeDouble(105.25);
}
try (//Create an input stream for file temp.dat 
    DataInputStream input=new DataInputStream(new FileInputStream("temp.dat"));
){
	//Read student test scores from the file System. 
    out.println(input.readUTF()+""+input.readDouble()); 
    System.out.println(input.readUTF()+""+input.readDouble()); 
    System.out.println(input.readUTF()+""+input.readDouble());
}
```

输出：

John 85.5
Susan 185.5
Kim 105.25

- ⭐**BufferedInputStream和BufferedOutputStream**

> 应该总是使用缓冲区 I/O 来加速输入和输出。对于小文件，我们可能注意不到性能的提升。但是，对于超过 100MB 的大文件，我们将会看到使用缓冲的 I/O 带来的实质性的性能提升。

**BufferedInputStream** 类和**BufferedoutputStream**类可以通过减少磁盘读写次数来提高输入和输出的速度。

使用**BufferdInputstream**时，磁盘上的整块数据一次性地读入到内存中的缓冲区中。然后从缓冲区中将个别的数据传递到程序中。

使用**BufferedOutputStream**，个别的数据首先写入到内存中的缓冲区中。当缓冲区已满时，缓冲区中的所有数据一次性写入到磁盘中。

**BufferedlnputStream** 类和 **BufferedOutputStream** 类在后台管理了一个缓冲区，根据要求自动从磁盘中读取数据和写入数据。

构造器如下（**BufferedlnputStream** 同理）：

```java
BufferedOutputStream(in: InputStream);//从一个OutputStream对象创建一个BufferedOutputStream
BufferedOutputStream(in: InputStream, bufferSIze: int);//从一个OutputStream对象创建一个BufferedOutputStream,并指定缓冲区大小(不指定默认为512个字节) 
```

使用方式：

```java
DataOutputStream output = new DataOutputStream( new BufferedOutputStream(new Fi1eOutputStream("temp.dat")));
DataInputStream input = new DataInputStream(new BufferedInputStreamCnew Fi1elnputStream("temp.dat")));
```

- **ObjectInputStream 和ObjectOutputStream**

> DatalnputStream 类和 DataOutputStream 类可以实现基本数据类型与字符串的输入和输出。而 ObjectlnputStream 类和 ObjectOutputStream 类除了可以实现基本数据类型与字符串的输入和输出之外，还可以实现输入和输出可序列化的对象

- - 可序列化对象

可序列化对象必须是`Serializable`接口的实例对象，`Serializable`接口是一种标记接口。因为它没有方法，所以，不需要在类中为实现`Serializab1e`接口增加额外的代码。实现这个接口可以启动Java的序列化机制，自动完成存储对象和数组的过程。

- - 对象I/O实例

写：

```java
 try ( 
     ObjectOutputStream output = 
     new ObjectOutputStream(new FileOutputStream("object.dat")); 
     ) { 
    output.writeUTF("John"); 
    output,writeDouble(85.5); 
    output.writeObject(new java.uti1.Date()); //写入一个Date对象
}
```

读：

```java
try ( 
    ObjectlnputStream input = new ObjectlnputStream(new Fi1elnputStreamC"object.dat"))； ) { 
    String name = input.readUTF(); 
    double score = input.readDouble(); 
   java.util.Date date = (java.util.Date)(input.readObject());//读取一个Date对象
}                                                    
```

## 文本IO和二进制IO比较

二进制I/O从文件中读取一个字节，将其直接复制到内存中，反之亦然。文本I/O需要编码和解码。在编写字符时，JVM将Unicode转换为特定于文件的编码，并在读取字符时将特定于文件的编码转换为Unicode。所以，二进制I/O效率比文本I/O高。


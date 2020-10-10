# Linux

## 常用命令

> http://www.runoob.com/linux/linux-command-manual.html

### 切换路径(目录)

> 使用cd命令，可用于切换当前目录，它的参数是要切换到的目录的路径，可以是绝对路径，也可以是相对路径

```shell
cd /home    #进入 '/ home' 目录
cd ..            #返回上一级目录 
cd ../..         #返回上两级目录 
cd               #进入个人的主目录 
cd ~user1   #进入个人的主目录 
cd -             #返回上次所在的目录
```

### 显示工作目录

直接输入`pwd`可显示出当前的工作路径

```shell
pwd #显示工作路径
```

### 查看文件与目录

#### ls

> 查看文件与目录的命令，list之意

```shell
ls #查看目录中的文件 
ls -l #显示文件和目录的详细资料
ll #ls -l的一个别名
ls -a #列出全部文件，包含隐藏文件
ls -R #连同子目录的内容一起列出（递归列出），等于该目录下的所有文件都会显示出来  
ls [0-9] #显示包含数字的文件名和目录名
```

#### tail

> 常用来查看正在运行程序的日志文件

```shell
tail -f app.log  #循环读取app.log日志文件 -f参数表示循环读取（最常用）
```

### 进程查看

检查tomcat进程是否存在

```shell
ps -ef |grep tomcat
```

**ps的意思是process status，即进程状态。在控制台执行`man ps`命令可以查看ps命令后面的命令选项的含义**

**-e 显示所有进程。**
**-f 全格式。**

### 复制文件(文件夹)

命令格式：

```shell
cp [-adfilprsu] 源文件(source) 目标文件(destination)
cp [option] source1 source2 source3 ... directory
```

参数说明：
**-a**:是指archive的意思，也说是指复制所有的目录
**-d**:若源文件为连接文件(link file)，则复制连接文件属性而非文件本身
**-f**:强制(force)，若有重复或其它疑问时，不会询问用户，而强制复制
**-i**:若目标文件(destination)已存在，在覆盖时会先询问是否真的操作
**-l**:建立硬连接(hard link)的连接文件，而非复制文件本身
**-p**:与文件的属性一起复制，而非使用默认属性
**-r**:递归复制，用于目录的复制操作
**-s**:复制成符号连接文件(symbolic link)，即“快捷方式”文件
**-u**:若目标文件比源文件旧，更新目标文件 

将/test1目录下的file1复制到/test3目录，并将文件名改为file2

```shell
cp /test1/file1 /test3/file2
```

将/home/user1目录下的所有东西拷到/root/temp/下而不拷贝user1目录本身

```shell
cp -Rf /home/user1/* /root/temp/
```

### 修改文件（文件夹）名

命令格式：

```shell
mv file1 file2
```

### 移动文件(文件夹)

命令格式：

```shell
mv [-fiv] source destination
```

参数说明：
**-f**:force，强制直接移动而不询问
**-i**:若目标文件(destination)已经存在，就会询问是否覆盖
**-u**:若目标文件已经存在，且源文件比较新，才会更新

将/test1目录下的file1复制到/test3 目录，并将文件名改为file2

```shell
mv /test1/file1 /test3/file2
```

移动/WorkReport/web.xml文件到/WorkReport/WEB-INF/下

```shell
mv ./WorkReport/web.xml ./WorkReport/WEB-INF/
```

移动/data/new 到/data/old/文件夹下(**注：**移动文件夹的话就不要再加 / 了)

```shell
mv /data/new /data/old/
```

如果是移动文件夹下的所有文件的话就可以文件夹后面跟上 /* 

```shell
mv /data/new/* /data/old/
```

### 删除文件(文件夹)

命令格式：

```shell
rm [-fir] 目录名字 
```

参数说明：
**-f**:强制删除,不作任何提示
**-i**:交互模式，在删除前询问用户是否操作
**-r**:递归删除，常用在目录的删除

删除/var/log/httpd/access目录以及其下所有文件、文件夹

```shell
rm -rf /var/log/httpd/access
```

强制删除/var/log/httpd/access.log这个文件

```shell
rm -f /var/log/httpd/access.log
```

### 创建文件夹

创建nginx文件夹

```shell
mkdir nginx
```

### 查找文件

#### 按照文件名查找

- 在根目录下查找文件httpd.conf，表示在整个硬盘查找

```shell
find / -name httpd.conf
```

- 在/etc目录下文件httpd.conf

```shell
find /etc -name httpd.conf
```

- 使用通配符*(0或者任意多个)。表示在/etc目录下查找文件名中含有字符串‘srm’的文件

```shell
find /etc -name '*srm*'
```

- 表示当前目录下查找文件名开头是字符串‘srm’的文件

```shell
find . -name 'srm*' 
```

### 解压和压缩

> 一般的，对文件进行打包，默认情况并不会压缩，如果指定了相应的参数，它还会调用相应的压缩程序（如gzip和bzip等）进行压缩和解压
>
> 参考：https://www.cnblogs.com/newcaoguo/p/5896975.html

#### 常见压缩命令参数

##### 必须

**-f**: 使用档案名字，切记，这个参数是最后一个参数，后面只能接档案名。

##### 可选

> 下面的参数是根据需要在压缩或解压档案时可选的

**-z**：有gzip属性的
**-j**：有bz2属性的
**-Z**：有compress属性的
**-v**：显示所有过程
**-O**：将文件解开到标准输出

##### 独立

> 这五个是独立的命令，压缩解压都要用到其中一个，可以和别的命令连用但只能用其中一个。

**-c**: 建立压缩档案
**-x**：解压
**-t**：查看内容，查看打包文件的内容含有哪些文件名
**-r**：向压缩归档文件末尾追加文件
**-u**：更新原压缩包中的文件

#### 常见解压/压缩命令

##### tar

解包：

```shell
tar -xvf FileName.tar
```

打包：

```shell
tar -cvf FileName.tar DirName
```

（注：tar是打包，不是压缩！）

##### .gz

解压1：

```shell
gunzip FileName.gz
```

解压2：

```shell
gzip -d FileName.gz
```

压缩：

```shell
gzip FileName
```

##### .tar.gz和.tgz

解压：

```shell
tar -zxvf FileName.tar.gz
```

压缩：

```shell
tar -zcvf FileName.tar.gz DirName
```

##### .bz2

解压1：

```shell
bzip2 -d FileName.bz2
```

解压2：

```shell
bunzip2 FileName.bz2
```

压缩：

```shell
 bzip2 -z FileName
```

##### .tar.bz2

解压：

```shell
tar -jxvf FileName.tar.bz2
```

压缩：

```shell
tar -jcvf FileName.tar.bz2 DirName
```

##### .bz

解压1：

```shell
bzip2 -d FileName.bz
```

解压2：

```shell
bunzip2 FileName.bz
```

压缩：未知

##### .tar.bz

解压：

```shell
tar -jxvf FileName.tar.bz
```

压缩：未知

##### .Z

解压：

```shell
uncompress FileName.Z
```

压缩：

```shell
compress FileName
```

##### .tar.Z

解压：

```shell
tar -Zxvf FileName.tar.Z
```

压缩：

```shell
tar -Zcvf FileName.tar.Z DirName
```

##### **.**zip

解压：

```shell
unzip FileName.zip
```

压缩：

```shell
zip FileName.zip DirName
```

##### .rar

解压：

```shell
rar -x FileName.rar
```

压缩：

```shell
rar -a FileName.rar DirName 
```

### Vim

> vi的升级版本。vi使用于文本编辑，但是vim更适用于coding。完全可以用vim替代vi。

插入模式：在此模式下可以输入字符，按ESC将回到命令模式。 
命令模式：可以移动光标、删除字符等。 
低行模式：可以保存文件、退出vi、设置vi、查找等功能(低行模式也可以看作是命令模式里的)。 

#### **打开文件、保存、关闭文件**

```shell
vi filename       //打开filename文件 
按键esc           //退出插入模式
按键shift+； //出现数据命令行，也就是一个：号，然后就可以输入下面的命令了
:w       //保存文件 
:w vpser.net //保存至vpser.net文件 
:q          //退出编辑器，如果文件已修改请使用下面的命令 
:q!        //退出编辑器，且不保存 
:wq         //退出编辑器，且保存文件 
```

#### **插入文本或行**

> 命令模式下使用，执行下面命令后将进入插入模式，按ESC键可退出插入模式

```shell
a      //在当前光标位置的右边添加文本 
i       //在当前光标位置的左边添加文本 
A     //在当前行的末尾位置添加文本 
I      //在当前行的开始处添加文本(非空字符的行首) 
O     //在当前行的上面新建一行 
o     //在当前行的下面新建一行 
R    //替换(覆盖)当前光标位置及后面的若干文本 
J    //合并光标所在行及下一行为一行(依然在命令模式) 
```

#### 删除与复制

**删除**

单行删除(光标移动到待删除行)

```shell
1d或者dd
```

多行删除

```shell
1,10d //删除1-10行
```

```
Ndd //删除光标所在行以下的N行
```

**复制**

方法一：

> 此方法适合复制少量行文本的情况，复制第6行（包括）下面的2行数据，放到第9行下面。
>
> 如果是复制一行，直接用y和p就可以了

```shell
光标放到第6行，
输入：2yy
光标放到第9行，
输入：p
```

方法二

> 我们可以使用命令v然后加上 “上下键”，在vim可以看到部分文本变颜色，就像在windows系统下的shift键加上鼠标左键的效果一样
> 然后用y命令复制，然后粘贴用p命令显示在光标下面

```shell
v + 上or下键
y
p
```

方法三(在命令模式下按shift+;出现“:”后面输入)：

> 复制第6行到第9行之间的内容到第12行后面

```shell
6,9 co 12
```


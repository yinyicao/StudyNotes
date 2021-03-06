## 前言

这些命令都来自自己总结平时工作实践中容易遇到的一些和自认为比较重要的命令，仅供参考使用。所以很多命令或参数都是常用的我才写这儿，更多完整命令可参考： https://www.runoob.com/linux/linux-command-manual.html 

## 文件管理

### tail命令

> 常用于查看滚动的日志文件

```shell
tail -f testlog.log
# Ctrl+c退出
```

### less命令

> 也常用来查看日志

```shell
less testlog.log
```

**less指令几个有用的操作:** 

**/**+搜索内容:可以自上而下搜索文本。<br>
**?**+搜索内容:可以自下而上搜索文本。<br>**n** 是根据输入的查询条件值向上翻页查找 <br>
**N** 是根据输入的查询条件值向下翻页查找 <br>**Ctrl+Z**： 退出 <br>**Shit+G**：翻到末页，即查看最新日志。<br>
**Ctrl+B**:向前翻页。<br>
**Ctrl+F**:向后翻页。<br>

### vi命令和vim命令

> vim是vi命令的升级版本。vi使用于文本编辑，但是vim更适用于coding。完全可以用vim替代vi。 

命令模式：一般情况下，使用vim/vi命令打开文件时默认的模式，在此模式下可以移动光标、删除字符等。<br>
插入模式：在此模式下可以输入字符，按ESC将回到命令模式。 <br>
低行模式：可以保存文件、退出vi、设置vi、查找等功能(低行模式也可以看作是命令模式里的)。<br>

 **打开文件、保存、关闭文件**

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

 **插入文本或行**

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

**删除与复制**

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

### mkdir命令

创建nginx文件夹

```shell
mkdir nginx
```

### find命令

在指定目录下按照文件名查找

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

- 将目前目录及其子目录下所有最近 20 天内更新过的文件列出

```shell
find . -ctime -20
```

### cat命令

```shell
cat [-AbeEnstTuv] [--help] [--version] fileName
```

查看文本文件/etc/test.txt内容

```shell
cat /etc/test.txt
```

把 textfile1 的文档内容加上行号后输入 textfile2 这个文档里：

```shell
cat -n textfile1 > textfile2
```

把 textfile1 和 textfile2 的文档内容加上行号（空白行不加）之后将内容附加到 textfile3 文档里：

```shell
cat -b textfile1 textfile2 >> textfile3
```

参数说明：

**-n** 或 **--number**：由 1 开始对所有输出的行数编号。<br>
**-b** 或 **--number-nonblank**：和 **-n** 相似，只不过对于空白行不编号。<br>
**-s** 或 **--squeeze-blank**：当遇到有连续两行以上的空白行，就代换为一行的空白行。<br>
**-v** 或 **--show-nonprinting**：使用 **^** 和 **M-** 符号，除了 **LFD** 和 **TAB** 之外。<br>
**-E** 或 **--show-ends** : 在每行结束处显示 <b>$</b>。<br>
**-T** 或 **--show-tabs**: 将 TAB 字符显示为 **^I**。<br>
**-e** : 等价于 **-vE**。<br>
**-A**, **--show-al**l：等价于 **-vET**。<br>
**-e**：等价于"**-vE**"选项；<br>
**-t**：等价于"**-vT**"选项。


### mv命令

| 命令格式         | 运行结果                                                     | 执行结果  |
| :--------------- | :----------------------------------------------------------- | --------- |
| mv 文件名 文件名 | 将源文件名改为目标文件名                                     | 改名      |
| mv 文件名 目录名 | 将文件移动到目标目录                                         | 移动      |
| mv 目录名 目录名 | 目标目录已存在，将源目录移动到目标目录；目标目录不存在则改名 | 移动/改名 |
| mv 目录名 文件名 | /                                                            | /         |

#### 文件或目录改名

命令格式：

```shell
mv file1 file2
```

#### 文件或目录移动位置

命令格式：

```shell
mv [-fiv] source destination
```

参数说明：
**-f**:force，强制直接移动而不询问<br>
**-i**:若目标文件(destination)已经存在，就会询问是否覆盖<br>
**-u**:若目标文件已经存在，且源文件比较新，才会更新<br>

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

### rm命令

命令格式：

```shell
rm [-fir] 目录名字 
```

参数说明：
**-f**:强制删除,不作任何提示<br>
**-i**:交互模式，在删除前询问用户是否操作<br>
**-r**:递归删除，常用在目录的删除<br>

删除/var/log/httpd/access目录以及其下所有文件、文件夹

```shell
rm -rf /var/log/httpd/access
```

强制删除/var/log/httpd/access.log这个文件

```shell
rm -f /var/log/httpd/access.log
```

删除多个文件并提示是否删除

```shell
rm -i test.txt test2.txt test3.txt
#rm -i test*.txt
```

### cp命令

命令格式：

```shell
cp [-adfilprsu] 源文件(source) 目标文件(destination)
cp [option] source1 source2 source3 ... directory
```

参数说明：
**-a**:是指archive的意思，也说是指复制所有的目录<br>
**-d**:若源文件为连接文件(link file)，则复制连接文件属性而非文件本身<br>
**-f**:强制(force)，若有重复或其它疑问时，不会询问用户，而强制复制<br>
**-i**:若目标文件(destination)已存在，在覆盖时会先询问是否真的操作<br>
**-l**:建立硬连接(hard link)的连接文件，而非复制文件本身<br>
**-p**:与文件的属性(如**修改时间和访问权限**)一起复制，而非使用默认属性<br>
**-r**:递归复制（**该目录下所有的子目录和文件**），用于目录的复制操作<br>
**-s**:复制成符号连接文件(symbolic link)，即“快捷方式”文件<br>
**-u**:若目标文件比源文件旧，更新目标文件 <br>

将/test1目录下的file1复制到/test3目录，并将文件名改为file2

```shell
cp /test1/file1 /test3/file2
```

将/home/user1目录下的所有东西拷到/root/temp/下而不拷贝user1目录本身

```shell
cp -Rf /home/user1/* /root/temp/
```

### scp命令

> Linux scp 命令用于 Linux 之间复制文件和目录。
>
> scp 是 secure copy 的缩写, scp 是 linux 系统下基于 ssh 登陆进行安全的远程文件拷贝命令。
>
> scp 是加密的，[rcp](https://www.runoob.com/linux/linux-comm-rcp.html) 是不加密的，scp 是 rcp 的加强版

语法：

```shell
scp [-1246BCpqrv] [-c cipher] [-F ssh_config] [-i identity_file]
[-l limit] [-o ssh_option] [-P port] [-S program]
[[user@]host1:]file1 [...] [[user@]host2:]file2
# scp [可选参数] file_source file_target 
```

比较常用的**参数说明**：

- **-B**： 使用批处理模式（传输过程中不询问传输口令或短语）
- **-C**： 允许压缩。（将-C标志传递给ssh，从而打开压缩功能）
- **-p**：保留原文件的修改时间，访问时间和访问权限。
- **-q**： 不显示传输进度条。
- **-r**： 递归复制整个目录。
- **-P port**：注意是大写的P, port是指定数据传输用到的端口号

**实例：**

1、从本地复制到远程

```shell
#命令执行后需要再输入密码，仅指定了远程的目录，文件名字不变
scp local_file remote_username@remote_ip:remote_folder 
#命令执行后需要再输入密码，指定了远程的文件名字
scp local_file remote_username@remote_ip:remote_file 
#命令执行后需要输入用户名和密码，仅指定了远程的目录，文件名字不变
scp local_file remote_ip:remote_folder 
##命令执行后需要输入用户名和密码，指定了远程的文件名字
scp local_file remote_ip:remote_file
#递归复制整个目录
scp -r local_folder remote_username@remote_ip:remote_folder 
```

使用命令将本地的包传到远程服务器，并重命名（本地为windows系统可以使用`gitbash`客户端）：

```shell
scp ./app.war user1@10.6.77.84:/opt/app/jboss-eap-5.0/jboss-as/server/server/deploy/app.war-2021333
```

2、从远程复制到本地

从远程复制到本地，只要将从本地复制到远程的命令的后2个参数调换顺序即可。

```shell
scp -r root@199.9.9.9:/home/root/others/ /home/space/music/
```

### chmod命令

> 修改文件权限命令， Linux/Unix 的文件调用权限分为三级 : 文件拥有者、群组、其他。 

语法

```shell
chmod [-cfvR] [--help] [--version] mode file...
```

参数说明：

mode : 权限设定字串，格式如下 : 

`[ugoa...][[+-=][rwxX]...][,...] `

其中： 

**u** 表示该文件的拥有者，**g** 表示与该文件的拥有者属于同一个群体(group)者，o 表示其他以外的人，**a** 表示这三者皆是。 <br>
**+** 表示增加权限、<b>-</b> 表示取消权限、<b>=</b> 表示唯一设定权限。 <br>
**r** 表示可读取，**w** 表示可写入，**x** 表示可执行，**X** 表示只有当该文件是个子目录或者该文件已经被设定过为可执行。 <br>

其他参数说明：

**-c** : 若该文件权限确实已经更改，才显示其更改动作 <br>
**-f** : 若该文件权限无法被更改也不要显示错误讯息<br>
**-v** : 显示权限变更的详细资料 <br>
**-R** : 对目前目录下的所有文件与子目录进行相同的权限变更(即以递回的方式逐个变更) <br>
**--help** : 显示辅助说明 <br>
**--version** : 显示版本<br>

将文件 file1.txt 设为所有人皆可读取 :

```shell
chmod ugo+r file1.txt
#或者也可以
chmod a+r file1.txt
```

将文件 file1.txt 与 file2.txt 设为该文件拥有者，与其所属同一个群体者可写入，但其他以外的人则不可写入 :

```shell
chmod ug+w,o-w file1.txt file2.txt
```

将 ex1.py 设定为只有该文件拥有者可以执行 :

```shell
chmod u+x ex1.py
```

将目前目录下的所有文件与子目录皆设为任何人可读取 :

```shell
chmod -R a+r *
```



一般工作中就用数字表示 User、Group、及Other的权限 。

```shell
chmod 777 file #等同于chmod a=rwx file
```

语法为：

```shell
chmod abc file
```

其中a,b,c各为一个数字，分别表示User、Group、及Other的权限。

 r=4，w=2，x=1（r 表示可读取，w 表示可写入，x 表示可执行）

- 若要rwx属性则4+2+1=7；
- 若要rw-属性则4+2=6；
- 若要r-x属性则4+1=5。

## 磁盘管理

### df命令

> 检查linux服务器的文件系统的磁盘空间占用情况。可以利用该命令来获取硬盘被占用了多少空间，目前还剩下多少空间等信息。

通过-h选项，可以产生可读的格式df命令的输出

```shell
df -h
```

**命令参数：**

**-a** 全部文件系统列表

**-h** 方便阅读方式显示

**-H** 等于“-h”，但是计算式，1K=1000，而不是1K=1024

**-i** 显示inode信息

**-k** 区块为1024字节

**-l** 只显示本地文件系统

**-m** 区块为1048576字节



“df -h”这条命令再熟悉不过。以更易读的方式显示目前磁盘空间和使用情况。

“df -i” 以inode模式来显示磁盘使用情况。

**df -h 和df -i的区别是什么？同样是显示磁盘使用情况，为什么显示占用百分比相差甚远？**

df -h的比较好解释，就是查看磁盘容量的使用情况。

至于df -i，先需要去理解一下inode，最简单的说法，inode包含的信息：文件的字节数，拥有者id，组id，权限，改动时间，链接数，数据block的位置。相反是不表示文件大小。这就是为什么df -h和df -i 显示的结果是不一样的原因。

[ps](http://www.111cn.net/fw/photo.html)：在df -h 和df -i 显示使用率100%，基本解决方法都是删除文件。

df -h  是去删除比较大无用的文件-----------大文件占用大量的磁盘容量。

df -i  则去删除数量过多的小文件-----------过多的文件占用了大量的inode号。

### cd命令

> 使用cd命令，可用于切换当前目录，它的参数是要切换到的目录的路径，可以是绝对路径，也可以是相对路径

```shell
cd /home    #进入 '/ home' 目录
cd ..            #返回上一级目录 
cd ../..         #返回上两级目录 
cd               #进入个人的主目录 
cd ~user1   #进入个人的主目录 
cd -             #返回上次所在的目录
```

### pwd命令

直接输入`pwd`可显示出当前的工作路径

```shell
pwd #显示工作路径
```

### ls命令和ll命令

>  `ll `命令列出的信息更加详细，有时间，是否可读写等信息 

查看目录中的文件

```shell
ll
```

或

```shell
ls
```

其它`ls`带参数常用命令

```shell
ls -l #显示文件和目录的详细资料 
ls -a #列出全部文件，包含隐藏文件
ls -R #连同子目录的内容一起列出（递归列出），等于该目录下的所有文件都会显示出来  
ls [0-9] #显示包含数字的文件名和目录名
```

### du命令

> du命令用于显示目录或文件的大小。du会显示指定的目录或文件所占用的磁盘空间。

方便阅读的格式显示test目录所占空间情况： 

```shell
du -h test
```

显示各个文件夹大小 ，1表示目录层数： `--max-depth=<目录层数>` 超过指定层数的目录后，予以忽略。 

```shell
du -h --max-depth=1
```



## 压缩解压

### 常见压缩命令参数

**必须**

**-f**: 使用档案名字，切记，这个参数是最后一个参数，后面只能接档案名。

**可选**

> 下面的参数是根据需要在压缩或解压档案时可选的

**-z**：有gzip属性的
**-j**：有bz2属性的
**-Z**：有compress属性的
**-v**：显示所有过程
**-O**：将文件解开到标准输出

**独立**

> 这五个是独立的命令，压缩解压都要用到其中一个，可以和别的命令连用但只能用其中一个。

**-c**: 建立压缩档案
**-x**：解压
**-t**：查看内容，查看打包文件的内容含有哪些文件名
**-r**：向压缩归档文件末尾追加文件
**-u**：更新原压缩包中的文件

### tar命令

解压：

```shell
tar -zxvf FileName.tar.gz
```

压缩：

```shell
tar -zcvf FileName.tar.gz DirName
```

列出压缩文件test.tar.gz内容：

```shell
tar -tzvf test.tar.gz
```

### zip和unzip命令

解压：

```shell
unzip FileName.zip
```

压缩：

```shell
zip FileName.zip DirName
```

## 网络通讯

### ifconfig命令

> ifconfig可显示目前的设置，或是设置网络设备的状态（不常用）。

```shell
ifconfig
```

如果只是查看本机IP地址，也可以用`ip addr`命令。

### telnet命令

> telnet命令用于远端（远程）登录，可用来测试指定端口是否开放。

显示IP地址(10.221.x.x)的指定端口(8080)是否可访问（开放）

```shell
telnet  10.221.x.x  8080
```

### ssh命令

> 远程连接服务器，Windows下使用需要安装SSH工具。Putty、OpenSSH等，Git Bash也可以用。

```shell
ssh name@server-ip
```

或者

```shell
ssh server-ip -l name
```

以上两种方式都可以远程登录到服务器，server-ip代表远程服务器的IP地址，name代表SSH登陆进远程服务器的用户名，一般为root。

如果远程服务器的端口是其他的，在后面加上-p参数。

```
ssh name@server-ip -p 12345
```

连接成功终端就会提示继续输入用户密码，输入就好了。

## 系统管理

### ps命令

 用于显示当前进程 (process) 的状态 ，一般和`grep`命令结合起来用。

>  gerp命令用于查找文件里符合条件的字符串 ，也就是起过滤作用

如：列出java相关进程信息

```shell
ps aux | grep java
```

或者

```shell
ps -ef | grep java
```

注： `-ef`是System V展示风格，而`aux`是BSD风格 ，一般使用前者

### top命令

>  top命令用于实时显示 process 的动态 

```shell
top
```

### w命令

> w命令用于显示目前登入系统的用户信息。
>
> 执行这项指令可得知目前登入系统的用户有哪些人，以及他们正在执行的程序。
>
> 单独执行 w 指令会显示所有的用户，您也可指定用户名称，仅显示某位用户的相关信息。

```shell
w
```

执行后显示

```shell
[root@yyc ~]# w
 15:07:41 up 10 days, 22:17,  1 user,  load average: 0.13, 0.10, 0.07
USER     TTY      FROM             LOGIN@   IDLE   JCPU   PCPU WHAT
root     pts/0    183.226.116.174  15:07    5.00s  0.00s  0.00s w
```

### uptime命令

> 执行效果相当于w命令的第一行

```shell
uptime
```

执行后显示

```shell
[root@yyc ~]# uptime
 15:07:47 up 10 days, 22:17,  1 user,  load average: 0.12, 0.09, 0.07
```

1. 当前时间 15:07:47
2. 系统已运行的时间 10 days, 22:17
3. 当前在线用户 1 user
4. 平均负载：0.12, 0.09, 0.07，最近1分钟、5分钟、15分钟系统的负载

另外有一个参数-V（大写），用来查询版本的

```shell
[root@yyc ~]# uptime -V
uptime from procps-ng 3.3.10
```

 `procps`是一个实用程序包，主要包括`ps`,` top`,` kill`等程序主要用来显示与控制一些系统信息，进程状态之类的内容 。

### jps命令

> 即Java Virtual Machine Process Status Tool，是java提供的一个显示当前所有java进程pid的命令，适合在linux/unix平台上简单察看当前java进程的一些简单情况。
>
> ps命令主要是用来显示当前系统的进程情况，有哪些进程以及进程id。 
>
> jps 也是一样，它的作用是显示当前系统的java进程情况及进程id。
>
> 我们可以通过它来查看我们到底启动了几个java进程（因为每一个java程序都会独占一个java虚拟机实例）

```shell
C:\Users\yyc>jps
11408 RemoteMavenServer36
20080 Launcher
5296 RemoteMavenServer36
5792 RemoteMavenServer36
5904 Jps
8224
```

注：在当前命令行下打jps(jps存放在JAVA_HOME/bin/jps，需将JAVA_HOME/bin/加入到Path环境变量中) 。

**常用参数：**

```shell
jps -q # 举例
```

**-q**  只显示pid，不显示class名称,jar文件名和传递给main方法的参数

**-m** 输出传递给main方法的参数，在嵌入式jvm上可能是null

**-l**    输出应用程序main class的完整package名或者应用程序的jar文件完整路径名

**-v** 输出传递给JVM的参数

**-V** 隐藏输出传递给JVM的参数

## 系统设置

### history命令

> 查询执行过的历史命令（ 保存在内存中的，当退出或者登录shell时，会自动保存或读取。在内存中，历史命令仅能够存储1000条历史命令，该数量是由**环境变量`HISTSIZE`**进行控制 ）。
>
>  该命令单独使用时，仅显示历史命令，在命令行中，可以使用符号`!`执行指定序号的历史命令。 

```shell
history
```

**参数说明：**

 **-c**：清空当前历史命令； 

 **-a**：将历史命令缓冲区中命令写入历史命令文件`/root/.bash_history`；

 **-r**：将历史命令文件中的命令读入当前历史命令缓冲区； 

 **-w**：将当前历史命令缓冲区命令写入历史命令文件中`/root/.bash_history` ;

 **n**：打印最近的n条历史命令

 `history`命令会列出bash保存的所有历史命令，并且给它们编了号，我们可以使用“叹号接编号”的方式运行特定的历史 

 **!数字**：表示调出历史记录的几条命令 

 **!!**: 表示最近使用的一次操作的命令 

 **!字母**：调出最近使用一次以此字母开头的命令 

显示最近10条命令：

```shell
history 10
```

执行第10条历史命令：

```shell
!10
```


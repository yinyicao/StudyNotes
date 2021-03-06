> vim是vi命令的升级版本。vi使用于文本编辑，但是vim更适用于coding。完全可以用vim替代vi。 

命令模式：一般情况下，使用vim/vi命令打开文件时默认的模式，在此模式下可以移动光标、删除字符等。
插入模式：在此模式下可以输入字符，按ESC将回到命令模式。 
低行模式：可以保存文件、退出vi、设置vi、查找等功能(低行模式也可以看作是命令模式里的)。

###  打开文件、保存、关闭文件

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

###  插入文本或行

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

### 删除

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

### 复制

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

### 搜索

> 命令模式下输入/要搜索的字符串或者字符，比如搜索user，默认从头开始搜索

```shell
/user
```

查看下一个匹配：小写n

查看上一个匹配：大写N

从后往前搜索：?要搜索的字符串或者字符，比如搜索user，`?user`
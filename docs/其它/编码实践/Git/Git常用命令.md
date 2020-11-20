# Git笔记

## 基本操作命令

> 前提条件：已经进入到工作区

### 创建版本库和提交

- 初始化一个Git仓库，把一个文件夹变为Git可以管理的本地仓库

  ```shell
  git init
  ```

- 添加文件到Git仓库，分两步

1. 提交到git暂存区
   > 工作区中有一个隐藏目录.git，这个是Git的版本库。Git的版本库中存了一个称为stage的暂存区

     添加指定文件到暂存区：

     ```shell
     git add <file> # git add gitTest.txt
     ```

     添加所有修改的的文件到暂存区<!--(add和.之间有一个空格)-->：

     ```shell
     git add .
     ```


  2. 提交到本地仓库

     ```shell
     git commit -m <message> # git commit -m 添加gitTest文件
     ```

     message为提交的备注信息，一般为这次提交干了什么


- add和commit组合命令<!--（git add .和git commit -m <mesage>的合并）-->

  ```shell
  git commit -am <message>
  ```

### 查看日志

- 查看提交日志

  ```shell
  git log
  ```

  例如：

  ```shell
  $ git log
  commit 2e8b022a6e2ab2c8f246620f49fc13fb591e810a (HEAD -> master)
  Author: yyc007 <34782655@qq.com>
  Date:   Thu Jan 17 10:41:54 2019 +0800
  
      添加了三行
  
  commit 16ffdfa48ccc31bdb11d66ad9a552bc56d90d793
  Author: yyc007 <34782655@qq.com>
  Date:   Thu Jan 17 10:22:39 2019 +0800
  
      添加gitTest文件
  ```
  
- 查看分支合并图

  会通过合并图的方式显示log日志。

  ```shell
  git log --graph
  ```

  例如：

  ```shell
  $ git log --graph
  *   commit ef2b825355ead88be7ed5bcf183acaab597658db (HEAD -> master, origin/mast
  er, origin/HEAD)
  |\  Merge: 8765b8d 7dc7681
  | | Author: yyc <yinyicao@qq.com>
  | | Date:   Thu Jul 2 13:20:51 2020 +0000
  | |
  | |     Merge branch 'modifyRegister' into 'master'
  | |
  | |     bug修复
  | |
  | |     See merge request CloudFly/flight!281
  | |
  | * commit 7dc76813faea0cf988c1c661cda277b0dd5dc74c
  |/  Author: yyc <yinyicao@qq.com>
  |   Date:   Thu Jul 2 21:18:21 2020 +0800
  |
  |       bug修复
  |
  | *   commit 84f0e424b8861e32eb94d57bb99295a97f5b5ad4
  | |\  Merge: f387c0a 7787f29
  | |/  Author: yyc <yinyicao@qq.com>
  |/|   Date:   Thu Jul 2 20:00:01 2020 +0800
  | |
  | |       Merge remote-tracking branch 'origin/master'
  | |
  * |   commit 7787f293c819b4b6b871dbd152bebf0b5df9dc8f
  |\ \  Merge: a2bacb4 495c543
  | | |
  ```
- 查看操作命令历史记录日志

  > 可以通过 git reset --hard [hash] 回退到指定操作版本，其中hash为git reflog看到的操作ID。
  
  ```shell
git reflog
  ```

  例如：
  
  ```shell
  $ git reflog
  bc70ed2 (HEAD -> master) HEAD@{0}: reset: moving to HEAD~1
  7fd2c6a HEAD@{1}: commit: 添加第二行内容
  bc70ed2 (HEAD -> master) HEAD@{2}: commit: 添加第一行内容
  16ffdfa HEAD@{3}: reset: moving to 16ffdfa48ccc31bdb11d66ad9a552bc56d90d793
  2e8b022 HEAD@{4}: commit: 添加了三行
  16ffdfa HEAD@{5}: commit (initial): 添加gitTest文件
  ```

### 合并commit

> 合并多个commit为一个完整commit。可以对某一段线性提交历史进行编辑、删除、复制、粘贴；因此，合理使用rebase命令可以使我们的提交历史干净、简洁！参考：<https://www.jianshu.com/p/4a8f4af4e803>

- 命令

  ```shell
   git rebase -i  [startpoint]  [endpoint]
  ```

  其中`-i`的意思是`--interactive`，即弹出交互式的界面让用户编辑完成合并操作，`[startpoint]`  `[endpoint]`则指定了一个编辑区间，如果不指定`[endpoint]`，则该区间的终点默认是当前分支`HEAD`所指向的`commit`(注：该区间指定的是一个前开后闭的区间)。

  比如我们需要合并最后三次的commit，在查看到了log日志后，我们就可以使用命令：

  ```shell
  git rebase -i 版本号 #git rebase -i HEAD~3 
  ```

  此时会弹出文件编辑，出现三行类似`pick 16ffdfa  添加gitTest文件`要我们进行修改，其中`pick`表示指令类型，git 为我们提供了以下几个命令：

  > pick：保留该commit（缩写:p）
  >
  > reword：保留该commit，但我需要修改该commit的注释（缩写:r）
  >
  > edit：保留该commit, 但我要停下来修改该提交(不仅仅修改注释)（缩写:e）
  >
  > squash：将该commit和前一个commit合并（缩写:s）
  >
  > fixup：将该commit和前一个commit合并，但我不要保留该提交的注释信息（缩写:f）
  >
  > exec：执行shell命令（缩写:x）
  >
  > drop：我要丢弃该commit（缩写:d）

  根据我们的需求，第一行不做修改，第二、三行将pick修改为 s后保存退出。

  此时弹出文件编辑，要求我们输入commit注释信息，即合并后的commit注释信息，修改完后保存退出完成commit的合并。

### 版本回退

> 参考：<https://blog.csdn.net/yxlshk/article/details/79944535>

#### 回退(reset)

**适用场景：** 如果想恢复到之前某个提交的版本，且那个版本之后提交的版本我们都不要了，就可以用这种方法。

- 版本回退到指定版本（<span style="color:red">ps：版本号就是git log信息中commit 后面的一大串内容</span>）

  > 指将内容commit后需要进行回退的操作

  ```shell
  git reset --hard 版本号
  ```

  例如：

  ```shell
  $ git reset --hard 16ffdfa48ccc31bdb11d66ad9a552bc56d90d793
  HEAD is now at 16ffdfa 添加gitTest文件
  ```

  这时候通过git log 只有一条记录（添加gitTest文件）

- 版本回退到前面几个版本

  <span style="color:red">如果使用windows的cmd控制台，^会被识别为换行符（如：`git reset --hard "HEAD^"`），建议加上引号或使用~</span>

  ```shell
  git reset --hard HEAD~版本数/HEAD^
  ```

  例如：(回退到上一个版本)

  ```shell
  git reset --hard HEAD~1 #或者git reset --hard HEAD^
  ```

  又例如：（回退到上上一个版本）

  ```shell
  git reset --hard HEAD~2 #或者git reset --hard HEAD^^
  ```

- reset后的push

<span style="color:red">reset到指定版本之后，git log就会发现在其之后的commit记录就不存在了。</span>

此时，如果使用`git push`命令推送则会报错，因为本地库HEAD指向的版本比远程库的要旧。所以，<span style="color:red">这里需要使用命令`git push -f`强制推送（多人协作情况下不建议使用，因为这样会丢失commit记录，并且如果远程设置当前分支为保护分支是无法推送的）</span>

#### 反做(revert)

> revert 是回滚某个commit ，不是回滚“到”某个commit

**原理：** git revert是用于“反做”某一个版本，以达到撤销该版本的修改的目的。比如，我们commit了三个版本（版本一、版本二、 版本三），突然发现版本二不行（如：有bug），想要撤销版本二，但又不想影响撤销版本三的提交，就可以用 git revert 命令来反做版本二，生成新的版本四，这个版本四里会保留版本三的东西，但撤销了版本二的东西。如下图所示：

<img src="_images/E0MjA1ODE2MTg4.png" />

**适用场景：** 如果我们想撤销之前的某一版本，但是又想保留该目标版本后面的版本，记录下这整个版本变动流程，就可以用这种方法。

- 反做指定版本

  ```shell
  git revert 版本号 # git revert 16ffdfa48ccc31bdb11d66ad9a552bc56d90d793
  ```

  通过`git log`会发现多了一条commit记录。（<span style="color:red">ps：revert几次就会产生几条commit记录，将多条commit记录合并可使用`git rebase `命令</span>）


### 撤销修改

- git status命令

  通过这个命令可以查看git中文件状态，比如一个文件已经提交到本地仓库，再对本地文件修改等操作，这时执行git status命令，文件名显示为<span style="color:red">红色</span>，如下：

  <img src="_images/1547695749454.png" />

  表示文件还没有add到暂存区。这时执行add提交到暂存区后，文件名显示为<span style="color:green">绿色</span>，如下：

  <img src="_images/1547695948063.png" />

  表示文件还没有commit到工作区。这时执行commit提交到工作区后，该文件的提示消失，如下：

    <img src="_images/1547696048022.png" />  

  此时文件已经提交到本地仓库。

- 丢弃未进入暂存区的修改（还没有放到暂存区，回到和版本库一模一样的状态）

  当修改文件，还未add，希望可以回到未更改前的状态。<span style="color:red">只回退修改的文件，不会删除新增的文件</span>：

  ```shell
  git checkout -- filaname #单个被修改文件的回退
  git checkout -- *        #所有被修改文件的回退
  ```
  当新增文件，还未add,希望可以删除这些文件。<span style="color:red">只删除新增的文件，不会回退修改的文件</span>：

  ```shell
  git clean -n  #显示将要删除的 文件
  git clean -f  #删除文件
  git clean -df #删除文件和目录
  ```

- 丢弃add暂存区的修改

  当修改文件后，通过git add <file>命令后，文件存在本地暂存区中。这时希望撤销本次对暂存区的提交，<span style="color:red">撤销后回到未add的状态</span>。可通过下面的命令：

  ```shell
  git reset HEAD 文件名 #单个文件
  git reset HEAD .     #所有文件
  ```

- 丢弃commit版本库的修改

  通过commit命令之后，文件已经更新到本地仓库中，也就是已经更新到版本库中，在本地`git log`已经可以查看到提交日志。这时希望撤销本次对工作区的提交，<span style="color:red">撤销后变成add之后，commit之前状态</span>。可通过下面的命令：

  ```shell
  git reset --soft  HEAD^ 
  ```

------

## git diff

- **git diff HEAD**：显示工作目录(已track但未add文件)和暂存区(已add但未commit文件)与最后一次commit（同样也可以用`~`或`^`来表示前几个版本）之间的的所有不相同文件的增删改。
- **git diff <分支名1> <分支名2>** ：比较两个分支上最后 commit 的内容的差别

## 远程仓库

> 通过gitee或github创建远程仓库并于本地仓库建立连接，将代码和文件托管到远程仓库中。

- 查看远程仓库信息

  ```shell
  git remote -v
  ```

### 与远程仓库建立连接


- 在gitHub或码云上创建一个新的仓库，将远程仓库的git地址复制下来。然后通过命令建立连接，如下：

  ```shell
   git remote add origin url
  ```

  例如：

  ```shell
   git remote add origin https://gitee.com/yyc007/test-demo.git
  ```

### 将代码提交到远程仓库

- 提交到远程仓库的命令：

  ```shell
  git push -u origin master
  ```

  第一次提交会弹出输入用户名和密码。如果提示错误：

    <img src="_images/1547703364588.png" />  

  就需要先执行一次：

  ```shell
  git pull --rebase origin master
  ```

  这是因为远程仓库上不是空的（比如：码云上新建仓库默认有一个ReadMe.md文件），需要先pull远程仓库到本地保持同步，如下效果如下图：

  <img src="_images/1547703118476.png" />  

​	然后再push到远程仓库。  

  <img src="_images/1547703593611.png" />  

### 远程仓库克隆

- 将远程仓库直接克隆到本地

  ```shell
  git clone url
  ```

- 克隆仓库中指定分区到本地

  ```shell
  git clone -b 分支名 地址
  ```

  例如：克隆dev分支到本地

  ```shell
  git clone -b dev http://192.168.221.188/root/PFM_pc_ui.git
  ```

### 本地关联远程分支

使用git在本地新建一个分支（或者将远程仓库克隆到本地）后，需要做远程分支关联。

关联目的是在执行git pull, git push操作时就不需要指定对应的远程分支，你只要没有显示指定，git pull的时候，就会提示你。

```shell
git branch --set-upstream-to=origin/remote_branch  your_branch
```

其中，origin/remote_branch是你本地分支对应的远程分支；your_branch是你当前的本地分支。

例如，本地dev分支关联远程dev分支：

```shell
git branch --set-upstream-to=origin/dev  dev
```

### 分支管理

- 分支查看

  ```shell
  git branch
  ```

- 创建分支

  ```shell
  git branch <name>  # git branch dev
  ```


- 切换分支

  ```shell
  git checkout <name>
  ```

- 创建+切换分支组合命令

  ```shell
  git checkout -b <name>
  ```

- 删除分支

  ```shell
  git branch -d <name>
  ```

- 合并分支

  ```shell
  git merge <name>
  ```

- git fetch命令（fetch是将远程主机的最新内容拉到本地，不进行合并）

  ```
  git fetch origin master
  ```

　　- - 解读：git fetch命令用于从另一个存储库下载对象和引用

　　　　　　git fetch <远程主机名>

　　　　　　要更新所有分支，命令可以简写为：

　　　　　　$ git fetch

　　　　　　如果只想取回特定分支的更新，可以指定分支名

　　　　　　$ git fetch <远程主机名> <分支名>

　　　　　　比如，取回origin主机的master分支。

　　　　　　$ git fetch origin master

### 多人协作


- 从远程抓取分支（pull 则是将远程主机的master分支最新内容拉下来后与当前本地分支直接合并 fetch+merge）

  ```shell
  git pull
  ```

- 推送分支

  将建立好的分支推送到远程仓库中

  ```shell
  git push origin <branch-name>
  ```

  例如：

  ```shell
   git push origin dev
  ```

  效果图：

  <img src="_images/1547705187446.png" />  

  <img src="_images/1547705238814.png" />  
  
  

## 其它

### 多人协作代码行统计

> 参考：https://my.oschina.net/u/3412738/blog/1631452
> https://blog.csdn.net/chengye5643/article/details/100707293

- 指定时间段指定人提交的代码行数统计

```bash
git log --author=yyc --since=2018-01-01 --until=2020-06-25 --pretty=tformat: --numstat | awk '{ add += $1; subs += $2; loc += $1 - $2 } END { printf "added lines: %s, removed lines: %s, total lines: %s\n", add, subs, loc }'
```

- 提交次数排名前 5的仓库提交者(如果看全部，去掉 head 管道即可)

```bash
git log --pretty='%aN' | sort | uniq -c | sort -k1 -n -r | head -n 5
```

- 指定时间段每个人的代码行数统计

```bash
git log --format='%aN' | sort -u | while read name; do echo -en "$name\t"; git log --since ==2017-11-07 --until==2020-07-08 --author="$name" --pretty=tformat: --numstat | awk '{ add += $1; subs += $2; loc += $1 + $2 } END { printf "added lines: %s, removed lines: %s, total lines: %s\n", add, subs, loc }' -; done
```


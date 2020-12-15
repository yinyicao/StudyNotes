# Docker笔记

## 虚拟机与Linux容器

都是带环境安装软件的解决方案。

 用户可以通过虚拟机还原软件的原始环境 ，缺点： **（1）资源占用多** ； **（2）冗余步骤多** ； **（3）启动慢** 

 **Linux 容器不是模拟一个完整的操作系统，而是对进程进行隔离。**  **（1）启动快** ； **（2）资源占用少** ； **（3）体积小** 

## 什么是Docker？

**Docker 属于 Linux 容器的一种封装，提供简单易用的容器使用接口。**它是目前最流行的 Linux 容器解决方案。

Docker 将应用程序与该程序的依赖，打包在一个文件里面。运行这个文件，就会生成一个虚拟容器。程序在这个虚拟容器里运行，就好像在真实的物理机上运行一样。有了 Docker，就不用担心环境问题。

总体来说，Docker 的接口相当简单，用户可以方便地创建和使用容器，把自己的应用放入容器。容器还可以进行版本管理、复制、分享、修改，就像管理普通的代码一样。 

## Docker image

**Docker 把应用程序及其依赖，打包在 image 文件里面。**只有通过这个文件，才能生成 Docker 容器。image 文件可以看作是容器的模板。Docker 根据 image 文件生成容器的实例。同一个 image 文件，可以生成多个同时运行的容器实例。 

```shell
# 列出本机的所有 image 文件。
$ docker image ls

# 删除 image 文件
$ docker image rm [imageName]

# 抓取 image 文件
$ docker image pull [imageName] # eg: docker image pull hello-world
```

image 文件是通用的，一台机器的 image 文件拷贝到另一台机器，照样可以使用。一般来说，为了节省时间，我们应该尽量使用别人制作好的 image 文件，而不是自己制作。即使要定制，也应该基于别人的 image 文件进行加工，而不是从零开始制作。 

## Docker  container

**image 文件生成的容器实例，本身也是一个文件，称为容器文件。**也就是说，一旦容器生成，就会同时存在两个文件： image 文件和容器文件。而且关闭容器并不会删除容器文件，只是容器停止运行而已。

```shell
# 列出本机正在运行的容器
$ docker container ls

# 列出本机所有容器，包括终止运行的容器
$ docker container ls --all

# 停止指定的容器运行
$ docker container kill [containID]

# 删除已停止运行的容器文件
$ docker container rm [containerID]
```

- <span id="bash">容器生成docker container run</span>

具有自动抓取 image 文件的功能。本地没有指定的 image 文件，就从仓库自动抓取

```shell
# 从 image 文件生成容器（每执行一次新建一个容器）并运行
$ docker container run [imageName]	
```

```shell
$ docker container run -p 8000:3000 -it koa-demo /bin/bash
# 或者
$ docker container run -p 8000:3000 -it koa-demo:0.0.1 /bin/bash
```

上面命令的各个参数含义如下：

> - `-p`参数：容器的 3000 端口映射到本机的 8000 端口。
> - `-it`参数：容器的 Shell 映射到当前的 Shell，然后你在本机窗口输入的命令，就会传入容器。
> - `koa-demo:0.0.1`：image 文件的名字（如果有标签，还需要提供标签，默认是 latest 标签）。
> - `/bin/bash`：容器启动以后，内部第一个执行的命令。这里是启动 Bash，保证用户可以使用 Shell。

### 其它命令

**（1）docker container start**

前面的`docker container run`命令是新建容器，每运行一次，就会新建一个容器。同样的命令运行两次，就会生成两个一模一样的容器文件。如果希望重复使用容器，就要使用`docker container start`命令，它用来<span style="color:red">启动已经生成、已经停止运行的容器文件</span>。

```shell
$ docker container start [containerID]
```

**（2）docker container stop**

前面的`docker container kill`命令终止容器运行，相当于向容器里面的主进程发出 SIGKILL 信号。而`docker container stop`命令也是用来终止容器运行，相当于向容器里面的主进程发出 SIGTERM 信号，然后过一段时间再发出 SIGKILL 信号。

```shell
$ docker container stop [containerID]
```

这两个信号的差别是，应用程序收到 SIGTERM 信号以后，可以自行进行收尾清理工作，但也可以不理会这个信号。如果收到 SIGKILL 信号，就会强行立即终止，那些正在进行中的操作会全部丢失。

**（3）docker container logs**

`docker container logs`命令用来查看 docker 容器的输出，即容器里面 Shell 的标准输出。如果`docker run`命令运行容器的时候，没有使用`-it`参数，就要用这个命令查看输出。

```bash
$ docker container logs [containerID]
```

**（4）docker container exec**

`docker container exec`命令用于进入一个正在运行的 docker 容器。如果`docker run`命令运行容器的时候，没有使用`-it`参数，就要用这个命令进入容器。一旦进入了容器，就可以在容器的 Shell 执行命令了。

```bash
$ docker container exec -it [containerID] /bin/bash
```

**（5）docker container cp**

`docker container cp`命令用于从正在运行的 Docker 容器里面，将文件拷贝到本机。下面是拷贝到当前目录的写法。

```bash
$ docker container cp [containID]:[/path/to/file] .
```

## Docker Dockerfile

 *Dockerfile*是一个包含用于生成镜像(就是前面说到的Docker image)的命令的文本文档。  Docker通过读取`Dockerfile`中的指令自动生成image。 

`docker build`命令用于从`Dockerfile`构建image。可以在`docker build`命令中使用`-f`标志指向文件系统中任何位置的`Dockerfile`。

```shell
$ docker build -f /path/to/a/Dockerfile
```

### Dockerfile文件说明

说明不区分大小写，但必须遵循建议使用大写字母的约定。

Docker以从上到下的顺序运行Dockerfile的指令。为了指定基本image，第一条指令必须是*FROM*。

一个声明以`＃`字符开头则被视为注释。可以在Docker文件中使用`RUN`，`CMD`，`FROM`，`EXPOSE`，`ENV`等指令。

在这里列出了一些常用的说明。

#### **FROM**

该指令用于设置后续指令的基本映像。有效的Dockerfile必须使用`FROM`作为其第一条指令。

```shell
FROM java:8 #说明后续命令需要java环境且是java8版本
```

#### **LABEL**

可以为映像添加标签来组织项目的映像。需要使用*LABEL*指令设置映像的标签。

```shell
LABEL vendorl = "YiiBai"
```

#### **COPY**

该指令用于将当前目录下文件或目录复制到容器image的文件系统。

```shell
COPY . /var/www/java #将当前目录下的所有文件复制到image的/var/www/java文件夹下
```

#### **RUN**

该指令用于执行当前image的任何命令，并且将命令执行后生成的结果文件都打包进 image 文件中。

```shell
RUN javac Hello.java #执行编译Hello.java命令并将编译后的class文件打包进入 image 文件
```

#### **CMD**

这用于指定容器启动后需要执行的命令。`RUN`命令在 image 文件的构建阶段执行，执行结果都会打包进入 image 文件；`CMD`命令则是在容器启动后执行。另外，一个 Dockerfile 可以包含多个`RUN`命令，但是只能有一个`CMD`命令。使用多个*CMD*，则只会执行最后一个*CMD*。

注意，指定了`CMD`命令以后，`docker container run`命令就不能附加命令了（比如<a href="#bash">前面的`/bin/bash`</a>），否则它会覆盖`CMD`命令。

```shell
CMD ["java", "Hello"] #表示容器启动后运行Hello.class
```

#### **WORKDIR**

*WORKDIR*用于为*Dockerfile*中的`RUN`，`CMD`和`COPY`指令设置工作目录。如果工作目录不存在，它默认将会创建。

我们可以在*Dockerfile*文件中多次使用`WORKDIR`。

## Docker docker-compose
**compose**
官方文档：https://docs.docker.com/compose/

**docker-compose文件说明**

官方文档：[Compose file version 3 reference | Docker Documentation](https://docs.docker.com/compose/compose-file/)

**dockerfile和docker-compose**

有些教程用了 `dockerfile+docker-compose`, 是因为 `docker-compose.yml` 本身没有镜像构建的信息，如果镜像是从` docker registry` 拉取下来的，那么 `Dockerfile` 就不需要；如果镜像是需要 build 的，那就需要提供 `Dockerfile`。

## 切换国内镜像源

> docker切换镜像源

编辑daemon.json文件，重启即可。

```
vim /etc/docker/daemon.json
```

加入以下镜像源内容：

```json
{
"registry-mirrors": [ "https://1nj0zren.mirror.aliyuncs.com",
        "https://docker.mirrors.ustc.edu.cn",
        "http://f1361db2.m.daocloud.io",
        "https://registry.docker-cn.com"]
}
```

重启docker

```shell
systemctl restart docker
```


## 实践：使用Docker搭建WordPress

> 这里没有使用Dockerfile
>
> 需要用到两个image。
>
> 1. [MySQL5.7](https://hub.docker.com/_/mysql)
>
> 2. [WordPress]( https://hub.docker.com/_/wordpress )

1. 首先，新建并启动 MySQL 容器。

```bash
$ docker container run \
  -d \
  --rm \
  --name wordpressdb \
  --env MYSQL_ROOT_PASSWORD=123456 \
  --env MYSQL_DATABASE=wordpress \
  mysql:5.7
```

 上面的命令会基于 下载MySQL 的 image 文件（5.7版本）并新建一个容器。该命令的五个命令行参数的含义如下。

> - `-d`：容器启动后，在后台运行。
> - `--rm`：容器终止运行后，自动删除容器文件。
> - `--name wordpressdb`：容器的名字叫做`wordpressdb`
> - `--env MYSQL_ROOT_PASSWORD=123456`：向容器进程传入一个环境变量`MYSQL_ROOT_PASSWORD`，该变量会被用作 MySQL 的根密码。
> - `--env MYSQL_DATABASE=wordpress`：向容器进程传入一个环境变量`MYSQL_DATABASE`，容器里面的 MySQL 会根据该变量创建一个同名数据库（本例是`WordPress`）。

2. 然后，基于官方的 WordPress image，新建并启动 WordPress 容器。 

```shell
 $ docker container run \
  -d \
  -p 8080:80 \
  --rm \
  --name wordpress \
  --env WORDPRESS_DB_PASSWORD=123456 \
  --link wordpressdb:mysql \
  --volume "$PWD/wordpress":/var/www/html \
  wordpress
```

跟上一次相比，该命令其余四个命令行参数的含义如下。

> -  `-p 8080:80`：将容器的 80 端口映射到宿主机的`8080`端口。  127.0.0.2:8080:80 也可以，但是在我这台机子上没成功
> - `--env WORDPRESS_DB_PASSWORD=123456` ： MySQL 容器的根密码 
> - `--link wordpressdb:mysql`： WordPress 容器要连到`wordpressdb`容器，冒号表示该容器的别名是`mysql` 
> - `--volume "$PWD/wordpress":/var/www/html`： 将容器的`/var/www/html`目录映射到当前目录（当前命令执行的目录）的`wordpress`子目录。 

3. 最后，浏览器访问`127.0.0.1:8080`就能看到 WordPress 的安装提示了 ，由于我这里是用的阿里云CentOS主机没有浏览器，所以只能开放8080端口远程访问了。

## 实践：使用Docker Dockerfile执行JAVA HELLO

> 使用Docker Dockerfile生成一个JAVA版的`Hello World！`image并运行

1. 创建一个目录

随便在哪儿创建一个目录即可，主要是为了放后面的文件。

```shell
mkdir -p /www/server/panel/plugin/docker/java-docker-app
```
创建完成后，直接进入这个目录，为了直接在这个目录下创建后续文件。

```shell
cd /www/server/panel/plugin/docker/java-docker-app
```

2. 创建一个Java文件

现在创建一个Java文件，将此文件保存为`Hello.java`。这个 `Hello.java` 的代码内容如下 - 

```java
class Hello{  
    public static void main(String[] args){  
        System.out.println("This is first java app \n by using Docker");  
    }  
}
```

将其保存在文件`Hello.java`中，并放置在 `/www/server/panel/plugin/docker/java-docker-app` 目录下。

3. 创建一个Dockerfile文件

创建Java文件后，还需要创建一个Dockerfile文件，其中包含了Docker的说明。 Dockerfile不包含任何文件扩展名。 所以这个文件简单使用`Dockerfile`作为名称保存即可。此 *Dockerfile* 文件的内容如下 - 

```shell
FROM java:8
COPY . /var/www/java  
WORKDIR /var/www/java  
RUN javac Hello.java  
CMD ["java", "Hello"]
```

依照惯例，所有指令要使用大写字母编写。将此文件放在`/www/server/panel/plugin/docker/java-docker-app`目录中，与`Hello.java`放在同一个目录。

4. 构建Docker image

在此之前，我们在`/www/server/panel/plugin/docker/java-docker-app`目录中新建了两个文件，如下：

```shell
[root@yyc java-docker-app]# ll
total 8
-rw-r--r-- 1 root root 105 May 15 13:34 Dockerfile
-rw-r--r-- 1 root root 146 May 15 13:32 Hello.java
```

现在就可以构建image了。

```shell
$ docker image build -t java-demo . #可以简写为：docker build -t java-demo .
# 或者
$ docker image build -t java-demo:0.0.1 .
```

上面代码中，`-t`参数用来指定 image 文件的名字，后面还可以用冒号指定标签。如果不指定，默认的标签就是`latest`。最后的那个点表示 Dockerfile 文件所在的路径，本例是当前路径，所以是一个点。

注意：本例中，需要依赖基础image java8，所以如果当前docker image中没有java8会自动pull。

如果运行成功，就可以看到新生成的 image 文件`java-demo`了。

```shell
[root@yyc java-docker-app]# docker build -t java-demo .
Sending build context to Docker daemon  3.072kB
Step 1/5 : FROM java:8
8: Pulling from library/java
5040bd298390: Pull complete 
fce5728aad85: Pull complete 
76610ec20bf5: Pull complete 
60170fec2151: Pull complete 
e98f73de8f0d: Pull complete 
11f7af24ed9c: Pull complete 
49e2d6393f32: Pull complete 
bb9cdec9c7f3: Pull complete 
Digest: sha256:c1ff613e8ba25833d2e1940da0940c3824f03f802c449f3d1815a66b7f8c0e9d
Status: Downloaded newer image for java:8
 ---> d23bdf5b1b1b
Step 2/5 : COPY . /var/www/java
 ---> 5f33b29ef3b5
Step 3/5 : WORKDIR /var/www/java
 ---> Running in 2b94730237e3
Removing intermediate container 2b94730237e3
 ---> 9d0ba8fd12eb
Step 4/5 : RUN javac Hello.java
 ---> Running in 06c2e055f701
Removing intermediate container 06c2e055f701
 ---> 5326872e95ed
Step 5/5 : CMD ["java", "Hello"]
 ---> Running in de9632f0ad73
Removing intermediate container de9632f0ad73
 ---> 2da527554d75
Successfully built 2da527554d75
Successfully tagged java-demo:latest
[root@yyc java-docker-app]# docker image ls
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
java-demo           latest              2da527554d75        16 seconds ago      643MB
java                8                   d23bdf5b1b1b        3 years ago         643MB
```

5. 运行Docker image

 成功创建映像后 现在可以使用`run`命令运行docker。以下命令用于运行`java-demo`。 

```shell
[root@yyc java-docker-app]# docker run java-demo
This is first java app 
 by using Docker
[root@yyc java-docker-app]#
```

## 实践：使⽤Docker Compose编排WordPress博客

编写`docker-compose.yaml`文件：

```yaml
# 用docker-compose部署WordPress博客
version: "3.1"
services:
  wordpress:
    image: wordpress:5.5.3-php7.2-apache
    environment:
      WORDPRESS_DB_HOST: mysql
      WORDPRESS_DB_USER: wordpress
      WORDPRESS_DB_PASSWORD: wordpresspassword
      WORDPRESS_DB_NAME: wordpress
    ports:
      - "83:80"  
    depends_on: 
      - mysql  
  mysql:
    image: mysql:5.7.32
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: wordpress
      MYSQL_USER: wordpress
      MYSQL_PASSWORD: wordpresspassword
    volumes:
      - ./mysql-data:/var/lib/mysql
```

在yaml文件同目录下，使用`docker-compose up`即可启动。

## 参考

- [官方参考API](https://docs.docker.com/reference/)：各种命令都在这儿咯
- [官方文档-安装Docker](https://docs.docker.com/engine/install/centos/)
- [官方文档-启动Docker](https://docs.docker.com/config/daemon/systemd/)
- [官方文档-Best practices for writing Dockerfiles](https://docs.docker.com/develop/develop-images/dockerfile_best-practices/)
- [官方文档-Compose file version 3 reference | Docker Documentation](https://docs.docker.com/compose/compose-file/)
- [阮一峰—Docker 入门教程](http://www.ruanyifeng.com/blog/2018/02/docker-tutorial.html)
- [阮一峰—Docker 微服务教程](http://www.ruanyifeng.com/blog/2018/02/docker-wordpress-tutorial.html)
- [易百教程—Docker Dockerfile](https://www.yiibai.com/docker/docker-dockerfile.html)
- [易百教程—Docker Java应用程序示例 ](https://www.yiibai.com/docker/docker-java-example.html)
- [dockerfile 与 docker-compose的区别_Allen技术小站-CSDN博客](https://blog.csdn.net/londa/article/details/91815208)
# 工作负载

> 工作负载是在 Kubernetes 上运行的应用程序。
>
> 无论你的负载是单一组件还是由多个一同工作的组件构成，在 Kubernetes 中你 可以在一组 [Pods](https://kubernetes.io/zh/docs/concepts/workloads/pods) 中运行它。 在 Kubernetes 中，Pod 代表的是集群上处于运行状态的一组 [容器](https://kubernetes.io/zh/docs/concepts/overview/what-is-kubernetes/#why-containers)。
>
> Pod 有确定的生命周期。例如，一旦某 Pod 在你的集群中运行，Pod 运行所在的 [节点](https://kubernetes.io/zh/docs/concepts/architecture/nodes/) 出现致命错误时， 所有该节点上的 Pods 都会失败。Kubernetes 将这类失败视为最终状态： 即使节点后来恢复正常运行，你也需要创建新的 Pod。
>
> 不过，为了让用户的日子略微好过一些，你并不需要直接管理每个 Pod。 相反，你可以使用 *负载资源* 来替你管理一组 Pods。 这些资源配置 [控制器](https://kubernetes.io/zh/docs/concepts/architecture/controller/) 来确保合适类型的、处于运行状态的 Pod 个数是正确的，与你所指定的状态相一致。
>
> 这些工作负载资源包括：
>
> - [Deployment](https://kubernetes.io/zh/docs/concepts/workloads/controllers/deployment/) 和 [ReplicaSet](https://kubernetes.io/zh/docs/concepts/workloads/controllers/replicaset/) （替换原来的资源 [ReplicationController](https://kubernetes.io/zh/docs/reference/glossary/?all=true#term-replication-controller)）；
> - [StatefulSet](https://kubernetes.io/zh/docs/concepts/workloads/controllers/statefulset/);
> - 用来运行提供节点本地支撑设施（如存储驱动或网络插件）的 Pods 的 [DaemonSet](https://kubernetes.io/zh/docs/concepts/workloads/controllers/daemonset/)；
> - 用来执行运行到结束为止的 [Job](https://kubernetes.io/zh/docs/concepts/workloads/controllers/job/) 和 [CronJob](https://kubernetes.io/zh/docs/concepts/workloads/controllers/cron-jobs/)。

## 对象

kubectl命令行工具支持创建和管理Kubernetes对象的几种不同方式。

1. 命令行式方式：如`kubectl create deployment nginx --image nginx`

2. 命令式配置文件方式：	

  - 创建在配置文件中定义的对象：

    ```shell
    kubectl create -f nginx.yaml
    ```

- 删除两个配置文件中定义的对象：

  ```shell
  kubectl delete -f nginx.yaml -f redis.yaml
  ```

- 通过覆盖实时配置来更新配置文件中定义的对象：

  ```shell
  kubectl replace -f nginx.yaml
  ```

3. 声明式方式：

- 例如：处理configs目录中的所有对象配置文件，并创建或修补活动对象。 您可以先进行比较以查看将要进行的更改，然后应用：

  ```shell
  kubectl diff -f configs/
  kubectl apply -f configs/
  ```

- 递归处理目录:

  ```shell
  kubectl diff -R -f configs/
  kubectl apply -R -f configs/
  ```

以上方式着重比较命令式配置方式和声明式方式的区别：

对于命令式请求（比如，kubectl replace ）： 一次只能处理一个写请求，否则会有产生冲突的可能 （替换更新）。

对于声明式请求（比如，kubectl apply ） ： 一次能处理多个写操作，并且具备 Merge 能力 （增量更新）

更多区别可访问官方文档：https://kubernetes.io/docs/concepts/overview/working-with-objects/object-management/

## Pod

Pod是Kubernetes中可以创建和管理的最⼩部署单元。是K8s中最⼩的调度单位，可以认为是⼀个“虚拟机”，Pod是⼀个逻辑概念。

Pod由⼀个或多个容器（例如Docker容器）组成的容器组，组内容器具有共享存储、⽹络，以及容器的运⾏规范。 Pod的内容始终是被同时调度。 Pod可被认为是运⾏特定应⽤程序的“逻辑主机”——它包含⼀个或多个相对紧密耦合的应⽤容器——在容器启动之前，应⽤容器总是会被调度到在相同的Node上。

一般使用诸如 [Deployment](https://kubernetes.io/zh/docs/concepts/workloads/controllers/deployment/) 或 [Job](https://kubernetes.io/zh/docs/concepts/workloads/controllers/job/) 这类工作负载资源 来创建 Pod。如果 Pod 需要跟踪状态， 可以考虑 [StatefulSet](https://kubernetes.io/zh/docs/concepts/workloads/controllers/statefulset/) 资源。

## Names

集群中的每个对象都有一个名称(Name)，该名称(Name)对于该类型的资源是唯一的。

例如，在同一命名空间(namespace)中只能有一个名为 myapp-1234的 Pod，但是可以有一个 Pod 和一个名为 myapp-1234的 Deployment。

对于非唯一的用户提供的属性，Kubernetes 提供了标签和注释( labels、annotations)。

下面是一个名为 nginx-demo 的 Pod 的清单示例。

```yaml
apiVersion: v1
kind: Pod
metadata:
  name: nginx-demo
spec:
  containers:
  - name: nginx
    image: nginx:1.14.2
    ports:
    - containerPort: 80
```

## Namespaces

Kubernetes 支持由同一物理集群支持的多个虚拟集群，这些虚拟集群称为namespaces。

**使⽤多个Namespace的场景**

Namespace旨在⽤于这种环境：有许多的⽤户，这些⽤户分布在多个团队/项⽬中。对于只有⼏个或⼏⼗个⽤户的集
群，您根本不需要创建或考虑使⽤Namespace。 当您需要使⽤Namespace提供的功能时，再考虑使⽤Namespace。

**使⽤Namespace**
Namespace的创建和删除在 Admin Guide documentation for namespaces 有描述。
**查看Namespace**
可使⽤如下命令列出集群中当前的Namespace：

```shell
kubectl get namespaces
```

Kubernetes初始有两个Namespace：

- default ：对于没有其他Namespace的对象的默认Namespace
- kube-system ：由Kubernetes系统所创建的对象的Namespace

## Labels and Selectors

**Label**

Label是附加到对象（如Pod）的键值对。Label旨在⽤于指定对⽤户有意义的对象的识别属性，但不直接表示核⼼系统
的语义。Label可⽤于组织和选择对象的⼦集。Label可在创建时附加到对象，也可在创建后随时添加和修改。每个对象
都可定义⼀组Label。 对于给定的对象，Key必须唯⼀。

```yaml
"metadata": {
  "labels": {
    "key1" : "value1",
    "key2" : "value2"
  }
}
```

**selectors**

不同于Names，Label不提供唯⼀性。⼀般来说，多个可对象携带相同的Label。
通过Label选择器 ，客户端/⽤户可识别⼀组对象。Label选择器是Kubernetes中的核⼼分组API。
API⽬前⽀持两种类型的选择器：equality-based 和 set-based 。Label选择器可由逗号分隔的多个需求组成。在多重需
求的情况下，必须满⾜所有需求，因此逗号作为AND逻辑运算符。

- equality-based

  允许通过标签键(Key)和值(Value)进行过滤。匹配对象必须满足所有指定的标签约束，尽管它们可能还有其他标签。允许使用三种运算符 =、== 、!=。前两个代表相等(而且只是同义) ，而后两个代表不相等。例如:

  ```
  environment = production
  tier != frontend
  ```

  前者选择所有与key = environment 并且value = production 的资源。后者选择key = tier 并且value不等
  于 frontend ，以及key不等于 tier 的所有资源。

- set-based

  允许根据一组值设置过滤键。支持三种操作符: in、 notin 和 exists (只有Key标识符)。例如:

  ```
  environment in (production, qa)
  tier notin (frontend, backend)
  partition
  !partition
  ```

  第⼀个示例选择Key等于 environment ，Value等于 production 或 qa 的所有资源。
  第⼆个示例选择Key等于 tier ，Value不等于 frontend 或 backend ，以及所有Key不等于 tier 的所有资源。
  第三个示例选择Key包含 partition 所有资源；不检查Value的值。
  第四个示例选择Key不包含 partition 的所有资源，不检查任何值。
  类似地，逗号可⽤作AND运算符。 因此可⽤`partition,environment notin (qa)` 过滤` Key = partition `(⽆论值）并
  且 `environment != qa` 的资源。 基于集合的Label选择器，也可表示基于等式的Label选择。例
  如， `environment=production` 等同于 `environment in (production)` ; 同样， != 等同于 notin 。
  Set-based requirement可以与equality-based requirement混合使⽤。例如： `partition in (customerA,
  customerB),environment!=qa`。

**使用**

- 两种Label选择器都可⽤于在REST客户端中LIST或WATCH资源。例如，使⽤ kubectl 定位 apiserver 并使⽤
  equality-based 的⽅式可写为：
  `kubectl get pods -l environment=production,tier=frontend`
  或使⽤set-based requirements：
  `kubectl get pods -l 'environment in (production),tier in (frontend)'`
  如上所示，set-based requirement表达能⼒更强。例如，他们可以对Value执⾏OR运算符：
  `kubectl get pods -l 'environment in (production, qa)'`
  或通过exists操作符，实现restricting negative matching：
  `kubectl get pods -l 'environment,environment notin (frontend)'`

- 使⽤Label选择器定义 service 指向的⼀组Pod。类似地， replicationcontroller 所管理的Pod总数也可⽤Label选择
  器定义。
  两个对象的Label选择器都使⽤map在 json 或 yaml ⽂件中定义，只⽀持equality-based requirement选择器：

    ```yaml
  "selector": {
      "component" : "redis",
  }
    ```

    或：

    ```yaml
  selector:
       component: redis
    ```

  此选择器（分别以 json 或 yaml 格式）等价于 component=redis 或 component in (redis) 。

- 较新的资源（例如 Job 、 Deployment 、 Replica Set 以及 Daemon Set ）也⽀持 set-based requirement。

  ```yaml
  selector:
    matchLabels:
      component: redis
    matchExpressions:
      - {key: tier, operator: In, values: [cache]}
      - {key: environment, operator: NotIn, values: [dev]}
  ```

  `matchLabels `是 `{ key,value } `的映射。 `matchLabels` 映射中的单个 `{ key,value } `等价于 `matchExpressions `⼀个元
  素，其 key 为“key”， operator 为“In”， values 数组仅包含“value”。 `matchExpressions` 是⼀个Pod选择器需求列
  表。有效运算符包括`In、NotIn、Exists`以及`DoesNotExist`。 当使⽤In和NotIn时，Value必须⾮空。所有来
  ⾃ `matchLabels` 和 `matchExpressions` 的需求使⽤AND串联，所有需求都满⾜才能匹配。

## Nodes

node 是Kubernetes中的⼯作机器（worker），以前被称为 minion 。 集群中的Node可以是VM或物理机。通常，集群中有多个节点在学习或资源有限的环境中，可能只有一个节点。每个Node上都有运⾏ pods 所必要的服务，并由Master组件管理。Node上的服务包括Docker、kubelet和kube-proxy。有关更多详细信息，请参阅架构设计⽂档中的 [The Kubernetes Node](https://git.k8s.io/community/contributors/design-proposals/architecture/architecture.md#the-kubernetes-node)部分。

Node不是由Kubernetes创建的：它是由Google Compute Engine等云提供商在外部创建的，或存在于物理机或虚拟机池中，所以我们没必要特别关注这个东西。这意味着当Kubernetes创建⼀个Node时，它只是创建⼀个表示Node的对象。创建后，Kubernetes将检查Node是否有效。

#  资源清单格式

> 关于资源清单个人理解：就是工作负载资源（ReplicaSet、Deployments、StatefulSets、DaemonSet等）配置文件.yaml。

```yaml
#定义k8s版本，不同版本下面会挂载不同的资源
#如果没有给定 group 名称，那么默认为 core，可以使用 kubectl api-versions 获取当前 k8s 版本上所有的 apiVersion 版本信息( 每个版本可能不同 )
apiVersion: group/apiversion 
 
#资源类别
kind:   

#资源元数据
metadata：  
  # 资源名称
  name: 
#规格/期望的状态（disired state）
spec: 
  # 容器[1-n个]
  containers:
    # 容器名称
    - name: 
      # 容器镜像
      image: 
      # 下载镜像的策略
      imagePullPolicy: 
  # 重启策略
  restartPolicy: Always
```

# 工作负载资源（资源控制器）

## 概述

- Kubernetes 中内建了很多 controller（控制器），这些相当于一个状态机，用来**控制 Pod 的具体状态和行为**

## 控制器类型

### ReplicationController 和 ReplicaSet

- 确保任何时间都有指定数量的 Pod 副本在运行，即如果有容器异常退出，会自动创建新的 Pod 来替代，而如果异常多出来的容器也会自动回收；
- 在新版本的 Kubernetes 中建议使用 ReplicaSet 来取代 ReplicationController 。ReplicaSet 跟ReplicationController 没有本质的不同，只是名字不一样，并且 ReplicaSet 支持集合式的 selector（标签 ）；
- Deployment 是一个更高级的概念，它管理 ReplicaSet，并向 Pod 提供声明式的更新以及许多其他有用的功能，<span style="color:red">建议使用 Deployment 而不是直接使用 ReplicaSet</span>，除非需要自定义更新业务流程或根本不需要更新。这意味着你可能永远不需要操作 ReplicaSet 对象，而是直接使用 Deployment。

### Deployment

- Deployment 为 Pod 和 ReplicaSet 提供了一个**声明式定义** (declarative) 方法，用来替代以前的ReplicationController 来方便的管理应用。典型的应用场景包括
  - ①　定义 Deployment 来创建 Pod 和 ReplicaSet
  - ②　滚动升级和回滚应用
  - ③　扩容和缩容
  - ④　暂停和继续 Deployment
- 滚动更新：会创建一个新副本的rs1，旧的rs的pod减少一个时，rs1会新加一个，直到全部增减完成
- 声明式尽量用apply

### StatefulSet 

- StatefulSet 作为 Controller 为 Pod 提供唯一的标识。它可以保证部署和 scale 的顺序，和 Deployment 不同的是， StatefulSet 为它们的每个 Pod 维护了一个有粘性的 ID。这些 Pod 是基于相同的规约来创建的， 但是不能相互替换：无论怎么调度，每个 Pod 都有一个永久不变的 ID。

- StatefulSet是为了解决**有状态**服务的问题（对应Deployments和ReplicaSets是为无状态服务而设计），其应用场景包括：

  - 稳定的持久化存储，即Pod重新调度后还是能访问到相同的持久化数据，基于PVC来实现
  - 稳定、唯一的网络标志，即Pod重新调度后其PodName和HostName不变，基于Headless Service（即没有Cluster IP的Service）来实现
  - 有序的、优雅的部署和缩放，即Pod是有顺序的，在部署或者扩展的时候要依据定义的顺序依次进行（即从0到N-1（或从N-1到0），在下一个Pod运行之前所有之前的Pod必须都是Running和Ready状态），基于init containers来实现
  - 有序的、自动的滚动更新
- 如果应⽤程序不需要任何稳定的标识符或有序部署、删除或缩放，则应该使⽤提供⽆状态副本的Controller部署应⽤。 诸如 Deployment 或者 ReplicaSet 可能更适合您的⽆状态需求。

### DaemonSet

- 确保全部（或者一些）Node 上运行**一个** Pod 的副本。当有 Node 加入集群时，也会为他们新增一个Pod 。当有 Node 从集群移除时，这些 Pod 也会被回收。删除 DaemonSet 将会删除它创建的所有 Pod

- 使用 DaemonSet 的一些**典型用法**：

  - ①　运行集群存储 daemon，例如在每个 Node 上运行glusterd、ceph
  - ②　在每个 Node 上运行日志收集 daemon，例如fluentd、logstash
  - ③　在每个 Node 上运行监控 daemon，例如Prometheus Node Exporter、collectd、Datadog 代理、New Relic 代理，或 Ganglia gmond

### Job

- job负责批处理任务，即仅执行一次的任务，它保证批处理任务的一个或多个pod成功结束

### CronJob

- **管理基于时间的 Job，即**：
  - 在给定时间点只运行一次
  - 周期性地在给定时间点运行
- **使用前提条件**：

- 当前使用的 Kubernetes 集群，版本 >= 1.8（对 CronJob）。对于先前版本的集群，版本 <1.8，启动 API Server时，通过传递选项--runtime-config=batch/v2alpha1=true可以开启 batch/v2alpha1API

- **典型的用法**：
  - 在给定的时间点调度 Job 运行

  - 创建周期性运行的 Job，例如：数据库备份、发送邮件

## 控制器实例应用

### RS / RC

> ReplicaSet和ReplicationController，一般都不直接操作RS/RC，而是通过Deployment

**核心字段**

- replicas	\<integer>：指定期望的Pod对象副本数量
- selector	\<Object>：当前控制器匹配Pod对象副本的标签选择器，支持matchLabels和matchExpressions两种匹配机制
- template	\<Object>：用于定义Pod时的Pod资源信息
- minReadySeconds	\<integer>：用于定义Pod启动后多长时间为可用状态，默认为0秒

**创建**

(1) 命令行查看ReplicaSet清单定义规则

```shell
[root@k8s-master-01 ~]# kubectl explain rs
[root@k8s-master-01 ~]# kubectl explain rs.spec
[root@k8s-master-01 ~]# kubectl explain rs.spec.template
```

(2) 创建ReplicaSet示例

```shell
vi replicaset.yaml 
```

<details> <summary>replicaset.yaml文件信息（点击展开）</summary>

```yaml
#api版本定义
apiVersion: apps/v1
#定义资源类型为ReplicaSet
kind: ReplicaSet
#元数据定义
metadata:
  name: nginx
  namespace: default
  labels:
    app: nginx
#ReplicaSet的规格定义
spec:
  #定义副本数量为3个
  replicas: 3
  #标签选择器，定义匹配Pod的标签
  selector:
    matchLabels:
      app: nginx
  #Pod的模板定义
  template:
    #Pod的元数据定义
    metadata:
      #自定义Pod的名称
      name: my-nginx
      #定义Pod的标签，需要和上面的标签选择器内匹配规则中定义的标签一致，可以多出其他标签
      labels:
        app: nginx
    #Pod的规格定义
    spec:
      #容器定义
      containers:
          #容器名称
        - name: nginx-container
          #容器镜像
          image: nginx:1.18.0
          #拉取镜像的规则
          imagePullPolicy: IfNotPresent
          #暴露端口
          ports:
              #端口名称
            - name: http
              containerPort: 80
      restartPolicy: Always
```

</details>    

(3) 创建上面定义的daemonset控制器

```shell
kubectl apply -f replicaset.yaml
```

(3) 查看验证

```shell
kubectl get rs    #查看创建的ReplicaSet控制器
kubectl get pods -o wide
```

**扩容和缩容**

可以直接通过`vim` 编辑清单文件修改`replicas`字段，也可以通过`kubect edit` 命令去编辑。`kubectl`还提供了一个专用的子命令`scale`用于实现应用规模的伸缩，支持从资源清单文件中获取新的目标副本数量，也可以直接在命令行通过`“--replicas”`选项进行读取。

修改Pod的副本数量

```shell
kubectl edit rs nginx
#kubectl edit rs [rs名字]
```

修改：`replicas: 4`

保存退出后，查看验证:

```shell
kubectl get rs -o wide
kubectl get pods --show-labels
```

**删除**

```shell
#删除rs,并删除管理的pod
#kubectl delete replicasets [rs名字] / kubectl delete rs [rs名字]
kubectl delete replicasets nginx

#删除rs,不删除管理的pod “--cascade=false”选项，取消级联关系。
#kubectl delete replicasets [rs名字] --cascade=false
```

### Deployment

Deployment其核心资源和ReplicaSet相似。

**创建**

(1) 命令行查看ReplicaSet清单定义规则

```shell
kubectl explain deployment
kubectl explain deployment.spec
kubectl explain deployment.spec.template
```

(2) 创建Deployment示例

```shell
vi deployment.yaml
```

<details> <summary>deployment.yaml文件信息（点击展开）</summary>

```yaml
apiVersion: apps/v1  #api版本定义
kind: Deployment  #定义资源类型为Deploymant
metadata:  #元数据定义
  name: nginx-deploy  #deployment控制器名称
  namespace: default  #名称空间
spec:  #deployment控制器的规格定义
  replicas: 2  #定义deployment副本数量为2个
  selector:  #标签选择器，定义匹配Pod的标签
    matchLabels:
      app: nginx-deploy
  template:  #Pod的模板定义
    metadata:  #Pod的元数据定义
      labels:  #定义Pod的标签，和上面的标签选择器标签一致，可以多出其他标签
        app: nginx-deploy
    spec:  #Pod的规格定义
      containers:  #容器定义
        - name: nginx  #容器名称
          image: nginx:1.18.0  #容器镜像
          ports:  #暴露端口
            - name: http  #端口名称
              containerPort: 80
```

</deatils>    

(3) 创建Deployment对象

```shell
kubectl apply -f deployment.yaml --record
#--record 可以方便的查看revision的变化
```

(4) 查看资源对象

```shell
kubectl get deployment    	#查看Deployment资源对象
kubectl get rs    			#查看ReplicaSet资源对象
kubectl get pods    		#查看Pod资源对象
```

**更新升级**

修改`Pod`模板相关的配置参数便能完成`Deployment`控制器资源的更新。由于是声明式配置，因此对`Deployment`控制器资源的修改尤其适合使用`apply`和`patch`命令来进行；如果仅只是修改容器镜像，`“set image”`命令更为易用。

- 通过`set image`命令将上面创建的`Deployment`对象的镜像版本改为`1.19.6`版本

```shell
kubectl set image deployment/nginx-deploy nginx=nginx:1.19.6
#kubectl set image deployment/[deployment名称] [容器名称]=[镜像名称]


#升级完成再次查看rs的情况，以下可以看到原的rs作为备份，而现在启动的是新的rs
[root@k8s-master-01 ~]# kubectl set image deployment/nginx-deploy nginx=nginx:1.19.6
deployment.apps/nginx-deploy image updated
[root@k8s-master-01 k8s]# kubectl get rs
NAME                      DESIRED   CURRENT   READY   AGE
nginx-deploy-5b877f9666   2         2         2       10m
nginx-deploy-bdfc8b95     0         0         0       63m

#通过查看deployment的详细信息可以看到镜像已经更改
kubectl get deployment -o wide

#或者以自定义的方式查看image版本
kubectl get pods -o custom-columns=Name:metadata.name,Image:spec.containers[0].image
```

**扩容**

```shell
#方式1：kubectl scale命令扩容
kubectl scale deployment nginx-deploy --replicas 10
#kubectl scale deployment 【deployment名称】 --replicas 10

#方式2：直接修改配置清单方式进行扩容
#kubectl edit deployment nginx-deploy

#方式3：使用kubectl patch打补丁的方式进行扩容
#kubectl patch deployment nginx-deploy -p '{"spec":{"replicas":5}}'
```

**回滚**

```shell
#回到上一个版本
kubectl rollout undo deployment/nginx-deploy

#查看版本
kubectl get pods -o custom-columns=Name:metadata.name,Image:spec.containers[0].image

#通过该命令查看更新历史记录
[root@k8s-master-01 k8s]# kubectl rollout history deployment/nginx-deploy
deployment.apps/nginx-deploy 
REVISION  CHANGE-CAUSE
1         kubectl apply --filename=deployment.yaml --record=true
2         kubectl apply --filename=deployment.yaml --record=true

#回滚到指定版本
kubectl rollout undo deployment/nginx-deploy --to-revision=2
```

### StatefulSet

StatefulSet⽬前需要⼀个 [Headless Service](https://kubernetes.io/docs/concepts/services-networking/service/#headless-services) 负责Pod的⽹络身份

**创建**

```shell
vi statefulset.yaml
```

<details> <summary>statefulset.yaml文件信息（点击展开）</summary>


```yaml
apiVersion: v1
kind: Service
metadata:
  name: statefulset-nginx
spec:
  selector:
    app: nginx
  ports:
    - port: 80
      targetPort: 80
      # 表示这是一个headlessService
  clusterIP: None
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: nginx
  labels:
    app: nginx
spec:
  replicas: 3
  serviceName: statefulset-nginx
  template:
    metadata:
      name: nginx
      labels:
        app: nginx
    spec:
      containers:
        - name: nginx
          image: nginx:1.18.0
          imagePullPolicy: IfNotPresent
          # glusterfs / ceph..
      restartPolicy: Always
  selector:
    matchLabels:
      app: nginx
```

</details>    

同Deployment一样，使用如下命令启动：

```shell
kubectl create -f statefulset.yaml --record
```

会返回如下格式：

```
service/statefulset-nginx created
statefulset.apps/nginx created
```

可以通过执行`kubectl get StatefulSet`以检查是否已创建部署。

通过`kubectl get pods`查看部署的pod状态。

### DaemonSet

**创建**

(1) 定义清单文件

```shell
vi daemonset.yaml
```

<details> <summary>daemonset.yaml文件信息（点击展开）</summary>

```yaml
apiVersion: apps/v1             #api版本定义
kind: DaemonSet                 #定义资源类型为DaemonSet
metadata:                       #元数据定义
  name: daemset-nginx           #daemonset控制器名称
  namespace: default            #名称空间
  labels:                       #设置daemonset的标签
    app: daem-nginx
spec:                           #DaemonSet控制器的规格定义
  selector:                     #指定匹配pod的标签
    matchLabels:                #指定匹配pod的标签
      app: daem-nginx           #注意：这里需要和template中定义的标签一样
  template:                     #Pod的模板定义
    metadata:                   #Pod的元数据定义
      name: nginx
      labels:                   #定义Pod的标签，需要和上面的标签一致，可以多出其他标签
        app: daem-nginx
    spec:                       #Pod的规格定义
      containers:               #容器定义
        - name: nginx-pod       #容器名字
          image: nginx:1.18.0   #容器镜像
          ports:                #暴露端口
            - name: http        #端口名称
              containerPort: 80 #暴露的端口
```

</details>    

(2) 创建上面定义的daemonset控制器

```shell
kubectl apply -f daemonset.yaml
```

(3) 查看验证
```yaml
#查看验证
kubectl get pods -o wide
kubectl describe daemonset/daemset-nginx
```

### Job

**创建**

(1) 定义清单文件

```shell
vi job.yaml
```

<details> <summary>daemonset.yaml文件信息（点击展开）</summary>

```yaml
apiVersion: apps/v1             #api版本定义
kind: DaemonSet                 #定义资源类型为DaemonSet
metadata:                       #元数据定义
  name: daemset-nginx           #daemonset控制器名称
  namespace: default            #名称空间
  labels:                       #设置daemonset的标签
    app: daem-nginx
spec:                           #DaemonSet控制器的规格定义
  selector:                     #指定匹配pod的标签
    matchLabels:                #指定匹配pod的标签
      app: daem-nginx           #注意：这里需要和template中定义的标签一样
  template:                     #Pod的模板定义
    metadata:                   #Pod的元数据定义
      name: nginx
      labels:                   #定义Pod的标签，需要和上面的标签一致，可以多出其他标签
        app: daem-nginx
    spec:                       #Pod的规格定义
      containers:               #容器定义
        - name: nginx-pod       #容器名字
          image: nginx:1.18.0   #容器镜像
          ports:                #暴露端口
            - name: http        #端口名称
              containerPort: 80 #暴露的端口
```

</details>    

(2) 创建上面定义的daemonset控制器

```shell
kubectl apply -f daemonset.yaml
```

(3) 查看验证
```yaml
#查看验证
kubectl get pods -o wide
kubectl describe daemonset/daemset-nginx
```


### CronJob
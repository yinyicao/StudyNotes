# 服务（Services）

> 将运行在一组 [Pods](https://kubernetes.io/docs/concepts/workloads/pods/pod-overview/) 上的应用程序公开为网络服务的抽象方法。
>
> 使用 Kubernetes，你无需修改应用程序即可使用不熟悉的服务发现机制。 Kubernetes 为 Pods 提供自己的 IP 地址，并为一组 Pod 提供相同的 DNS 名， 并且可以在它们之间进行负载均衡。
>
> 创建和销毁 Kubernetes [Pod](https://kubernetes.io/docs/concepts/workloads/pods/pod-overview/) 以匹配集群状态。 Pod 是非永久性资源。 如果你使用 [Deployment](https://kubernetes.io/zh/docs/concepts/workloads/controllers/deployment/) 来运行你的应用程序，则它可以动态创建和销毁 Pod。
>
> 每个 Pod 都有自己的 IP 地址，但是在 Deployment 中，在同一时刻运行的 Pod 集合可能与稍后运行该应用程序的 Pod 集合不同。
>
> 这导致了一个问题： 如果一组 Pod（称为“后端”）为集群内的其他 Pod（称为“前端”）提供功能， 那么前端如何找出并跟踪要连接的 IP 地址，以便前端可以使用工作量的后端部分？可⽤ Services 来解决该问题。

<img src="_images/k8s/1455597-20190923163900738-1242117836.png" />

## 定义Service

> 官方文档：<https://kubernetes.io/zh/docs/concepts/services-networking/service/#%E5%AE%9A%E4%B9%89-service>

Service 在 Kubernetes 中是一个 REST 对象，和 Pod 类似。 像所有的 REST 对象一样，Service 定义可以基于 `POST` 方式，请求 API server 创建新的实例。 Service 对象的名称必须是合法的 [DNS 标签名称](https://kubernetes.io/zh/docs/concepts/overview/working-with-objects/names#dns-label-names)。

例如，在`K8S工作负载`中有一组Deployment Pod，它们对外暴露了 80 端口，同时还被打上 `app=nginx-deploy` 标签：

```yaml
apiVersion: v1
kind: Service
metadata:
  name: nginx-service
spec:
  type: NodePort # 为该Service开启NodePort⽅式的外⽹访问模式
  selector:
    app: nginx-deploy
  ports:
    - protocol: TCP # 默认使用TCP协议，另外还支持UDP,SCTP等
      port: 80 # Service提供服务的端⼝号
      targetPort: 80 # 将Service的80端⼝转发到Pod中容器的80端⼝上
      nodePort: 30001 # 在k8s集群外访问的端⼝，如果设置了NodePort类型，但没设置nodePort，将会随机映射⼀个端⼝，可使⽤kubectl get svc nginx-service看到
```

上述配置创建一个名称为 "nginx-service" 的 Service 对象，它会将请求代理到使用 TCP 端口 80，并且具有标签 `"app=nginx-deploy"` 的 Pod 上。

服务选择算符的控制器不断扫描与其选择器匹配的 Pod（这里就会扫描到被打上 `app=nginx-deploy` 标签的Pod），然后将所有更新发布到也称为 “nginx-service” 的 Endpoint 对象。

## Service的类型

上述定义的Service中，`type: NodePort`表示发布服务的服务类型为NodePort，还有`ClusterIP(默认)`，`ExternalName`等四种类型，他们之间的区别可以直接查看官方文档：<https://kubernetes.io/zh/docs/concepts/services-networking/service/#publishing-services-service-types>：

**ClusterIP**：（默认），自动分配一个仅集群的内部可以访问的虚拟IP。

**[NodePort](https://kubernetes.io/zh/docs/concepts/services-networking/service/#nodeport)**：通过每个节点上的 IP 和静态端口（`NodePort`）暴露服务。 `NodePort` 服务会路由到自动创建的 `ClusterIP` 服务。 通过请求 `<节点 IP>:<节点端口>`，你可以从集群的外部访问一个 `NodePort` 服务。

**[LoadBalancer](https://kubernetes.io/zh/docs/concepts/services-networking/service/#loadbalancer)**：（了解就行），使用云提供商的负载均衡器向外部暴露服务。 外部负载均衡器可以将流量路由到自动创建的 `NodePort` 服务和 `ClusterIP` 服务上。

**[ExternalName](https://kubernetes.io/zh/docs/concepts/services-networking/service/#externalname)**：通过返回 `CNAME` 和对应值，可以将服务映射到 `externalName` 字段的内容（例如，`foo.bar.example.com`）。 也就是说可以把集群外部的服务引入到集群内部来，在集群内部直接使用。无需创建任何类型代理。这只有 kubernetes 1.7 或更高版本的 kube-dns 才支持。

## 虚拟 IP 和 Service 代理

在 Kubernetes 集群中，每个 Node 运行一个 `kube-proxy` 进程。 `kube-proxy` 负责为 Service 实现了一种 VIP（虚拟 IP）的形式，而不是 [`ExternalName`](https://kubernetes.io/zh/docs/concepts/services-networking/service/#externalname) 的形式。

在 Kubernetes v1.0 版本，代理完全在 userspace。

在 Kubernetes v1.1 版本，新增了 iptables 代理，但并不是默认的运行模式。

从 Kubernetes v1.2 起，默认就是iptables 代理。

在 Kubernetes v1.8.0-beta.0 中，添加了 ipvs 代理

在 Kubernetes 1.14 版本开始默认使用ipvs 代理

在 Kubernetes v1.0 版本，Service是 “4层”（TCP/UDP over IP）概念。在 Kubernetes v1.1 版本，新增了Ingress API（beta 版），用来表示 “7层”（HTTP）服务

### 为什么不使用 DNS 轮询？

时不时会有人问到为什么 Kubernetes 依赖代理将入站流量转发到后端。那其他方法呢？ 例如，是否可以配置具有多个 A 值（或 IPv6 为 AAAA）的 DNS 记录，并依靠轮询名称解析？

使用服务代理有以下几个原因：

- DNS 实现的历史由来已久，它不遵守记录 TTL，并且在名称查找结果到期后对其进行缓存。
- 有些应用程序仅执行一次 DNS 查找，并无限期地缓存结果。
- 即使应用和库进行了适当的重新解析，DNS 记录上的 TTL 值低或为零也可能会给 DNS 带来高负载，从而使管理变得困难。

简单来说，DNS会在很多的客户端里进行缓存，很多服务在访问DNS进行域名解析完成、得到地址后不会对DNS的解析进行清除缓存的操作，所以一旦有他的地址信息后，不管访问几次还是原来的地址信息，导致负载均衡无效。

### userspace 代理模式

> https://kubernetes.io/zh/docs/concepts/services-networking/service/#proxy-mode-userspace

默认情况下，用户空间模式下的 kube-proxy 通过轮转算法选择后端。

<img src="_images/k8s/services-userspace-overview.png" />

### iptables 代理模式

> https://kubernetes.io/zh/docs/concepts/services-networking/service/#proxy-mode-iptables

默认的策略是，kube-proxy 在 iptables 模式下随机选择一个后端。

<img src="_images/k8s/services-iptables-overview.png" />

### IPVS 代理模式

> https://kubernetes.io/zh/docs/concepts/services-networking/service/#proxy-mode-ipvs
>
> 在 Kubernetes 1.14 版本开始默认使用ipvs 代理	

在 `ipvs` 模式下，kube-proxy 监视 Kubernetes 服务和端点，调用 `netlink` 接口相应地创建 IPVS 规则， 并定期将 IPVS 规则与 Kubernetes 服务和端点同步。 该控制循环可确保IPVS 状态与所需状态匹配。访问服务时，IPVS 将流量定向到后端Pod之一。

IPVS代理模式基于类似于 iptables 模式的 netfilter 挂钩函数， 但是使用哈希表作为基础数据结构，并且在内核空间中工作。 这意味着，与 iptables 模式下的 kube-proxy 相比，IPVS 模式下的 kube-proxy 重定向通信的延迟要短，并且在同步代理规则时具有更好的性能。 与其他代理模式相比，IPVS 模式还支持更高的网络流量吞吐量。

IPVS 提供了更多选项来平衡后端 Pod 的流量。 这些是：

- `rr`：轮替（Round-Robin）
- `lc`：最少链接（Least Connection），即打开链接数量最少者优先
- `dh`：目标地址哈希（Destination Hashing）
- `sh`：源地址哈希（Source Hashing）
- `sed`：最短预期延迟（Shortest Expected Delay）
- `nq`：从不排队（Never Queue）

要在 IPVS 模式下运行 kube-proxy，必须在启动 kube-proxy 之前使 IPVS 在节点上可用。

当 kube-proxy 以 IPVS 代理模式启动时，它将验证 IPVS 内核模块是否可用。 如果未检测到 IPVS 内核模块，则 kube-proxy 将退回到以 iptables 代理模式运行。

<img src="_images/k8s/services-ipvs-overview.png" />
# Ingress

> Ingress: 入口，负责统一管理外部对k8s cluster中service的请求。

## 概述

通过上节[K8S之服务](/其它/编码实践/Kubernetes/K8S之服务)可以了解到k8s 对外暴露服务（service）主要有两种方式：NotePort和LoadBalancer， 此外externalIPs也可以使各类service对外提供服务，但是当集群服务很多的时候，NodePort方式最大的缺点是会占用很多集群机器的端口；LB方式最大的缺点则是每个service一个LB又有点浪费和麻烦，并且需要k8s之外的支持； `而ingress则只需要一个NodePort或者一个LB就可以满足所有service对外服务的需求`。

<img src="_images/k8s/ingress.png" width="60%"/>

## Ingress和Ingress Controller

> `Ingress`是`Kubernetes API`的标准资源类型之一，它其实就是一组基于`DNS`名称（`host`）或`URL`路径把请求转发至指定的`Service`资源的规则，用于将集群外部的请求流量转发至集群内部完成服务发布。然而，`Ingress`资源自身并不能进行“流量穿透”，它仅是一组路由规则的集合，这些规则要想真正发挥作用还需要其他功能的辅助，如监听某套接字，然后根据这些规则的匹配机制路由请求流量。这种能够为`Ingress`资源监听套接字并转发流量的组件称为`Ingress`控制器（`Ingress Controller`）。



> `Ingress`控制器并不直接运行为`kube-controller-manager`的一部分，它是`Kubernetes`集群的一个重要组件，类似`CoreDNS`，需要在集群上单独部署。

<img src="_images/k8s/ingress-controller.png" width="80%"/>

从上图中可以很清晰的看到，实际上请求进来还是被负载均衡器拦截，比如 nginx，然后 Ingress Controller 通过跟 Ingress 交互得知某个域名对应哪个 service，再通过跟 kubernetes API 交互得知 service 地址等信息；综合以后生成配置文件实时写入负载均衡器，然后负载均衡器 reload 该规则便可实现服务发现，即动态映射。

Ingress Controller 会根据你定义的 Ingress 对象，提供对应的代理能力。业界常用的各种反向代理项目，比如 Nginx、HAProxy、Envoy、Traefik 等，都已经为Kubernetes 专门维护了对应的 Ingress Controller。

## 部署Ingress Controller（Nginx）

> `Ingress` 控制器自身是运行于`Pod`中的容器应用，一般是`Nginx`或`Envoy`一类的具有代理及负载均衡功能的守护进程，它监视着来自`API Server`的`Ingress`对象状态，并根据规则生成相应的应用程序专有格式的配置文件并通过重载或重启守护进程而使新配置生效。
>
> `Ingress`控制器其实就是托管于`Kubernetes`系统之上的用于实现在应用层发布服务的`Pod`资源，跟踪`Ingress`资源并实时生成配置规则。

运行为`Pod`资源的`Ingress`控制器进程通过下面两种方式接入外部请求流量：

1. 以`Deployment`控制器管理`Ingress`控制器的`Pod`资源，通过`NodePort`或`LoadBalancer`类型的`Service`对象为其接入集群外部的请求流量，这就意味着，定义一个`Ingress`控制器时，必须在其前端定义一个专用的`Service`资源。

   <img src="_images/k8s/1591236257333-8ec59ad6-37fd-4d80-be59-3f7c20abf96f.png" width="80%"/>

2. 借助于`DaemonSet`控制器，将`Ingress`控制器的`Pod`资源各自以单一实例的方式运行于集群的所有或部分工作节点之上，并配置这类`Pod`对象以`HostPort`（如下图中的a）或`HostNetwork`（如下图中的b）的方式在当前节点接入外部流量。

   <img src="_images/k8s/1591236268701-ab604fc2-f398-49d6-bd1e-13761d3148fe.png" width="80%"/>

### 通过github下载配置清单文件

仓库地址：<https://github.com/kubernetes/ingress-nginx/> 选择对应的版本tag。

比如我选择`helm-chart-3.18.0`这个tag：<https://github.com/kubernetes/ingress-nginx/blob/ingress-nginx-3.15.2/deploy/static/provider/baremetal/deploy.yaml>

点击`Raw`拿到文件地址：<https://raw.githubusercontent.com/kubernetes/ingress-nginx/ingress-nginx-3.15.2/deploy/static/provider/baremetal/deploy.yaml>

然后下载：

```shell
[root@k8s-master-01 ~]# mkdir ingress-nginx   #这里创建一个目录专门用于ingress-nginx(可省略)
[root@k8s-master-01 ~]# cd ingress-nginx/
[root@k8s-master-01 ingress-nginx]# wget https://raw.githubusercontent.com/kubernetes/ingress-nginx/ingress-nginx-3.15.2/deploy/static/provider/baremetal/deploy.yaml #下载配置清单yaml文件
[root@k8s-master-01 ingress-nginx]# ls    #查看下载的文件
deploy.yaml
```

### 准备镜像

由于国内无法从k8s.gcr.io下载镜像，所以需要自己想办法搞镜像。通过查看下载好的deploy.yaml文件，发现只有`k8s.gcr.io/ingress-nginx/controller:v0.42.0`这个镜像需要我们准备一下，其它的都在hub.docker上可以直接完成下载。

我们到阿里镜像仓库去搜一下<https://cr.console.aliyun.com/cn-qingdao/instances>，找到了一个对应版本的镜像。pull下来。

```shell
[root@k8s-master-01 ingress-nginx]# docker pull registry.cn-qingdao.aliyuncs.com/highland2020-containers/ingress-nginx-controller:v0.41.2
v0.41.2: Pulling from highland2020-containers/ingress-nginx-controller
188c0c94c7c5: Pull complete 
... 
6177bf658faf: Pull complete 
Digest: sha256:c7f8c2eaa157cc18f267c6c89a9e659f65ee7b347547a37c6471aff78ca22021
Status: Downloaded newer image for registry.cn-qingdao.aliyuncs.com/highland2020-containers/ingress-nginx-controller:v0.41.2
registry.cn-qingdao.aliyuncs.com/highland2020-containers/ingress-nginx-controller:v0.41.2
```

修改deploy.yaml文件对应的镜像地址：

```shell
#将
image: k8s.gcr.io/ingress-nginx/controller:v0.41.2@sha256:1f4f402b9c14f3ae92b11ada1dfe9893a88f0faeb0b2f4b903e2c67a0c3bf0de
#修改为
image: registry.cn-qingdao.aliyuncs.com/highland2020-containers/ingress-nginx-controller:v0.41.2@sha256:c7f8c2eaa157cc18f267c6c89a9e659f65ee7b347547a37c6471aff78ca22021
```

### 创建部署

```shell
[root@k8s-master-01 ingress-nginx]# kubectl apply -f deploy.yaml 
namespace/ingress-nginx created
serviceaccount/ingress-nginx created
configmap/ingress-nginx-controller created
clusterrole.rbac.authorization.k8s.io/ingress-nginx created
clusterrolebinding.rbac.authorization.k8s.io/ingress-nginx created
role.rbac.authorization.k8s.io/ingress-nginx created
rolebinding.rbac.authorization.k8s.io/ingress-nginx created
service/ingress-nginx-controller-admission created
service/ingress-nginx-controller created
deployment.apps/ingress-nginx-controller created
validatingwebhookconfiguration.admissionregistration.k8s.io/ingress-nginx-admission created
serviceaccount/ingress-nginx-admission created
clusterrole.rbac.authorization.k8s.io/ingress-nginx-admission created
clusterrolebinding.rbac.authorization.k8s.io/ingress-nginx-admission created
role.rbac.authorization.k8s.io/ingress-nginx-admission created
rolebinding.rbac.authorization.k8s.io/ingress-nginx-admission created
job.batch/ingress-nginx-admission-create created
job.batch/ingress-nginx-admission-patch created
```

### 验证

查看生成的pod，注意这里在ingress-nginx名称空间

```shell
[root@k8s-master-01 ingress-nginx]# kubectl get pods -n ingress-nginx 称空间
NAME                                       READY   STATUS      RESTARTS   AGE
ingress-nginx-admission-create-bwgvp       0/1     Completed   0          6m44s
ingress-nginx-admission-patch-jxprc        0/1     Completed   0          6m44s
ingress-nginx-controller-d44bb6955-7gzl4   1/1     Running     0          6m44s
```

查看该pod的详细信息

```shell
[root@k8s-master-01 ingress-nginx]# kubectl describe pod ingress-nginx-controller-d44bb6955-7gzl4 -n ingress-nginx
Name:         ingress-nginx-controller-d44bb6955-7gzl4
Namespace:    ingress-nginx
Priority:     0
Node:         k8s-node-02/172.26.11.141
Start Time:   Tue, 05 Jan 2021 14:20:26 +0800
Labels:       app.kubernetes.io/component=controller
              app.kubernetes.io/instance=ingress-nginx
              app.kubernetes.io/name=ingress-nginx
              pod-template-hash=d44bb6955
Annotations:  <none>
Status:       Running
IP:           10.244.2.34
IPs:
  IP:           10.244.2.34
Controlled By:  ReplicaSet/ingress-nginx-controller-d44bb6955
...
```

## 实例一：使用Ingress发布Nginx

> 该示例中创建的所有资源都位于新建的`testing`名称空间中。与其他的资源在逻辑上进行隔离，以方便管理。

<img src="_images/k8s/1591236309341-35fd8ed2-e84c-4efa-a874-87ca87c8bc49.png" width="80%"/>

1.首先创建一个单独的目录为了方便管理

```shell
[root@k8s-master-01 ~]# mkdir ingress-nginx/ingress
[root@k8s-master-01 ~]# cd ingress-nginx/ingress/
```

2.创建`testing`名称空间(也可以使用命令直接创建`# kubectl create namespace my-namespace`，不过这里使用资源清单格式创建)

```shell
[root@k8s-master-01 ingress]# vim namespace-testing.yaml #编写namespace清单文件
apiVersion: v1
kind: Namespace
metadata:
  name: testing
  labels:
    env: testing
                                                                                                                                                                          
[root@k8s-master-01 ingress]# kubectl apply -f namespace-testing.yaml #创建namespace
namespace/testing created
[root@k8s-master-01 ingress]# kubectl get namespace testing #验证
NAME      STATUS   AGE
testing   Active   7s
```

3.部署`nginx`实例，这里使用`Deployment`控制器于`testing`中部署`nginx`相关的`Pod`对象。

```shell
[root@k8s-master-01 ingress]# vim namespace-testing.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: testing
  labels:
    env: testing
                                                                                                                                                                           
[root@k8s-master-01 ingress]# kubectl apply -f namespace-testing.yaml
namespace/testing created
[root@k8s-master-01 ingress]# kubectl get namespace testing
NAME      STATUS   AGE
testing   Active   7s
[root@k8s-master-01 ingress]# vim deployment-nginx.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: deploy-nginx
  namespace: testing
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.18
        ports:
        - name: http
          containerPort: 80                                                                                                                                                            
[root@k8s-master-01 ingress]# kubectl apply -f deployment-nginx.yaml 
deployment.apps/deploy-nginx created
[root@k8s-master-01 ingress]# kubectl get deploy -n testing
NAME           READY   UP-TO-DATE   AVAILABLE   AGE
deploy-nginx   3/3     3            3           70s
[root@k8s-master-01 ingress]# kubectl get pods -n testing
NAME                            READY   STATUS    RESTARTS   AGE
deploy-nginx-7f67947c6f-8chsd   1/1     Running   0          73s
deploy-nginx-7f67947c6f-rzj56   1/1     Running   0          73s
deploy-nginx-7f67947c6f-snrb2   1/1     Running   0          73s
```

4.创建`Service`资源，关联后端的`Pod`资源。这里通过`service`资源`svc-nginx`的`80`端口去暴露容器的`80`端口。

```shell
[root@k8s-master-01 ingress]# vim service-nginx.yaml
apiVersion: v1
kind: Service
metadata:
  name: svc-nginx
  namespace: testing
  labels:
    app: svc-nginx
spec:
  selector:
    app: nginx
  ports:
  - name: http
    port: 80
    targetPort: 80
    protocol: TCP
                                                                                                                                                                          
[root@k8s-master-01 ingress]# kubectl apply -f service-nginx.yaml 
service/svc-nginx created
[root@k8s-master-01 ingress]# kubectl get svc -n testing
NAME        TYPE        CLUSTER-IP     EXTERNAL-IP   PORT(S)   AGE
svc-nginx   ClusterIP   10.97.144.60   <none>        80/TCP    8s
[root@k8s-master-01 ingress]# kubectl describe svc/svc-nginx -n testing
Name:              svc-nginx
Namespace:         testing
Labels:            app=svc-nginx
Annotations:       <none>
Selector:          app=nginx
Type:              ClusterIP
IP:                10.97.144.60
Port:              http  80/TCP
TargetPort:        80/TCP
Endpoints:         10.244.1.37:80,10.244.1.38:80,10.244.2.36:80
Session Affinity:  None
Events:            <none>
```

5.创建`Ingress`资源，匹配`Service`资源`svc-nginx`，并将`svc-nginx`的80端口暴露。

```shell
[root@k8s-master-01 ingress]# vim ingress-nginx.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: nginx
  namespace: testing
  annotations:
    kubernetes.io/ingress.class: "nginx"
spec:
  rules:
  - host: nginx.ilinux.io
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: svc-nginx
            port:
              number: 80
                                                                                                                                                                                 
[root@k8s-master-01 ingress]# kubectl apply -f ingress-nginx.yaml 
ingress.networking.k8s.io/nginx created
[root@k8s-master-01 ingress]# kubectl get ingress -n testing
Warning: extensions/v1beta1 Ingress is deprecated in v1.14+, unavailable in v1.22+; use networking.k8s.io/v1 Ingress
NAME    CLASS    HOSTS             ADDRESS   PORTS   AGE
nginx   <none>   nginx.ilinux.io             80      10s
[root@k8s-master-01 ingress]# kubectl describe ingress -n testing
Warning: extensions/v1beta1 Ingress is deprecated in v1.14+, unavailable in v1.22+; use networking.k8s.io/v1 Ingress
Name:             nginx
Namespace:        testing
Address:          
Default backend:  default-http-backend:80 (<error: endpoints "default-http-backend" not found>)
Rules:
  Host             Path  Backends
  ----             ----  --------
  nginx.ilinux.io  
                   /   svc-nginx:80   10.244.1.37:80,10.244.1.38:80,10.244.2.36:80)
Annotations:       kubernetes.io/ingress.class: nginx
Events:
  Type    Reason  Age   From                      Message
  ----    ------  ----  ----                      -------
  Normal  Sync    17s   nginx-ingress-controller  Scheduled for sync
[root@k8s-master-01 ingress]# 
```

6.测试，通过`Ingress`控制器的前端的`Service`资源的`NodePort`来访问此服务

```shell
[root@k8s-master-01 ingress]# kubectl get svc -n ingress-nginx #首先查看前面部署Ingress控制器的前端的Service资源的映射端口
NAME                                 TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)                      AGE
ingress-nginx-controller             NodePort    10.111.4.136    <none>        80:32183/TCP,443:32127/TCP   76m
ingress-nginx-controller-admission   ClusterIP   10.108.19.135   <none>        443/TCP                      76m
[root@k8s-master-01 ingress]# cat /etc/hosts  #终端测试，添加hosts
127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4
::1         localhost localhost.localdomain localhost6 localhost6.localdomain6

172.26.11.14  k8s-master-01 nginx.ilinux.io
172.26.11.139 k8s-node-01 nginx.ilinux.io
172.26.11.141 k8s-node-02 nginx.ilinux.io
199.232.68.133 raw.githubusercontent.com
[root@k8s-master-01 ingress]# curl nginx.ilinux.io:32183 #访问测试
<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
...
</body>
</html>
```

验证是否调度到后端的`Pod`资源，查看日志

```shell
[root@k8s-master ~]# kubectl get pods -n testing
[root@k8s-master ~]# kubectl logs deploy-nginx-7f67947c6f-8chsd -n testing
```

## 实例二：使用Ingress发布多个服务

### 将不同的服务映射不同的主机上

<img src="_images/k8s/1591236424756-5eae547f-ac23-497f-858b-f42d943f323a.png" width="80%"/>

**准备工作：**这里创建一个目录保存本示例的所有资源配置清单

```shell
[root@k8s-master-01 ~]# mkdir ingress-nginx/multi_svc
[root@k8s-master-01 ~]# cd !$
cd ingress-nginx/multi_svc
[root@k8s-master-01 multi_svc]# 
```

#### 创建名称空间

<br>

# *参考：*

1. 官方文档Services：<https://kubernetes.io/zh/docs/concepts/services-networking/>
2. 官方文档Ingress控制器：<https://kubernetes.io/zh/docs/concepts/services-networking/ingress-controllers/>
3. 官方文档Ingress资源：<https://kubernetes.io/zh/docs/concepts/services-networking/ingress/#the-ingress-resource>
4. ingress-nginx：<https://github.com/kubernetes/ingress-nginx>、<https://kubernetes.github.io/ingress-nginx/>
5. ingress安装文档：<https://kubernetes.github.io/ingress-nginx/deploy/>
6. 服务发现-Ingress：<https://www.yuque.com/ivescode/k8s/rntak1#df368884>
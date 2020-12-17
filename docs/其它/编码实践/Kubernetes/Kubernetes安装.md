# Kubernetes安装

> 旨在记录安装过程以及踩到的一些坑，为下次安装提供便利

## 安装方式对比

> 前提：科学上⽹，或⾃⾏将gcr.io的镜像转成其他镜像仓库的镜像（如阿里云镜像）。

K8S安装非常麻烦，有很多方式可以安装。

| 部署方案                                                     | 优点                                                         | 缺点                                                       |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ---------------------------------------------------------- |
| [Docker Desktop](https://www.docker.com/products/docker-desktop) | 安装方便、可使用[k8s-for-docker-desktop](https://github.com/AliyunContainerService/k8s-for-docker-desktop)这个开源项目下载镜像<br>支持Windows/Mac等 | 单机版、Windows上需要开启Hyper-V                           |
| [Minikube](https://github.com/kubernetes/minikube)           | 适⽤于Windows 10、Linux、macOS                               | 用来测试搞搞还行                                           |
| [Kubeadm](https://github.com/kubernetes/kubeadm)             | 官⽅出品、修改镜像地址方便                                   | 部署较麻烦、不够透明                                       |
| [Kubespray](https://github.com/kubernetes-incubator/kubespray) | 官⽅出品、部署较简单、懂Ansible就能上⼿                      | 修改镜像地址麻烦（需要改源码且需要修改的地方多）、不够透明 |
| [RKE](https://github.com/rancher/rke)                        | 部署较简单                                                   | 需要另外花⼀些时间了解RKE的cluster.yml配置⽂件、不够透明   |
| [⼿动部署-第三⽅操作⽂档](https://github.com/opsnull/follow-me-install-kubernetes-cluster) | 完全透明、可配置、便于理解K8s各组件之间的关系                | 部署⾮常麻烦，容易出错                                     |

## 单机版安装

> 听说Kubernetes 1.20 版本启动Docker，不知道今后还能用不
>
> 关于如何在Windows 10上运⾏Docker和Kubernetes？：http://dockone.io/article/8136

对于macOS或者Windows 10，Docker已经原⽣⽀持了Kubernetes。你所要做的只是在Docker Desktop中启⽤Kubernetes即可。

如果因为⽹络问题安装不成功，可参考 https://github.com/AliyunContainerService/k8s-for-docker-desktop 的说明进⾏
安装。
**TIPS：**
在Windows上:
如果在Kubernetes部署的过程中出现问题，可以在 C:\ProgramData\DockerDesktop下的service.txt 查看Docker⽇志; 如
果看到 Kubernetes⼀直在启动状态，请参考[Issue 3769(comment) ](https://github.com/docker/for-win/issues/3769#issuecomment-486046718)和[Issue 1962(comment)](https://github.com/docker/for-win/issues/1962#issuecomment-431091114)。



同时，单机版的话也可以使用[Minikube](https://github.com/kubernetes/minikube)进行安装。

## Kubeadm方式安装集群

<img src="_images/k8s/1598714983124.png" />

### 前期准备

开始使用本地虚拟机安装，后来一直卡在`kubeadm join`添加节点那里或者是在主节点初始化时报超时错误，折腾了几天，觉得还是环境有问题。所以租了几台云服务器安装。需要注意的是配置<span style="color:red">至少需要2核CPU2GB内存才可以安装，需要内网互通</span>。

|    主机名     |    系统    |     配置     | IPV4(内网)  |   备注    |
| :-----------: | :--------: | :----------: | :---------: | :-------: |
| k8s-master-01 | CentOS 7.6 | 2c 4Gb *50Gb | 172.30.0.5  | k8s主节点 |
|  k8s-node-01  | CentOS 7.6 | 2c 4Gb *50Gb | 172.30.0.11 | k8s从节点 |
|  k8s-node-02  | CentOS 7.6 | 2c 4Gb *50Gb | 172.30.0.17 | k8s从节点 |

我这里拟安装Docker目前最新版（v20.10），kubernetes-1.19.0。似乎版本差异太大是安装不了的。

#### 关闭防火墙启用IPtables

> master、node都需要执行

```shell
# 关闭防火墙
systemctl stop firewalld && systemctl disable firewalld
# 启用IPtables
yum -y install iptables-services &&  systemctl start iptables && systemctl enable iptables  && iptables -F && service iptables save

# 验证firewalld 应该是关闭状态
systemctl status firewalld

# 验证iptables 应该是开启状态
systemctl status iptables
```

#### 主机名/Host文件解析

- 大型环境中，建议通过DNS主机名和ip进行关联

```shell
# 设置主机名：master上执行
hostnamectl set-hostname k8s-master-01

# 设置主机名：node1上执行
hostnamectl set-hostname k8s-node-01

# 设置主机名：node2上执行
hostnamectl set-hostname k8s-node-02

# 查看主机名
hostname
```

- 设置Host解析

```shell
# 在master上执行修改
vim /etc/hosts

# 添加如下内容（根据真实情况修改）
172.30.0.5 k8s-master-01
172.30.0.11 k8s-node-01
172.30.0.17 k8s-node-02

# 拷贝当前文件到其他服务器目录中
scp /etc/hosts root@k8s-node-01:/etc/hosts
scp /etc/hosts root@k8s-node-02:/etc/hosts
```

#### 关闭swap交换分区

> master、node都需要执行

```shell
# 关闭虚拟内存 && 永久关闭虚拟内存(也可以注解掉)
swapoff -a && sed -i '/ swap / s/^\(.*\)$/#\1/g' /etc/fstab

# 确认交换分区是否关闭,都为0表示关闭
free -m
```

#### 关闭selinux虚拟内存

> master、node都需要执行

```shell
## 临时生效 && 永久生效
setenforce 0 && sed -i 's/^SELINUX=.*/SELINUX=disable/' /etc/selinux/config
```

#### *\*集群时间同步配置*

> #### （可选\*一般时间都是一致的，如果不一致需要执行）

```shell
# 选择一个节点作为服务端
#	我们选择master01为时间服务器的服务端，其他的为时间服务器的客户端
# 安装时间服务器
yum install -y chrony
# 编辑配置文件(master)
vi /etc/chrony.conf
# 添加
server 192.168.88.128 iburst
allow 192.168.88.0/24
local stratum 10
# 编辑配置文件(node)
vi /etc/chrony.conf
# 添加
server 192.168.88.128 iburst
# 确认是否可以同步
chronyc sources
# 启动服务
systemctl start chronyd
# 验证启动
ss -unl | grep 123
# 开机启动服务
systemctl enable chronyd

#设置系统时区为中国/上海
#timedatectl set-timezone Asia/Shanghai
#将当前的 UTC 时间写入硬件时钟
#timedatectl set-local-rtc 0
#重启依赖于系统时间的服务
#systemctl restart rsyslog 
#systemctl restart crond
```

#### *\*系统日志保存方式设置*

> #### (可选\*不影响) master、node都需要执行

- 原因：centos7以后，引导方式改为了systemd，所以会有两个日志系统同时工作只保留一个日志（journald）的方法
- 设置rsyslogd 和 systemd journald

```shell
# 持久化保存日志的目录
mkdir /var/log/journal 
mkdir /etc/systemd/journald.conf.d

cat  >  /etc/systemd/journald.conf.d/99-prophet.conf  << EOF
[Journal]
#持久化保存到磁盘
Storage=persistent
# 压缩历史日志
Compress=yes
SyncIntervalSec=5m
RateLimitInterval=30s
RateLimitBurst=1000
# 最大占用空间10G
SystemMaxUse=10G
# 单日志文件最大200M
SystemMaxFileSize=200M
# 日志保存时间 2 周
MaxRetentionSec=2week
# 不将日志转发到 syslog
ForwardToSyslog=no
EOF

#重启journald配置
systemctl restart systemd-journald
```

#### *\*修改yum镜像源*

> (\*可选，但建议设置，这样使用yum安装更快)  master、node都需要执行

```shell
#方法：
# 进入到/etc/yum.repos.d/目录下，备份之前的CentOS-Base.repo地址。
cd /etc/yum.repos.d/
mv CentOS-Base.repo CentOS-Base.repo.bak

# 下载阿里云yum源
wget -O CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo

# (需要一点时间，也可以不执行这个缓存)将服务器上的软件包信息缓存到本地,以提高搜索安装软件的速度
yum makecache
# 如果你在执行上面这边命令时，报错：Error: Cannot retrieve metalink for repository: epel. Please verify its path and try again
# 建议用如下方法解决：检查/etc/yum.repos.d/下是否有epel.repo文件，如果有，重命名为epel.repo_bak 千万不能以.repo格式备份，然后在执行一次上面的命令即可！
# CentOS7对应地址：http://mirrors.aliyun.com/repo/Centos-7.repo
# CentOS8对应地址：http://mirrors.aliyun.com/repo/Centos-8.repo
 
# 更改为网易的centos yum源
# wget http://mirrors.163.com/.help/CentOS6-Base-163.repo
# mv CentOS6-Base-163.repo CentOS-Base.repo
```

#### *\*升级系统内核(如需)*

```shell
# 查看当前内核版本
uname -r

# 升级内核
# 安装 ELRepo 源：
rpm --import https://www.elrepo.org/RPM-GPG-KEY-elrepo.org
rpm -Uvh https://www.elrepo.org/elrepo-release-8.0-2.el8.elrepo.noarch.rpm

# 启用 ELRepo 源仓库：
yum --disablerepo="*" --enablerepo="elrepo-kernel" list available

# 安装新内核：
yum -y --enablerepo=elrepo-kernel install kernel-ml kernel-ml-devel

#------如无意外，最新内核已经安装好。-------

# 修改 grub 配置使用新内核版本启动
# 查看当前默认启动内核：
dnf install grubby
grubby --default-kernel

# 如不是，查看所有内核：
grubby --info=ALL
# 然后指定新内核启动：
grubby --set-default /boot/vmlinuz-5.3.8-1.el8.elrepo.x86_64
```

#### 更新yum环境

> master、node都需要执行

```shell
yum update
```

#### 安装工具包

> master、node都可以安装一下

```shell
yum install -y conntrack  ipvsadm ipset jq iptables curl sysstat libseccomp wget vim net-tools git
```

#### 安装docker软件

> master、node都需要执行

```shell
# docker依赖
yum install -y yum-utils device-mapper-persistent-data lvm2

# 导入阿里云的docker-ce仓库
yum-config-manager  \
--add-repo  \http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo

# centos8的yum库中没有符合最新版docker-ce对应版本的containerd.io，docker-ce-3:19.03.11-3.el7.x86_64需要containerd.io >= 1.2.2-3
# 通过阿里云镜像库安装符合最新docker-ce版本的containerd.io；
yum install -y https://mirrors.aliyun.com/docker-ce/linux/centos/7/x86_64/edge/Packages/containerd.io-1.2.13-3.2.el7.x86_64.rpm

# 安装 
yum -y install docker-ce docker-ce-cli

# 启动 and 开机自启
systemctl start docker && systemctl enable docker

## 查看
systemctl status docker

# 配置镜像加速deamon

tee /etc/docker/daemon.json << EOF
{
  "registry-mirrors": ["https://4bsnyw1n.mirror.aliyuncs.com"],
  "exec-opts":["native.cgroupdriver=systemd"]
}
EOF

# 重启docker
systemctl daemon-reload && systemctl restart docker && systemctl enable docker
```

#### kube-proxy开启ipvs的前置条件

> master、node都需要执行

```shell
# 1、加载netfilter模块
modprobe br_netfilter  

# 2、添加配置文件
cat  >  /etc/sysconfig/modules/ipvs.modules  <<EOF
#!/bin/bash
modprobe  --  ip_vs
modprobe  --  ip_vs_rr
modprobe  --  ip_vs_wrr
modprobe  --  ip_vs_sh
modprobe  --  nf_conntrack_ipv4
EOF

# 3、赋予权限并引导
chmod 755 /etc/sysconfig/modules/ipvs.modules && bash /etc/sysconfig/modules/ipvs.modules && lsmod | grep -e ip_vs -e nf_conntrack_ipv4
```

### 集群安装

#### 安装Kubeadm相关

> master、node都需要执行

- 导入阿里云的YUM仓库

  ```shell
  cat << EOF > /etc/yum.repos.d/kubernetes.repo
  [kubernetes]
  name=Kubernetes
  baseurl=https://mirrors.aliyun.com/kubernetes/yum/repos/kubernetes-el7-x86_64/
  enabled=1
  gpgcheck=1
  repo_gpgcheck=1
  gpgkey=https://mirrors.aliyun.com/kubernetes/yum/doc/yum-key.gpg https://mirrors.aliyun.com/kubernetes/yum/doc/rpm-package-key.gpg
  EOF
  ```

- 在每个节点安装kubeadm、kubectl、kubelet

  ```shell
  # 安装初始化工具、命令行管理工具、与docker的cri交互创建容器kubelet
  yum -y install kubeadm-1.19.0 kubectl-1.19.0 kubelet-1.19.0 --disableexcludes=kubernetes
  
  # 验证安装是否成功
  rpm -aq kubelet kubectl kubeadm
  
  # k8s开机自启
  systemctl enable kubelet.service & systemctl start kubelet.service
  
  ## 查看状态 这个时候应该是activating (auto-restart)状态
  systemctl status kubelet.service
  ```

#### kubeadm config配置

> 仅master上需要执行

```shell
#获取默认初始化配置文件
kubeadm config print init-defaults >init.default.yaml
#保存配置文件名为init-config.yaml备用
cp init.default.yaml  init-config.yaml
```

其中init-config.yaml文件内容如下(<span style="color:red">需要做一些修改</span>)：

```yaml
apiVersion: kubeadm.k8s.io/v1beta2
bootstrapTokens:
- groups:
  - system:bootstrappers:kubeadm:default-node-token
  token: abcdef.0123456789abcdef
  ttl: 24h0m0s
  usages:
  - signing
  - authentication
kind: InitConfiguration
localAPIEndpoint:
  # 修改这个IP为master内网IP地址
  advertiseAddress: 172.30.0.5
  bindPort: 6443
nodeRegistration:
  criSocket: /var/run/dockershim.sock
  name: k8s-master-01
  taints:
  - effect: NoSchedule
    key: node-role.kubernetes.io/master
---
apiServer:
  timeoutForControlPlane: 4m0s
apiVersion: kubeadm.k8s.io/v1beta2
certificatesDir: /etc/kubernetes/pki
clusterName: kubernetes
controllerManager: {}
dns:
  type: CoreDNS
etcd:
  local:
    dataDir: /var/lib/etcd
# 修改拉取镜像的地址    
imageRepository: registry.aliyuncs.com/google_containers
kind: ClusterConfiguration
kubernetesVersion: v1.19.0
networking:
  dnsDomain: cluster.local
  # 新增一个配置
  podSubnet: 10.244.0.0/16
  serviceSubnet: 10.96.0.0/12
scheduler: {}
---
apiVersion: kubeproxy.config.k8s.io/v1alpha1
kind: KubeProxyConfiguration
featureGates:
  SupportIPVSProxyMode: true
mode: ipvs
```

#### 下载kubernetes镜像

> 仅master上需要执行

```shell
#下载镜像，使用上一步创建的配置文件
kubeadm config images pull --config=init-config.yaml
```

等待下载镜像，可以看到打印出拉取的镜像信息

```
[config/images] Pulled k8s.gcr.io/kube-apiserver:v1.19.0
[config/images] Pulled k8s.gcr.io/kube-controller-manager:v1.19.0
[config/images] Pulled k8s.gcr.io/kube-scheduler:v1.19.0
[config/images] Pulled k8s.gcr.io/kube-proxy:v1.19.0
[config/images] Pulled k8s.gcr.io/pause:3.2
[config/images] Pulled k8s.gcr.io/etcd:3.4.9-1
[config/images] Pulled k8s.gcr.io/coredns:1.7.0
```

也可以在Docker中使用命令【docker images】看到这些镜像信息

```
REPOSITORY                                                        TAG       IMAGE ID       CREATED         SIZE
registry.aliyuncs.com/google_containers/kube-proxy                v1.19.0   bc9c328f379c   3 months ago    118MB
registry.aliyuncs.com/google_containers/kube-apiserver            v1.19.0   1b74e93ece2f   3 months ago    119MB
registry.aliyuncs.com/google_containers/kube-controller-manager   v1.19.0   09d665d529d0   3 months ago    111MB
registry.aliyuncs.com/google_containers/kube-scheduler            v1.19.0   cbdc8369d8b1   3 months ago    45.7MB
registry.aliyuncs.com/google_containers/etcd                      3.4.9-1   d4ca8726196c   5 months ago    253MB
registry.aliyuncs.com/google_containers/pause                     3.2       80d28bedfe5d   10 months ago   683kB
```

镜像下载完成后就可以进行安装了

#### 安装master节点

使用上面的配置文件初始化master节点

```shell
kubeadm init --config=init-config.yaml  | tee kubeadm-init.log
```

> 也可以不用配置文件，直接使用命令进行安装：但是推荐使用配置文件，这样可以保存状态（下次我们回过头来看时可以知道配置了些什么，使用命令的话执行了就完了没有存下来）。
>
> ```shell
> kubeadm init --kubernetes-version=v1.19.0 --image-repository registry.aliyuncs.com/google_containers --pod-network-cidr=10.244.0.0/16 --service-cidr=10.96.0.0/12 --ignore-preflight-errors=Swap
> ```
>

生成信息如下：

```
[root@VM-0-5-centos ~]# kubeadm init --config=init-config.yaml  | tee kubeadm-init.log
W1215 09:58:20.813394   13077 configset.go:348] WARNING: kubeadm cannot validate component configs for API groups [kubelet.config.k8s.io kubeproxy.config.k8s.io]
[init] Using Kubernetes version: v1.19.0
[preflight] Running pre-flight checks
        [WARNING SystemVerification]: this Docker version is not on the list of validated versions: 20.10.0. Latest validated version: 19.03
[preflight] Pulling images required for setting up a Kubernetes cluster
[preflight] This might take a minute or two, depending on the speed of your internet connection
[preflight] You can also perform this action in beforehand using 'kubeadm config images pull'
[certs] Using certificateDir folder "/etc/kubernetes/pki"
[certs] Generating "ca" certificate and key
[certs] Generating "apiserver" certificate and key
[certs] apiserver serving cert is signed for DNS names [k8s-master-01 kubernetes kubernetes.default kubernetes.default.svc kubernetes.default.svc.cluster.local] and IPs [10.96.0.1 172.30.0.5]
[certs] Generating "apiserver-kubelet-client" certificate and key
[certs] Generating "front-proxy-ca" certificate and key
[certs] Generating "front-proxy-client" certificate and key
[certs] Generating "etcd/ca" certificate and key
[certs] Generating "etcd/server" certificate and key
[certs] etcd/server serving cert is signed for DNS names [k8s-master-01 localhost] and IPs [172.30.0.5 127.0.0.1 ::1]
[certs] Generating "etcd/peer" certificate and key
[certs] etcd/peer serving cert is signed for DNS names [k8s-master-01 localhost] and IPs [172.30.0.5 127.0.0.1 ::1]
[certs] Generating "etcd/healthcheck-client" certificate and key
[certs] Generating "apiserver-etcd-client" certificate and key
[certs] Generating "sa" key and public key
[kubeconfig] Using kubeconfig folder "/etc/kubernetes"
[kubeconfig] Writing "admin.conf" kubeconfig file
[kubeconfig] Writing "kubelet.conf" kubeconfig file
[kubeconfig] Writing "controller-manager.conf" kubeconfig file
[kubeconfig] Writing "scheduler.conf" kubeconfig file
[kubelet-start] Writing kubelet environment file with flags to file "/var/lib/kubelet/kubeadm-flags.env"
[kubelet-start] Writing kubelet configuration to file "/var/lib/kubelet/config.yaml"
[kubelet-start] Starting the kubelet
[control-plane] Using manifest folder "/etc/kubernetes/manifests"
[control-plane] Creating static Pod manifest for "kube-apiserver"
[control-plane] Creating static Pod manifest for "kube-controller-manager"
[control-plane] Creating static Pod manifest for "kube-scheduler"
[etcd] Creating static Pod manifest for local etcd in "/etc/kubernetes/manifests"
[wait-control-plane] Waiting for the kubelet to boot up the control plane as static Pods from directory "/etc/kubernetes/manifests". This can take up to 4m0s
[apiclient] All control plane components are healthy after 16.003274 seconds
[upload-config] Storing the configuration used in ConfigMap "kubeadm-config" in the "kube-system" Namespace
[kubelet] Creating a ConfigMap "kubelet-config-1.19" in namespace kube-system with the configuration for the kubelets in the cluster
[upload-certs] Skipping phase. Please see --upload-certs
[mark-control-plane] Marking the node k8s-master-01 as control-plane by adding the label "node-role.kubernetes.io/master=''"
[mark-control-plane] Marking the node k8s-master-01 as control-plane by adding the taints [node-role.kubernetes.io/master:NoSchedule]
[bootstrap-token] Using token: abcdef.0123456789abcdef
[bootstrap-token] Configuring bootstrap tokens, cluster-info ConfigMap, RBAC Roles
[bootstrap-token] configured RBAC rules to allow Node Bootstrap tokens to get nodes
[bootstrap-token] configured RBAC rules to allow Node Bootstrap tokens to post CSRs in order for nodes to get long term certificate credentials
[bootstrap-token] configured RBAC rules to allow the csrapprover controller automatically approve CSRs from a Node Bootstrap Token
[bootstrap-token] configured RBAC rules to allow certificate rotation for all node client certificates in the cluster
[bootstrap-token] Creating the "cluster-info" ConfigMap in the "kube-public" namespace
[kubelet-finalize] Updating "/etc/kubernetes/kubelet.conf" to point to a rotatable kubelet client certificate and key
[addons] Applied essential addon: CoreDNS
[addons] Applied essential addon: kube-proxy

Your Kubernetes control-plane has initialized successfully!

To start using your cluster, you need to run the following as a regular user:

  mkdir -p $HOME/.kube
  sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
  sudo chown $(id -u):$(id -g) $HOME/.kube/config

You should now deploy a pod network to the cluster.
Run "kubectl apply -f [podnetwork].yaml" with one of the options listed at:
  https://kubernetes.io/docs/concepts/cluster-administration/addons/

Then you can join any number of worker nodes by running the following on each as root:

kubeadm join 172.30.0.5:6443 --token abcdef.0123456789abcdef \
    --discovery-token-ca-cert-hash sha256:f4b5ae084353e8eed3e30648b74259dae3f7dad2492045e26b52d7cc5bccf3a8
```

按照提示依次执行以下命令：

```shell
mkdir -p $HOME/.kube

sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config

sudo chown $(id -u):$(id -g) $HOME/.kube/config
```

查看证书

```shell
ll /etc/kubernetes/pki
```
可以看到安装了以下的证书：
```
  total 60
  -rw-r--r-- 1 root root 1273 Dec 15 09:58 apiserver.crt
  -rw-r--r-- 1 root root 1135 Dec 15 09:58 apiserver-etcd-client.crt
  -rw------- 1 root root 1679 Dec 15 09:58 apiserver-etcd-client.key
  -rw------- 1 root root 1675 Dec 15 09:58 apiserver.key
  -rw-r--r-- 1 root root 1143 Dec 15 09:58 apiserver-kubelet-client.crt
  -rw------- 1 root root 1675 Dec 15 09:58 apiserver-kubelet-client.key
  -rw-r--r-- 1 root root 1066 Dec 15 09:58 ca.crt
  -rw------- 1 root root 1679 Dec 15 09:58 ca.key
  drwxr-xr-x 2 root root 4096 Dec 15 09:58 etcd
  -rw-r--r-- 1 root root 1078 Dec 15 09:58 front-proxy-ca.crt
  -rw------- 1 root root 1679 Dec 15 09:58 front-proxy-ca.key
  -rw-r--r-- 1 root root 1103 Dec 15 09:58 front-proxy-client.crt
  -rw------- 1 root root 1679 Dec 15 09:58 front-proxy-client.key
  -rw------- 1 root root 1679 Dec 15 09:58 sa.key
  -rw------- 1 root root  451 Dec 15 09:58 sa.pub
```

此时，master主机上便已经安装了kubernetes,但是集群内还是没有可用工作的Node,并缺乏对容器网络的配置。

这里需要注意kubeadm init 命令执行完成后的最后几行提示信息，其中包含加入节点的指令(kubeadm join)和所需的Token

此时可以用kubectl命令验证ConfigMap:

  ```shell
kubectl get -n kube-system configmap
  ```

可以看到其中生成了名为kubeadm-config的configMap对象

```
  NAME                                 DATA   AGE
  coredns                              1      118s
  extension-apiserver-authentication   6      2m1s
  kube-proxy                           2      118s
  kubeadm-config                       2      119s
  kubelet-config-1.19                  1      119s
```

技巧：如果说某次执行`kubeadm init`初始化k8s集群失败了，在下一次执行`kubeadm init`初始化语句之前，先执行`kubeadm reset`命令。这个命令的作用是重置节点，大家可以把这个命令理解为：上一次`kubeadm init`初始化集群操作失败了，该命令清理了之前的失败环境。如若不执行，会出现额外的错误比如xxx already exists和Port xxx is in use等等，所以大家一定要先执行`kubeadm reset`指令。

```shell
# 执行kubeadm init初始化k8s集群失败 再次init时需要执行
kubeadm reset
```

#### 安装Node节点加入集群

> 对于新节点的添加，系统准备、kubernetes yum 源的配置、安装kubeadm/kubectl/kubelet等过程和前面一致的。

- 如果未关闭swap分区则需要配置忽略swap报错

  ```shell
  [root@k8s-node-01 ~]# vim /etc/sysconfig/kubelet
  KUBELET_EXTRA_ARGS="--fail-swap-on=false"
  
  [root@k8s-node-02 ~]# vim /etc/sysconfig/kubelet
  KUBELET_EXTRA_ARGS="--fail-swap-on=false"
  ```

对于Node节点加入Master有两种方式：

- 方式一：直接使用master 初始化后生成的命令在各个node节点上执行即可。

  ```shell
  [root@VM-0-17-centos ~]# kubeadm join 172.30.0.5:6443 --token abcdef.0123456789abcdef \
    > --discovery-token-ca-cert-hash sha256:f4b5ae084353e8eed3e30648b74259dae3f7dad2492045e26b52d7cc5bccf3a8
  ```

  生成如下信息：

  ```
  [preflight] Running pre-flight checks
            [WARNING SystemVerification]: this Docker version is not on the list of validated versions: 20.10.0. Latest validated version: 19.03
    [preflight] Reading configuration from the cluster...
    [preflight] FYI: You can look at this config file with 'kubectl -n kube-system get cm kubeadm-config -oyaml'
    [kubelet-start] Writing kubelet configuration to file "/var/lib/kubelet/config.yaml"
    [kubelet-start] Writing kubelet environment file with flags to file "/var/lib/kubelet/kubeadm-flags.env"
    [kubelet-start] Starting the kubelet
    [kubelet-start] Waiting for the kubelet to perform the TLS Bootstrap...
  
    This node has joined the cluster:
    * Certificate signing request was sent to apiserver and a response was received.
    * The Kubelet was informed of the new secure connection details.
  
    Run 'kubectl get nodes' on the control-plane to see this node join the cluster.
  ```

   然后可以在master上执行`kubectl get nodes`可以发现node节点加进来了（但是是NotReady状态，这是因为没有安装CNI网络插件）。

  如果是后面新加的node节点，可能token会过期（在初始化master时的配置文件中我们有配置有效期为24小时），过期则需要重新生成。
  **在master重新生成token**

  ```shell
  kubeadm token create
  # 424mp7.nkxx07p940mkl2nd
  
  openssl x509 -pubkey -in /etc/kubernetes/pki/ca.crt | openssl rsa -pubin -outform der 2>/dev/null | openssl dgst -sha256 -hex | sed 's/^.* //'
  # d88fb55cb1bd659023b11e61052b39bbfe99842b0636574a16c76df186fd5e0d
  ```

   **Node节点重新join就可以了**

  ```shell
  kubeadm join 172.30.0.5:6443 –token 424mp7.nkxx07p940mkl2nd \
    --discovery-token-ca-cert-hash sha256:d88fb55cb1bd659023b11e61052b39bbfe99842b0636574a16c76df186fd5e0d
  ```

- 方式二：也可以为kubeadm命令生成配置文件。创建文件join-config.yaml,

  获取默认初始化配置文件

  ```shell
  kubeadm config print init-defaults >join-config.yaml
  ```

  内容如下：
  
  ```yaml
  apiVersion: kubeadm.k8s.io/v1beta1
    kind: JoinConfiguration
    discovery:
      bootstrapToken:
        apiServerEndpoint: 172.30.0.5:6443
        token: abcdef.0123456789abcdef
        unsafeSkipCAVerification: true
      tlsBootstrapToken: abcdef.0123456789abcdef
  ```
  
  >   含义解释
  >   apiServerEndpoint：主节点服务器地址
  >   token：主节点初始化安装的最后一行提示的token信息。
  >   tlsBootstrapToken：主节点初始化安装的最后一行提示的token信息。
  
  将本Node节点加入到Master节点
  
  ```shell
  kubeadm join --config=join-config.yaml
  ```
  
- 了解：kubelet在主节点也安装了，默认情况节点Node的kubelet并不参与工作负载，如果你希望安装一个单机ALL-In-One 的Kubernetes环境，则可以执行下面的命令(删除Node的Label "node-role.kubernetes.io/master"),让master成为Node:

    ```shell
    kubectl taint nodes --all node-role.kubernetes.io/master-
    ```

- 查看日志

    ```
    /var/log/message
    ```

#### 安装网络插件(flannel)

当我们使用命令`kubectl get nodes`命令时发现 有提示master节点为NotReady状态，这是因为没有安装CNI网络插件

```shell
[root@VM-0-5-centos ~]# kubectl get nodes
NAME            STATUS     ROLES    AGE     VERSION
k8s-master-01   NotReady   master   8m36s   v1.19.0
k8s-node-01     NotReady   <none>   67s     v1.19.0
k8s-node-02     NotReady   <none>   58s     v1.19.0
```

网络插件有很多，我们安装flannel插件

|  网络插件   | 性能 | 隔离策略 |   开发者   |
| :---------: | :--: | :------: | :--------: |
| kube-router | 最高 |   支持   |            |
|   calico    |  2   |   支持   |            |
|    canal    |  3   |   支持   |            |
|   flannel   |  3   |    无    |   CoreOS   |
|   romana    |  3   |   支持   |            |
|    Weave    |  3   |   支持   | Weaveworks |

#### 安装flannel插件


- 安装方法一：

  ```shell
  kubectl apply -f https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
  ```

- 安装方法二：

  ```shell
  # 先下载
  wget https://raw.githubusercontent.com/coreos/flannel/master/Documentation/kube-flannel.yml
  # 替换仓库地址
  sed -i 's#quay.io#quay-mirror.qiniu.com#g' kube-flannel.yml  
  # 安装
  kubectl apply -f kube-flannel.yml
  ```
  

如果出现kube-flannel.yml 文件下载不了的是因为网站被墙了，建议在/etc/hosts文件添加一条 199.232.68.133 raw.githubusercontent.com 就可以正常下载了，或者直接去github上找个这个文件就好了。

```shell
echo '199.232.68.133 raw.githubusercontent.com' >> /etc/hosts
```


验证flannel网络插件是否部署成功（Running即为成功）

```shell
kubectl get pods -n kube-system |grep flannel  
```

如果apply安装的时候出错了 ，可以通过describe查看日志

```shell
# 查看是否成功，发现报错了ErrImagePull
[root@localhost k8s]# kubectl get pods -n kube-system |grep flannel
kube-flannel-ds-574sf                   0/1     Init:ErrImagePull   0          9s
kube-flannel-ds-9d8kg                   0/1     Init:ErrImagePull   0          8s
kube-flannel-ds-pbf82                   0/1     Init:ErrImagePull   0          9s

# 查看日志
kubectl describe pod kube-flannel-ds-574sf -n kube-system
```

最终通过日志发现如果是镜像下载错误，可以手动下载（直接去hub.docker.com找flannel这个插件），比如：

```shell
# 手动下载
docker pull easzlab/flannel:v0.13.0-amd64

# 修改镜像名称
docker tag easzlab/flannel:v0.13.0-amd64 quay.io/coreos/flannel:v0.13.0-amd64
```

下载完成后，需要修改一下刚刚下载下来的文件`kube-flannel.yml `：将flannel的版本号修改为刚刚我们手动pull的镜像一致即可。（有两个地方）。最后重新apply安装。

验证插件安装状态

```shell
[root@VM-0-5-centos ~]# kubectl get pod -n kube-system   
NAME                                    READY   STATUS    RESTARTS   AGE
coredns-6d56c8448f-68gt6                1/1     Running   0          16m
coredns-6d56c8448f-jv8gx                1/1     Running   0          16m
etcd-k8s-master-01                      1/1     Running   0          16m
kube-apiserver-k8s-master-01            1/1     Running   0          16m
kube-controller-manager-k8s-master-01   1/1     Running   0          16m
kube-flannel-ds-4m552                   1/1     Running   0          4m42s
kube-flannel-ds-7v6dq                   1/1     Running   0          4m42s
kube-flannel-ds-grg7k                   1/1     Running   0          4m42s
kube-proxy-7js8p                        1/1     Running   0          16m
kube-proxy-mb26h                        1/1     Running   0          9m28s
kube-proxy-vjmhm                        1/1     Running   0          9m19s
kube-scheduler-k8s-master-01            1/1     Running   0          16m   
```
#### 验证集群是否安装完成

```shell
# 执行下面的命令
# 获取所有节点
[root@VM-0-5-centos ~]# kubectl get nodes
NAME            STATUS   ROLES    AGE     VERSION
k8s-master-01   Ready    master   17m     v1.19.0
k8s-node-01     Ready    <none>   9m32s   v1.19.0
k8s-node-02     Ready    <none>   9m23s   v1.19.0
```
发现这里node的roles为none，可以使用如下命令设置：

```shell
# 设置roles为node
kubectl label node k8s-node-01 node-role.kubernetes.io/node=node

# 取消
kubectl label node k8s-node-01 node-role.kubernetes.io/node-
```

通过命令`kubectl get pod -n kube-system -o wide`如果发现有状态错误的pod,则可以执行kubctl --namesspaace=kube-system describepod <pod_name> 来查明错误原因，常见原因是镜像没有有下载完成。

如果安装失败，则可以通过`kubeadm reset`命令恢复初始状态重新执行初始化init命令，再次进行安装。

#### 常用命令

```shell
# 查看节点信息
kubectl get pod -n kube-system  

# 监视
kubectl get pod -n kube-system -w   

# 详细信息
kubectl get pod -n kube-system -o wide   

kubctl describe pod [pod name]

kubectl delete pod [pod name]

kubctl creat pod -f [file name]
```

# 验证

> 安装nginx验证是否成功

任意目录创建一个`deployment.yaml`文件，内容如下：

```
apiVersion: apps/v1
kind: Deployment

metadata:
  name: nginx-deployment
  # labels:
  #   app: nginx   
spec:
  selector:
    matchLabels:
      app: nginx
  replicas: 3
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
          volumeMounts:
            - mountPath: /etc/nginx/
              name: confs
            - mountPath: /usr/share/nginx/html
              name: htmls 
          resources:
            limits:
              memory: 512Mi
              cpu: "1"
            requests:
              memory: 256Mi
              cpu: "0.2"
      volumes:
      - name: confs
        hostPath:
           path: conf
          #  type: FileOrCreate   
      - name: htmls
        hostPath:
           path: html
          #  type: FileOrCreate       
      restartPolicy: Always
```

**创建并启动：**

```shell
[root@k8s-master-01 ~]# kubectl create -f k8s/deployment.yaml --record
```

**查看deployments：**

```shell
[root@k8s-master-01 ~]# kubectl get deployments
NAME               READY   UP-TO-DATE   AVAILABLE   AGE
nginx-deployment   3/3     3            3           59m
```

**查看pods:**

```shell
[root@k8s-master-01 ~]# kubectl get pods
NAME                                READY   STATUS    RESTARTS   AGE
nginx-deployment-5d75b84f57-lpzlp   1/1     Running   0          57m
nginx-deployment-5d75b84f57-rbgrh   1/1     Running   0          61m
nginx-deployment-5d75b84f57-s5nfx   1/1     Running   0          57m
```

**创建Service对象(将Service端口代理至Pod端口示例)**

```shell
# 为deployment的nginx-deployment创建service，取名叫nginx-svc，并通过service的80端口转发至容器的80端口上。
kubectl expose deployment/nginx-deployment --name=nginx-svc --port=80 --target-port=80 --protocol=TCP
```

**查看Service对象**

```shell
[root@k8s-master-01 ~]# kubectl get service
NAME          TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)        AGE
kubernetes    ClusterIP   10.96.0.1        <none>        443/TCP        2d1h
nginx-svc     NodePort    10.103.247.211   <none>        80/TCP         56m
```

集群内部通过`curl  10.103.247.211` 可以访问，集群外部不可访问

删除这个Service：

```shell
kubectl delete service nginx-svc
```

**创建Service对象(将创建的Pod对象使用“NodePort”类型的服务暴露到集群外部)**

```shell
# 创建一个service对象，并将nginx-deployment创建的pod对象使用NodePort类型暴露到集群外部。
kubectl expose deployments/nginx-deployment --name=nginx-svc --port=80 --type=NodePort --protocol=TCP
```

查看Service对象

```shell
[root@k8s-master-01 ~]# kubectl get service
NAME         TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)        AGE
kubernetes   ClusterIP   10.96.0.1        <none>        443/TCP        2d1h
nginx-svc    NodePort    10.105.164.181   <none>        80:32172/TCP   5s
```

集群内部通过`curl  10.105.164.181` 可以访问，集群外部可通过外网IP加端口32172访问（注意开放端口号规则）。



# 参考：

[1] [Kubernetes Documentation 官方文档](https://kubernetes.io/docs/home/)

[2] [kubeadm init初始化k8s集群时报错，[kubelet-check] Initial timeout of 40s passed.](https://blog.csdn.net/curry10086/article/details/107579113)

[3] [kubeadm 报错 abort connecting to API servers after timeout of 5m0s](https://www.cnblogs.com/winstom/p/11684921.html)

[4] [修改K8S Master节点IP后使用kubeadm join无法添加节点](https://blog.csdn.net/zhangxiangui40542/article/details/95481726)

[5] [一个网友整理的不错的笔记](https://www.yuque.com/ivescode/k8s)


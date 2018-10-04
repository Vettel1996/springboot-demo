<<<<<<< HEAD
#12lian业务项目 ---这个1.0分支不再开发

####配置访问路径
```bash
server.context-path=/12lian  ## 这种可以被上下文取到值
server.servlet-path=/12lian

```

###log使用方式：

可以使用
```java
@Slf4j

//使用log 调用方法
log.info("测试log是否正常");

```
使用后相当于 
```java
static final Logger logger = LogManager.getLogger(某某某类.class.getName());
```

###项目启动方式：
```shell
# prod为要启动的环境，idea中可以在run Configurations中配置的program arguments 中加入--spring.profiles.active=prod 指定环境
java -jar xxx.jar --spring.profiles.active=prod
```

### 项目打包：
```shell
mvn clean package -Dmaven.test.skip=true   
```

###子模块端口
0. 主业务：8080
1. upload : 8081
2. im: 8082
3. sms:8083

###新增了一个startup.sh启动脚本，使用这个脚本启动程序 具体内容查看~
```java
startup.sh
```

#### 
新增分支

###spring boot tomcat config
```bash
#指定当所有可以使用的处理请求的线程数都被使用时，可以放到处理队列中的请求数，超过这个数的请求将不予处理，默认为10个
server.tomcat.accept-count= 
# Buffer output such that it is only flushed periodically.
server.tomcat.accesslog.buffered=true 
# Directory in which log files are created. Can be relative to the tomcat base dir or absolute.
server.tomcat.accesslog.directory=logs 
#是否开启tomcat访问日志
server.tomcat.accesslog.enabled=false 
# 日志日期格式
server.tomcat.accesslog.file-date-format=.yyyy-MM-dd 
# Format pattern for access logs.
server.tomcat.accesslog.pattern=common 
# Log file name prefix.
server.tomcat.accesslog.prefix=access_log 
# Defer inclusion of the date stamp in the file name until rotate time.
server.tomcat.accesslog.rename-on-rotate=false 
# Set request attributes for IP address, Hostname, protocol and port used for the request.
server.tomcat.accesslog.request-attributes-enabled=false 
 # Enable access log rotation.
server.tomcat.accesslog.rotate=true
# Log file name suffix.
server.tomcat.accesslog.suffix=.log 
# Comma-separated list of additional patterns that match jars to ignore for TLD scanning.
server.tomcat.additional-tld-skip-patterns= 
# Delay in seconds between the invocation of backgroundProcess methods.
server.tomcat.background-processor-delay=30 
# Tomcat base directory. If not specified a temporary directory will be used.
server.tomcat.basedir= 
# regular expression matching trusted IP addresses.
server.tomcat.internal-proxies=10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|\\
        192\\.168\\.\\d{1,3}\\.\\d{1,3}|\\
        169\\.254\\.\\d{1,3}\\.\\d{1,3}|\\
        127\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}|\\
        172\\.1[6-9]{1}\\.\\d{1,3}\\.\\d{1,3}|\\
        172\\.2[0-9]{1}\\.\\d{1,3}\\.\\d{1,3}|\\
        172\\.3[0-1]{1}\\.\\d{1,3}\\.\\d{1,3} 
# Maximum number of connections that the server will accept and process at any given time.
server.tomcat.max-connections= 
# Maximum size in bytes of the HTTP post content.
server.tomcat.max-http-post-size=0 
# Maximum amount of worker threads.
server.tomcat.max-threads=0 
# Minimum amount of worker threads.
server.tomcat.min-spare-threads=0 
 # Name of the HTTP header used to override the original port value.
server.tomcat.port-header=X-Forwarded-Port
# Header that holds the incoming protocol, usually named "X-Forwarded-Proto".
server.tomcat.protocol-header= 
# Value of the protocol header that indicates that the incoming request uses SSL.
server.tomcat.protocol-header-https-value=https 
# Whether requests to the context root should be redirected by appending a / to the path.
server.tomcat.redirect-context-root= 
# Name of the http header from which the remote ip is extracted. For instance `X-FORWARDED-FOR`
server.tomcat.remote-ip-header= 
# Character encoding to use to decode the URI.
server.tomcat.uri-encoding=UTF-8 
```
=======
# api
>>>>>>> a96fe8a6bb8f9daa6127f5cc367b36d7bf0cf12d

#基础镜像
FROM openjdk:17-jdk-alpine
#作者
LABEL authors="Shiro"

#指定工作目录
WORKDIR /soj

#将jar包添加工作目录
ADD target/soj-backend-judge-service-0.0.1-SNAPSHOT.jar .

#暴露端口
EXPOSE 8003

#启动容器时执行命令
ENTRYPOINT ["java", "-jar", "/soj/soj-backend-judge-service-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]
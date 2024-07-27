#基础镜像
FROM openjdk:8-jdk-alpine
#作者
LABEL authors="Shiro"

#指定工作目录
WORKDIR /app

#将jar包添加工作目录
ADD target/soj-backend-question-service-0.0.1-SNAPSHOT.jar .

#暴露端口
EXPOSE 8103

#启动容器时执行命令
ENTRYPOINT ["java", "-jar", "/app/soj-backend-question-service-0.0.1-SNAPSHOT.jar","--spring.profiles.active=prod"]
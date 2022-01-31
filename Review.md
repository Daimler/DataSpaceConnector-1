 
1. Build project

```bash
./gradlew :launchers:sprint-4:connector:build  
```

2Build Connector Image
```bash
docker build -t connector ./launchers/sprint-4/connector/
```

sudo docker pull openjdk:11-jre-slim
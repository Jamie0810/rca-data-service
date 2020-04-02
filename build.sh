./gradlew bootJar -x test docker \ &&
docker save -o data-service.tar 10.57.232.169:8800/rca/data-service \ &&
scp ./data-service.tar bigdata@10.57.232.169:/home/bigdata/images

cd /home/stock
git pull
mvn clean package -Dmaven.test.skip=true
ps aux|grep java|grep jar|awk '{print $2}'|xargs kill
yes | cp -rf cloud_quick_demo/target/cloud-quick_demo.jar /home/cloud-quick_demo.jar

cd ..
nohup java -jar cloud-quick_demo.jar --server.port=80 >> info.log 2>&1
exit 0
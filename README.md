// todo write
mvn clean install test allure:serve
mvn clean install -Dmaven.test.failure.ignore=true allure:serve

docker build -t smthname .


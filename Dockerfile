FROM ubuntu:latest

    RUN apt-get update && apt-get install -y --no-install-recommends \
        curl \
        unzip \
        ca-certificates \
        openjdk-8-jdk \
        dbus

    # Скачиваем и устанавливаем Allure CLI
    RUN curl -L https://repo.maven.apache.org/maven2/io/qameta/allure/allure-commandline/2.30.0/allure-commandline-2.30.0.zip -o allure.zip && \
        unzip allure.zip && \
        mv allure-2.30.0 allure && \
        rm allure.zip

    # Добавляем путь к Allure в переменную окружения PATH
    ENV PATH="$PATH:/allure/bin"
    EXPOSE 9999

    # Копируем результаты тестов в контейнер (если необходимо)
    COPY target/allure-results allure-results
CMD ["allure", "serve", "-p", "9999"]

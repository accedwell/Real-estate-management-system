# REMS - Real Estate Management System
# Builds the WAR from source and runs it on Tomcat 9 at the site root ("/").
# Used by Render (see render.yaml), but works on any Docker host.

# ---------------------------------------------------------------- 1. build
FROM tomcat:9.0-jdk11-temurin AS build
WORKDIR /src
COPY RealEstateManagementSystem/src/java ./java
COPY RealEstateManagementSystem/web ./web
COPY RealEstateManagementSystem/lib ./lib
COPY ["The RealEstate Management System  DBscript.sql", "./schema.sql"]

RUN set -eux; \
    mkdir -p app/WEB-INF/classes app/WEB-INF/lib app/WEB-INF/db; \
    cp -r web/. app/; \
    cp lib/openpdf-*.jar lib/postgresql-*.jar app/WEB-INF/lib/; \
    cp schema.sql app/WEB-INF/db/schema.sql; \
    find java -name '*.java' > sources.txt; \
    javac --release 8 -nowarn -encoding UTF-8 -d app/WEB-INF/classes \
        -cp "$CATALINA_HOME/lib/servlet-api.jar:lib/openpdf-1.3.26.jar:lib/postgresql-42.7.10.jar" @sources.txt; \
    cd app && jar cf /src/ROOT.war .

# ---------------------------------------------------------------- 2. run
FROM tomcat:9.0-jre11-temurin
ENV TZ=Africa/Johannesburg \
    JAVA_OPTS="-Xms128m -Xmx384m -Djava.security.egd=file:/dev/./urandom"
RUN rm -rf "$CATALINA_HOME"/webapps/*
COPY --from=build /src/ROOT.war "$CATALINA_HOME"/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]

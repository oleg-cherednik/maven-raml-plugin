# WebService to RAML maven plugin

### Source code
```bash
git clone git@github.com:oleg-cherednik/maven-raml-plugin.git
cd maven-raml-plugin
mvn install
```

### Configuration in pom.xml
```xml
<project>
    <build>
        <plugins>
            <plugin>
                <groupId>cop.maven.plugins</groupId>
                <artifactId>raml</artifactId>
                <version>0.9-SNAPSHOT</version>
            </plugin>
        </plugins>
    </build>                                    
    
    <repositories>
        <repository>
            <id>cop.mvn.repo</id>
            <url>https://raw.github.com/oleg-cherednik/maven-repo/master/</url>
        </repository>
    </repositories>
</project>
````

### First start
```bash
mvn raml:raml
cd target\docs
```
You can see actual yaml config and RAML result. _(Note, that no need any modification in project's business code to make first run.)_

    





# Local Sandbox setup for Adobe Experience Manager (AEM)

<!-- MarkdownTOC autolink=true bracket=round depth=2 -->
Table of Contents

- [ ] [Prerequisites](#prerequisites)
    - [ ] [Install Java 1.8.0_151](#install-java-1.8.0_151)
    - [ ] [Install Maven](#install-maven)
    - [ ] [Notes](#notes)
    - [ ] [Registry mirror](#registry-mirror)
    - [ ] [IDE Setup](#ide-setup)
- [ ] [Get things running](#get-things-running)
    - [ ] [Start AEM Server](#start-aem-server)
    - [ ] [Code base](#code-base)
- [ ] [Installing dependencies in AEM](#installing-dependencies-in-aem)
- [ ] [Common AEM Links](#common-aem-links)
- [ ] [FAQs](#faqs)

 <!-- /MarkdownTOC -->

## Prerequisites

Below are instructions which follow the Adobe docs on prerequisites. You may view the original Adobe prerequisites [here](https://docs.adobe.com/content/help/en/experience-manager-learn/getting-started-wknd-tutorial-develop/project-setup.html)

### Install Java 1.8.0_151

- Download and install Java 8/11

- Set the **`JAVA_HOME`** environment variable  
  Mac OS/Linux
    - Open Terminal
    - Confirm you have JDK by typing **`which java`**.
        - It should show something like **`/usr/bin/java`**.
    - Check you have the needed version of Java, by typing **`java -version`**.
    - **`JAVA_HOME`** is essentially the full path of the directory that contains a sub-directory named bin which in-turn contains the java.
    - For Mac OSX 10.9 and higher (High Sierra)
        - It is `/Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home`
    - Add this line to your **~/.bash_profile**: `export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_151.jdk/Contents/Home`
    - Make a new terminal window and type `echo $JAVA_HOME` to confirm the path

  Windows
    - Open Command Prompt
        - Confirm you have JDK by typing **`where java`**.
            - It should show something like **`C:\Program Files\Java\jdk1.8.0_271\bin\java.exe`**.
        - Check you have the needed version of Java, by typing **`java -version`**
        - **`JAVA_HOME`** is essentially the full path of the directory that contains a sub-directory named bin which in-turn contains the java.
        - Type the following command in the command prompt  
          ` set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_271\bin\java.exe `
        - Make a new terminal window and type `echo %JAVA_HOME%` to confirm the path


### Install Maven

- `brew install maven`
- **OR**
- Download the .bin.zip version and extract it, it’ll extract to it’s own folder.
    - Move this folder to a known location, for example, `your.nt.username/`
    - **Update your $PATH**
        - Mac OS  
          In your **~/.bash_profile** you may need to add the following line: `export PATH=$PATH:/Users/your.nt.username/apache-maven-3.6.2/bin` if the line exists already, simply add `:/Users/your.nt.username/apache-maven-3.6.2/bin` this will tack it on to the existing environment path variable
        - Windows  
          Set MAVEN_HOME and add the same to the path variable. In your command prompt type the following command (Update path based on where the files have been extracted).
          ` set MAVEN_HOME=C:\Apps\apache-maven-3.6.3 `

### Notes

- Prerequisite 1  
  **Java 1.8 or Java 11**
    - You may try to install Java 1.8 or Java 11.
- Prerequisite 2 **Adobe Experience Manager** -  download AEM 6.5 from the following [link](https://drive.google.com/drive/u/0/folders/1spAss57pAFDQDxKg1V5wyBPm--f90A2d).
    - Folder is under Google Drive -> AEM -> Setup
        - Note: make sure all files are downloaded from the folder
            - Adobe Experience Manager - **aem-author-4502.jar**
                - For Publisher **(possibly)** you can rename it to **aem-publisher-4503.jar**
            - license - **license.properties**


### Registry Mirror

- Setting Maven (**`mvn`**) to use Artifactory.
    - Like all of our internal tools unless you are on the Transparent proxy (currently **`Corp_dev`**) you will need to setup the registry mirrors to point to Artifactory or you will get a `Connection Refused` message.
        - Alternatively, you can connect to **`Guest_net`** to download the dependencies locally but this is not recommended as if the dependencies change you will have to keep switching back.
    - To setup the registry mirror for **`mvn`** you will need to add/change the **`settings.xml`** file in the **`<user_dir>/.m2/`** folder so you would have a **`<user_dir>/.m2/settings.xml`**
        - If that file does not exist you may `touch` or create it manually.
        - The `settings.xml` file should like like the following:

      ```xml
      <settings xmlns="https://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="https://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="https://maven.apache.org/SETTINGS/1.0.0
                              https://maven.apache.org/xsd/settings-1.0.0.xsd">
      <proxies>
        <proxy>
            <id>optional</id>
            <active>true</active>
            <protocol>http</protocol>
            <host>artifactory-proxy.global.dish.com</host>
            <port>8080</port>
            </proxy>
        </proxies>      
        <profiles>
            <!-- ====================================================== -->
            <!-- P U B L I C P R O F I L E -->
            <!-- ====================================================== -->
                <profile>
                    <id>artifactory</id>
                    <activation>
                        <activeByDefault>true</activeByDefault>
                    </activation>
                    <repositories>
                        <repository>
                            <snapshots>
                                <enabled>false</enabled>
                            </snapshots>
                            <id>central</id>
                            <name>remote-repos</name>
                            <url>https://artifactory.global.dish.com/artifactory/remote-repos</url>
                        </repository>
                        <repository>
                            <snapshots />
                            <id>snapshots</id>
                            <name>libs-snapshot</name>
                            <url>https://artifactory.global.dish.com/artifactory/libs-snapshot</url>
                        </repository>
                    </repositories>
                    <pluginRepositories>
                        <pluginRepository>
                            <snapshots>
                                <enabled>false</enabled>
                            </snapshots>
                            <id>central</id>
                            <name>plugins-release</name>
                            <url>https://artifactory.global.dish.com/artifactory/plugins-release</url>
                        </pluginRepository>
                        <pluginRepository>
                            <snapshots />
                            <id>snapshots</id>
                            <name>plugins-release</name>
                            <url>https://artifactory.global.dish.com/artifactory/plugins-release</url>
                        </pluginRepository>
                    </pluginRepositories>
                </profile>
            </profiles>
            <activeProfiles>
                <activeProfile>artifactory</activeProfile>
            </activeProfiles>
        </settings>
        ```

        - For more information on **`mvn`** settings please view the following links:
            - [Settings Reference](https://maven.apache.org/settings.html)
            - [Elements in the Settings file](http://maven.apache.org/ref/3.6.2/maven-settings/settings.html)
            - [How to use a custom settings.xml file in mvn](https://stackoverflow.com/questions/25277866/maven-command-line-how-to-point-to-a-specific-settings-xml-for-a-single-command)

### IDE Setup

To ensure your IDE has support for Lombok annotations, do the following:

- Navigate to [projectlombok.org](https://projectlombok.org/)
- Clock on the `Install` menu at the top and select your IDE from the list.
    - Most major Java IDEs are supported.
    - If your IDE is not in the supported, you will likely get syntax errors reported by the IDE that are not really errors. It is strongly recommended that you use a supported IDE.
- Follow the install instructions for your IDE (usually means installing a plugin).

## Get things running

### Start AEM Server

Go to the folder you download the **aem-author-4502.jar** and double click on the **aem-author-4502.jar** file to start the AEM app

- If you get a message that the developer is not trusted see below otherwise disregard
    - You will need to go to `System Preferences -> Security & Privacy`
    - Under `Allow apps downloaded from:` select `Open anyways`

Once that is done then you should see `localhost:4502` open up

- username: admin
- password: admin

### Code base

- Clone the repo [wholesale-wireless-portal](https://gitlab.global.dish.com/it-wireless-commercial-application-engineering-repos/multi-tenant-aem-hosting/wholesale-wireless-portal)
    - To build this project use Maven. From the root directory, run **`mvn -PautoInstallPackage clean install`** to build the bundle and content package and install to a CQ instance.
        - If you have errors relating to certification validation you can run **`mvn -PautoInstallPackage -Dmaven.wagon.http.ssl.insecure=true clean install -s settings.xml`**

## Installing dependencies in AEM

Download the following zip files from this [Google Drive Folder](https://drive.google.com/drive/u/0/folders/1spAss57pAFDQDxKg1V5wyBPm--f90A2d)
1.Service Pack [download here](https://drive.google.com/drive/u/0/folders/1spAss57pAFDQDxKg1V5wyBPm--f90A2d)


## Common AEM Links

- `http://localhost:4502/`
    - AEM Home page to navigate to anywhere
- `http://localhost:4502/crx/de`
    - CRX is short for Content Repository eXtreme. It has full repository access with code editor to manage, and access data using a standardized Java interface.
- `http://localhost:4502/crx/packmgr/`
    - CRX Package manger to upload and install packages
- `http://localhost:4502/system/console/bundles`
    - Debugging. You may need to search for `wholesale` and if they are running

## FAQs

### Connection Refused when downloading dependencies

```bash
[ERROR]     Unresolveable build extension: Plugin com.day.jcr.vault:content-package-maven-plugin:0.0.24 or one of its dependencies could not be resolved: Failed to read artifact descriptor for com.day.jcr.vault:content-package-maven-plugin:jar:0.0.24: Could not transfer artifact com.day.jcr.vault:content-package-maven-plugin:pom:0.0.24 from/to adobe (https://repo.adobe.com/nexus/content/groups/public/): Connect to repo.adobe.com:443 [repo.adobe.com/192.147.130.162] failed: Connection refused (Connection refused) -> [Help 2]
```

- This means that you have not configured **`mvn`** to use Artifactory as a registry mirror. Please see [Registry mirror](#registry-mirror) in order to set this up.

### Connection Refused when building

```bash
[INFO] --- content-package-maven-plugin:0.0.24:install (install-content-package) @ portal.ui.apps ---
[INFO] Installing portal.ui.apps (<path>/ui.apps/target/wholesale.ui.apps-1.1.2-SNAPSHOT.zip) to http://localhost:4502/crx/packmgr/service.jsp
[INFO] I/O exception (java.net.ConnectException) caught when processing request: Connection refused (Connection refused)
[INFO] Retrying request

[ERROR] Failed to execute goal com.day.jcr.vault:content-package-maven-plugin:0.0.24:install (install-content-package) on project wholesale.ui.apps: Connection refused (Connection refused) -> [Help 1]
```

- This means that you have not started the AEM server. See [Start AEM Server](#start-aem-server) to start it.

### Certificate validation errors when building

- You can run `mvn -PautoInstallPackage -Dmaven.wagon.http.ssl.insecure=true clean install -s settings.xml`. See [Code base](#code-base).

### Compile Error: **cannot find symbol**

<!-- markdownlint-disable MD009 -->
```bash
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.1:compile (default-compile) on project portal.core: Compilation failure: Compilation failure: 
[ERROR] <path>/core/src/main/java/com/wholesale/core/models/CTAButtonModel.java:[21,24] cannot find symbol
[ERROR]   symbol:   class PostConstruct
[ERROR]   location: package javax.annotation
```
<!-- markdownlint-enable MD009 -->

- Make sure you have the correct version of Java installed from the [Prerequisites](#prerequisites).

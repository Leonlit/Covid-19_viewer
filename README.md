# Covid-19_Viewer

A simple dashboard that let's user to analyse covid-19 data. 
The application is constructed by using the JavaFX library provided by the OpenJFX.io. The application also used a third party API (application programming interface) from covid19api.com a free to use Covid-19 API data provider.

## Feature:

- View global status on covid-19 data
- View detailed Data on the covid-19 data for a specific country
- Comparing the covid-19 data between different countries

## How to use the application?
Here's a [short video](https://youtu.be/TZ4Qwvpa2Nw) ( 5min+ ) on how the application works


## Interfaces:

### Main window
<img src="https://github.com/Leonlit/Covid-19_Viewer/blob/master/asset/main_window.png?raw=true" alt="Main window of the  application" height="400px">


### Window for viewing specific country's covid-19 detailed data
<img src="https://github.com/Leonlit/Covid-19_Viewer/blob/master/asset/viewing_specific_country_detailed_data.png?raw=true" alt="Viewing Specific Country Detailed Data" height="400px">


### Comparing covid-19 data between different countries
<img src="https://github.com/Leonlit/Covid-19_Viewer/blob/master/asset/comparing_covid_19_data_between_countries.png?raw=true" alt="Comparing Covid-19 Data Between Countries" height="400px">


## How to setup the project?

Well, just clone the project and import the project into any of your IDE that supports Java (Netbeans, Eclipse, etc). Then make sure you have these installed on your machine:

- [Javafx 14+](https://openjfx.io/)
- [Maven](https://maven.apache.org/)
- [Launch4j](http://launch4j.sourceforge.net/)

To build a release, if you're using window as your OS, you can simply execute the "deploy" script which will execute the commands needed for building a JAR file for the project. Then, use launch4j to generate the executable file. 

Here's an example configuration XML file that you could refer during the creation of the executable.

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<launch4jConfig>
  <dontWrapJar>false</dontWrapJar>
  <headerType>gui</headerType>
  <jar>link to the jar file</jar>
  <outfile>destination that you want to output the executable to</outfile>
  <errTitle></errTitle>
  <cmdLine></cmdLine>
  <chdir>.</chdir>
  <priority>normal</priority>
  <downloadUrl>http://java.com/download</downloadUrl>
  <supportUrl></supportUrl>
  <stayAlive>false</stayAlive>
  <restartOnCrash>false</restartOnCrash>
  <manifest></manifest>
  <icon></icon>
  <singleInstance>
    <mutexName>Covid19_Viewer</mutexName>
    <windowTitle>Covid19_Viewer</windowTitle>
  </singleInstance>
  <jre>
    <path></path>
    <bundledJre64Bit>false</bundledJre64Bit>
    <bundledJreAsFallback>false</bundledJreAsFallback>
    <minVersion>1</minVersion>
    <maxVersion></maxVersion>
    <jdkPreference>preferJre</jdkPreference>
    <runtimeBits>64/32</runtimeBits>
  </jre>
</launch4jConfig>
```

## Contributing:

If you have any ideas for the project feel free to open up an issues.

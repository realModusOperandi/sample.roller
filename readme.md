# Roller

Sample apps using local EJB calls across app boundaries to demonstrate one "business app" that requires two apps deployed to the same server in order to work.

## Project Structure
This repo consists of two separate Gradle projects; you can import them into a single IntelliJ project/Eclipse workspace for ease of working with them. 

### Roller
This project holds an enterprise application divided into several Gradle subprojects:
- **roller-ejb:** Contains an EJB 3.x stateless bean for business logic. In this case, it knows how to roll a certain amount of dice with a certain count of sides and return the overall result.
- **roller-web:** An obsolete, servlet-based interface to the enterprise application. Currently doesn't match the actual functionality, since it's supposed to be deprecated in favor of Roller UI. 
- **roller-ear:** Parent project that defines the overall enterprise application structure. No code here, but earlib dependencies should be defined here.
- **roller-lib:** Not included in the roller archive, this project builds a jar containing the local interface for the EJB. It should be built and installed to your local m2 repo via the `:roller-lib:publishToMavenLocal` Gradle task before building Roller UI. 

  When deployed, it should be configured as a shared library on the classloader for the server (so both apps can share the same loaded class definition).

### Roller UI
This project holds the modern, React-based frontend to our Roller business application. It consists of two components:
- **Front End**
  
  Found in `src/main/frontend`, this is a React-based app that provides the user interface to Roller. The app uses Axios to make a REST API call to the back end in order to roll the dice requested by the user.

  The front end is built via the Gradle `buildStandaloneClient` task, which handles automatically installing node and NPM, installing dependencies from `package.json`, and then running `react-scripts build` to produce the static web application. Gradle's `war` task is configured to copy the build output to `src/main/webapp` before assembling the web archive, so the frontend is served from the `.war`. 

  This configuration enables a backend-for-frontend pattern where the REST API is always accessible at the same hostname the app is running from.

  For simple layout development/debugging, you can run the React development server standalone, however you will have to manually reconfigure the code to talk to a different back end since the Java EE backend won't be running at the same host/port:

  ```shell
  $ cd src/main/frontend
  $ node/$nodeDir/bin/npm run start
  ```

  _Replace `$nodeDir` with the directory name of the specific node install that Gradle has provided._

- **Back End**

  Found in `src/main/kotlin`, this is a Java EE 7-based Kotlin back end application. It provides a JAX-RS application with the endpoint used by the frontend to request dice rolls. 

  The endpoint uses the interface found in the `roller-lib` shared library to inject the EJB from Roller and call the business method therein. This is why it is important to ensure the `roller-lib` sub-project of Roller has been built and installed to your local m2 repository.

## Deployment

This application is intended to demonstrate a scenario where two separate Java EE application archives need to be deployed to the same server/JVM in order to work. 

This means that you must deploy both the roller-ear.ear and roller-ui.war files to the same server or cluster. You can find roller-ear.ear in roller/roller-ear/build/libs, and roller-ui.war in roller-ui/build/libs.

Furthermore, you must configure the roller-lib.jar as a shared library at the server level for all servers the application is deployed on, so that the same interface class can be applied both to the EJB itself and the JAX-RS class that injects and uses it. For WebSphere traditional that means referencing the shared library by the server classloader. For Liberty that requires doing a common library reference for both applications:

```xml
<enterpriseApplication id="roller.ear" location="roller.ear">
    <web-ext context-root="/roller-war" id="roller.ear_web-ext_roller-war" moduleName="roller-war"/>
    <classloader commonLibraryRef="ejbInterfaceLib"/>
</enterpriseApplication>

<webApplication contextRoot="roller-ui" id="roller-ui.ear" location="roller-ui.war">
    <classloader commonLibraryRef="ejbInterfaceLib"/>
</webApplication>

<library id="ejbInterfaceLib">
    <file name="${shared.config.dir}/lib/global/roller-lib.jar"/>
</library>
```

Roller UI should be deployed with a context-root of `roller-ui`. If you want to change this, you need to update the React applicatin in two places:

In `package.json`, update:
```json
  "homepage": "/roller-ui/",
```

In `index.js`, update:
```jsx
    <Router basename={'/roller-ui'}>
```

## Accessing the Application

Once both applications have started, access the UI at `<host>:<port>/roller-ui/` (or substitute the context root you set if you changed it).

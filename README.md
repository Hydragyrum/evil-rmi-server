# evil-rmi-server
An evil RMI server that can launch an arbitrary command. May be useful for CVE-2021-44228 in a local privesc scenario

## Build

`./gradlew bootJar`

## Run

```
Usage: java -jar build/libs/evilRMIServer-1.0-SNAPSHOT.jar [-hV] [-p=<port>]
       <cmd>
An evil RMI Server to help construct and run an arbitrary command.
      <cmd>           The Command to run. Wrap in quotes if there are spaces.
  -h, --help          Show this help message and exit.
  -p, --port=<port>   The port to listen on
  -V, --version       Print version information and exit.
  ```

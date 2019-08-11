//package ca.frar.jjjrmi.socket;
//import ca.frar.utility.console.Console;
//import java.util.HashSet;
//import java.util.Set;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.websocket.Endpoint;
//import javax.websocket.server.ServerApplicationConfig;
//import javax.websocket.server.ServerEndpointConfig;
//
///**
//Sets JJJConfigurator as the configurator for all RMISocket classes.
//The JJJConfigurator in turn saves the socket to the session and returns it if a reload takes place.
//@author edward
//*/
//public class JJJApplicationConfig  implements ServerApplicationConfig {
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> set) {
//        Console.trace();
//        Console.logMethod("# of programmic endpoints : " + set.size());
//        Set<ServerEndpointConfig> retSet = new HashSet<>();
//
//        for (Class<?> aClass : set){
//            Console.log(aClass.getSimpleName());
//            if (!JJJSocket.class.isAssignableFrom(aClass)) continue;
//            String endpointName = "/" + aClass.getSimpleName();
//            ServerEndpointConfig.Builder builder = ServerEndpointConfig.Builder.create(aClass, endpointName);
//
//            if (!JJJSocket.class.isAssignableFrom(aClass)) try {
//                throw new InstantiationException();
//            } catch (InstantiationException ex) {
//                Logger.getLogger(JJJApplicationConfig.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//            builder.configurator(new JJJConfigurator((Class<? extends JJJSocket<?>>) aClass));
//            retSet.add(builder.build());
//            Console.log(String.format("added configurator for class '%s' at endpoint '%s'", aClass.getSimpleName(), endpointName));
//        }
//
//        return retSet;
//    }
//
////    @Override
//    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> set) {
//        Console.trace();
//        Console.logMethod("# of annotated endpoints : " + set.size());
//        return set;
//    }
//}
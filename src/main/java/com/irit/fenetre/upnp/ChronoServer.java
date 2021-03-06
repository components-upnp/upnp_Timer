package com.irit.fenetre.upnp;

/**
 * Created by Abdourahamane Ly on 01/02/2017.
 */
import com.irit.fenetre.Chronometres;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.*;
import org.fourthline.cling.binding.annotations.*;
import org.fourthline.cling.model.*;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.*;
import java.io.IOException;
/**
 * Classe qui permet de lancer le serveur du Chronometre UPnP
 * @author creative Technology
 *
 */
public class ChronoServer implements Runnable {
    /**
     * Run the UPnP service
     */
    public void run() {
        try {

            final UpnpService upnpService = new UpnpServiceImpl();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    upnpService.shutdown();
                }
            });

            // Add the bound local device to the registry
            upnpService.getRegistry().addDevice(
                    createDevice()
            );
        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    /**
     * Permet de créer un device
     * Il est possible de créer plusieurs service pour un méme device, dans ce cas confer commentaires en fin de methode.
     * @return LocalDevice
     * @throws ValidationException
     * @throws LocalServiceBindingException
     * @throws IOException
     */
    public LocalDevice createDevice()
            throws ValidationException, LocalServiceBindingException, IOException {

        /**
         * Description du Device
         */
        DeviceIdentity identity =
                new DeviceIdentity(
                        UDN.uniqueSystemIdentifier("Chronometre")
                );

        DeviceType type =
                new UDADeviceType("Chronometre", 1);

        DeviceDetails details =
                new DeviceDetails(
                        "FriendlyChronometre",					// Friendly Name
                        new ManufacturerDetails(
                                "CreaTech",								// Manufacturer
                                ""),								// Manufacturer URL
                        new ModelDetails(
                                "Chronometre",						// Model Name
                                "Simulation d'un chronometre.",	// Model Description
                                "v1" 								// Model Number
                        )
                );

        // Ajout du service
        LocalService<ChronoService> chrnoService =
                new AnnotationLocalServiceBinder().read(ChronoService.class);

        chrnoService.setManager(
                new DefaultServiceManager(chrnoService, ChronoService.class)
        );

        new Chronometres(chrnoService).setVisible(true);

        // retour en cas de 1 service
        return new LocalDevice(identity, type, details, chrnoService);


		/* Si jamais plusieurs services pour un device (adapter code)
	    return new LocalDevice(
	            identity, type, details,
	            new LocalService[] {switchPowerService, myOtherService}
	    );
	    */
    }
}
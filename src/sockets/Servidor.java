package sockets;

import java.awt.*;

import java.io.DataInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class Servidor  {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		MarcoServidor mimarco=new MarcoServidor();
		
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
	}	
}

/*
 * Necesitamos que el servidor realize más de una tarea a la vez, eso se consigue gracias a los hilos, la primera tarea es recoger e imprimir el mensaje mandado por el cliente, 
 * la segunda tarea es mantener el servidor escuchando por si llega otro mensaje
 * 
 * Tenemos que implementar la interfaz Runnable e implementar el metodo run()
 */

class MarcoServidor extends JFrame implements Runnable{
	
	public MarcoServidor(){
		
		setBounds(1200,300,280,350);				
			
		JPanel milamina= new JPanel();
		
		milamina.setLayout(new BorderLayout());
		
		areatexto=new JTextArea();
		
		milamina.add(areatexto,BorderLayout.CENTER);
		
		add(milamina);
		
		setVisible(true);	
		
		/*
		 * Creamos e iniciamos el hilo
		 */
		
		Thread mihilo = new Thread(this);
		
		mihilo.start();
		
						
		}
	
	
	@Override
	public void run(){
		
	//	System.out.println("Estoy escuchando"); 
		
		
		try {
			ServerSocket servidor = new ServerSocket(9999);   //le indicamos el puerto que tiene que abrir (el mismo del cliente) este es el puerto que estará a la escucha
			
			String nick;
			String ip;
			String mensaje;
			
			ArrayList<String> listaIp = new ArrayList<String>();
			
			PaqueteEnvio paquete_recibido;
			
			/*
			 * El while nos genera un bucle infinito que sirve para que al enviar los mensajes y se haga el .close() se vuelva a crear el socket 
			 * y no se cierre el servidor al enviar el primer mensaje :D
			*/
			
			while(true) {
			
			Socket misocket = servidor.accept(); //aceptará las conexiones que le vengan del exterior gracias al método accept
			
						
			ObjectInputStream paquete_datos = new ObjectInputStream(misocket.getInputStream()); //Creamos el flujo de datos de entrada
			
			paquete_recibido = (PaqueteEnvio) paquete_datos.readObject(); //Le decimos que lea el flujo de datos y lo que se encuentre lo almacene en paquete_recibido
			
			
			/*
			 * Almacenamos en cada variable lo que nos ha llegado de paquete_recibido gracias a los getters de la clase Cliente
			 */
			nick = paquete_recibido.getNick();
			ip = paquete_recibido.getIp();
			mensaje = paquete_recibido.getMensaje();
			
			
			
		//	DataInputStream flujo_entrada = new DataInputStream(misocket.getInputStream()); //le indicamos por que socket va a viajar la informacion
			
		//	String mensaje_texto = flujo_entrada.readUTF(); //leemos lo que viaja por el flujo de datos que nos llega del cliente
			
			/*
			 * Utilizaremos nuestro JTextArea (está al final) para imprimir el mensaje
			 */
			
		//	areatexto.append("\n" + mensaje_texto); //lo escribimos
			
			
				if (!mensaje.equals("online")) {
				
				areatexto.append("\n" + nick + " dice: * " + mensaje + " * para " + ip);
				
				
				/*
				 * AHORA ENVIAREMOS EL MENSAJE QUE NOS HA LLEGADO DE UN CLIENTE AL CLIENTE QUE LE ABREMOS INDICADO
				 */
				Socket enviaDestinatario = new Socket(ip, 9090);  //Tendemos un puente entre servidor y CLIENTE DESTINATARIO e indicamos que el puerto de entrada es el 9090
				 
				ObjectOutputStream paquete_reenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream()); //Utilizamos el socket que acabamos de crear para mandar el paquete
				
				paquete_reenvio.writeObject(paquete_recibido); //Llenamos el flujo de salida con el paquete_recibido; que es el mensaje del cliente que envía
				
				
				
				/*
				 * Cerramos todos los flujos
				*/
				paquete_reenvio.close();
				
				enviaDestinatario.close();
				
				misocket.close(); //cerramos la conexión
				
				} else {
					 
					//---------------------- DETECTA ONLINE ----------------------
					InetAddress localizacion = misocket.getInetAddress();
					
					String ipRemota = localizacion.getHostAddress();
				
					System.out.println("Online: " +ipRemota);
					
					listaIp.add(ipRemota); //Llenamos el ArrayList con cada ip que nos llega
					
					paquete_recibido.setIps(listaIp);
					
					for (String z:listaIp) {
						
						System.out.println("Array: " +z);
						
						Socket enviaDestinatario = new Socket(z, 9090);  //Tendemos un puente entre servidor y CLIENTE DESTINATARIO e indicamos que el puerto de entrada es el 9090
						 
						ObjectOutputStream paquete_reenvio = new ObjectOutputStream(enviaDestinatario.getOutputStream()); //Utilizamos el socket que acabamos de crear para mandar el paquete
						
						paquete_reenvio.writeObject(paquete_recibido); //Llenamos el flujo de salida con el paquete_recibido; que es el mensaje del cliente que envía
						
						/*
						 * Cerramos todos los flujos
						*/
						paquete_reenvio.close();
						
						enviaDestinatario.close();
						
						misocket.close(); //cerramos la conexión
						
						
						
					}
					
					
					//------------------------------------------------------------//
					
				}
			}
			
			
			
			
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error AQUI: "+ e);
		}
		
	}
	
	private	JTextArea areatexto;
}
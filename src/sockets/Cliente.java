package sockets;

import java.awt.event.ActionEvent;


import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.net.*;
import java.util.ArrayList;

public class Cliente {

	public static void main(String[] args) {
			// TODO Auto-generated method stub
			
			MarcoCliente mimarco=new MarcoCliente();
			
			mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		}

	}


	class MarcoCliente extends JFrame{
		
		public MarcoCliente(){
			
			setBounds(600,300,280,350);
					
			LaminaMarcoCliente milamina=new LaminaMarcoCliente();
			
			add(milamina);
			
			setVisible(true);
			
			addWindowListener(new EnvioOnline());
			
			}	
		
	}
	
	
	// ------------------   ENVIO DE SEÑAL ONLINE ------------------
	class EnvioOnline extends WindowAdapter{
		
		public void windowOpened(WindowEvent e) {
			
			try {
				
				Socket miSocket = new Socket("192.168.1.42", 9999);
				
				PaqueteEnvio datos = new PaqueteEnvio();
				
				datos.setMensaje("online");
				
				ObjectOutputStream paquete_datos = new ObjectOutputStream(miSocket.getOutputStream());
				
				paquete_datos.writeObject(datos);
				
				miSocket.close();
				
				
			} catch (Exception e2) {
				System.out.println("Error en EnvioOnline" +e2.getMessage());
			}
			
		}
		
	}
	
	// -----------------------------------//

	
	

	/*
	 * IMPLEMENTAMOS RUNNABLE PARA USAR HILOS Y HACER QUE EL CLIENTE ESTE A LA ESCUCHA
	 */
	class LaminaMarcoCliente extends JPanel  implements Runnable{
		

		public LaminaMarcoCliente(){
			
			String nick_usuario = JOptionPane.showInputDialog("Introduce tu nick: ");
					
			JLabel n_nick = new JLabel("Nick: ");
			
			add(n_nick);
			
			nick = new JLabel();
			
			nick.setText(nick_usuario);
			
			add(nick);
		
			JLabel texto=new JLabel("Online: ");
			
			add(texto);
			
			ip = new JComboBox();
			
			/*
			lista estatica desplegable
			ip.addItem("192.168.1.42");
			ip.addItem("localhost");
			ip.addItem("User3");
			 */
						
			add(ip);
			
			campoChat = new JTextArea(12,20);
			
			add(campoChat);
		
			campo1=new JTextField(20);
		
			add(campo1);		
		
			miboton=new JButton("Enviar");
			
			EnviaText mievento = new EnviaText();
			miboton.addActionListener(mievento);
			
			add(miboton);	
			
			/*
			 * Ponemos en funcionamiento el hilo de escucha
			 */
			
			Thread miHilo = new Thread(this);
			miHilo.start();
			
		}
		
		
		private class EnviaText implements ActionListener{

			@Override
			public void actionPerformed(ActionEvent e) {
				
				//System.out.println(campo1.getText());
				
				campoChat.append("\n" + campo1.getText());
				
				try {
					Socket misocket = new Socket("192.168.1.42", 9999);   //los argumentos que necesita son dirección de red(ip) y puerto
					
					
					PaqueteEnvio datos = new PaqueteEnvio();
					
					/*
					 * Empaquetamos lo que queremos enviar al destinatario dentro de la variable datos
					 */
					datos.setNick(nick.getText());
					datos.setIp(ip.getSelectedItem().toString());
					datos.setMensaje(campo1.getText()); //mensaje
					
					/*
					 * Ahora tenemos que enviar el objeto datos al servidor a través de la red
					 */
					
					ObjectOutputStream paquete_datos = new ObjectOutputStream(misocket.getOutputStream()); // Creamos un flujo de salida de objetos con ObjectOutputStream
					
					paquete_datos.writeObject(datos);
					
					campo1.setText(null);
									
					misocket.close();
					
										
			//		DataOutputStream flujo_salida = new DataOutputStream(misocket.getOutputStream()); //se le indica por dónde van a circular los datos
					
			//		flujo_salida.writeUTF(campo1.getText()); //recupera el texto que hay en el JTextField
					
					/*
					 * En este punto el programa recupera el texto que hemos escrito y los manda usando DataOutputStream al Socket con los argumentos que le hayamos indicado
					 */
					
			//		flujo_salida.close(); //cerramos el flujo de datos
					
					
					
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					System.out.println("Error en la conexión con el servidor: " +e1.getMessage());
				}	
			}
			
		}
			
			
			
	
		
		/*
		 * NECESITAMOS QUE NUESTRA APLICACIÓN CLIENTE ESTÉ PERMANENTEMENTE A LA ESUCHA PARA PODER RECIBIR LOS MENSAJES QUE LE MANDA EL SERVIDOR :D (hilos)
		 */
		@Override
		public void run() {
			
			try {
				
				ServerSocket servidor_cliente = new ServerSocket(9090); //Le indicamos que abra el puerto 9090 (el que le hemos indicado en el servidor) y esté a la escucha
				
				Socket cliente;
				
				PaqueteEnvio paquete_recibido;
				
				while(true) {
					
					cliente = servidor_cliente.accept(); //Estará aceptado todas las conexiones que le entren 
					
					ObjectInputStream flujo_entrada = new ObjectInputStream(cliente.getInputStream()); //Creamos un flujo de entrada capaz de transportar objetos (un paquete de datos en este caso)
					
					paquete_recibido = (PaqueteEnvio) flujo_entrada.readObject();
					
					
					if (!paquete_recibido.getMensaje().equals("online")) {
						
						campoChat.append("\n" + paquete_recibido.getNick() + ": " + paquete_recibido.getMensaje()); //Mostramos por pantalla
						
					} else {
						
						//campoChat.append("\n" + paquete_recibido.getIps());
						
						ArrayList<String> IpsMenu = new ArrayList<String>();
						
						IpsMenu = paquete_recibido.getIps();
						
						ip.removeAllItems();
						
						for (String z: IpsMenu) {
							ip.addItem(z);
						}
						
					}
					
					
					
				}
				
				
			} catch (Exception e) {
				System.out.println("Error en el run() del cliente: "  +e.getMessage());
			}
			
		}
		
		
		
		private JTextField campo1;
		
		private JLabel nick;
		
		private JComboBox ip;
		
		private JTextArea campoChat;
		
		private JButton miboton;
		
		
	}
	
	
	
	
	/*
	 * Esta clase debe estar serializada para que al enviar el objeto este se convierta en 0 y 1 y se envíe por la red
	 */
	class PaqueteEnvio implements Serializable{
		
		private String nick;
		private String ip;
		private String mensaje;
		
		private ArrayList<String> ips;		
		
				
		//Getters y Setters
		public String getNick() {
			return nick;
		}
		public void setNick(String nick) {
			this.nick = nick;
		}
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public String getMensaje() {
			return mensaje;
		}
		public void setMensaje(String mensaje) {
			this.mensaje = mensaje;
		}
		public ArrayList<String> getIps() {
			return ips;
		}
		public void setIps(ArrayList<String> ips) {
			this.ips = ips;
		}
		

	}


